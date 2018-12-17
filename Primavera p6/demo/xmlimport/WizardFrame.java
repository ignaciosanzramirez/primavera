package demo.xmlimport;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import com.primavera.PrimaveraException;
import com.primavera.common.value.ObjectId;
import com.primavera.integration.client.xml.xmlimporter.ImportOption;
import com.primavera.integration.common.DatabaseInstance;

public class WizardFrame
  extends JFrame
{
    //~ Static fields/initializers -----------------------------------------------------------------

    private static final long serialVersionUID = 1L;

    // First card - connection mode
    private static final String CARD_MODE_INFO = "Mode Info";

    // Second card - login
    private static final String CARD_LOGIN_INFO = "Login Info";

    // Third card - demo-specific
    private static final String CARD_MAIN = "Main";

    // fourth card - choose project import option
    private static final String CARD_PROJ_IMPORT = "Project import option";

    //~ Instance fields ----------------------------------------------------------------------------

    private JPanel pnlCards = new JPanel();
    private List<String> cards = new ArrayList<String>();
    private int iCurrentCard;
    private String sAppTitle;
    private DatabaseInstance[] dbInstances = new DatabaseInstance[0];
    private LoginCallback loginCallback;
    private JTextField txtfldRMIHost = new JTextField("localhost");
    private JTextField txtfldRMIPort;
    private JRadioButton btnLocal = new JRadioButton(new LocalAction());
    private JRadioButton btnRemote = new JRadioButton(new RemoteAction());
    private JComboBox cmbRMISeviceType = new JComboBox();
    private JTextField txtfldUserName = new JTextField();
    private JPasswordField txtfldPassword = new JPasswordField();
    private JComboBox cmbDBInstances = new JComboBox(dbInstances);
    private JTextField txtfldInputFile;
    private JTextField txtfldLogFile;
    private JComboBox cmbGlobalImportOption;
    private JComboBox cmbProjectImportOption;
    private JButton btnBack = new JButton(new BackAction());
    private JButton btnNext = new JButton(new NextAction());
    private JRadioButton btnCreateNewProj = new JRadioButton(new NewProjAction());
    private JRadioButton btnUpdateProj = new JRadioButton(new UpdateProjAction());
    private JComboBox cmbEPSId = new JComboBox();
    private JComboBox cmbProjectId = new JComboBox();
    private final JButton btnCancel = new JButton(new CancelAction());
    private LoginCallback.DemoInfo importInfo;
    private Map<String, ObjectId> epsMap;
    private Map<String, ObjectId> projectMap;

    //~ Constructors -------------------------------------------------------------------------------

    WizardFrame(LoginCallback loginCallback)
    {
        sAppTitle = "XML Import Demo";
        setTitle(sAppTitle);
        setResizable(false);
        this.loginCallback = loginCallback;
        cards.add(CARD_MODE_INFO);
        cards.add(CARD_LOGIN_INFO);
        cards.add(CARD_MAIN);
        cards.add(CARD_PROJ_IMPORT);
        initComponents();
    }

    //~ Methods ------------------------------------------------------------------------------------

    // Enable or disable all the RMI related input fields
    private void enableRMIFields(boolean bEnabled)
    {
        txtfldRMIHost.setEnabled(bEnabled);
        txtfldRMIPort.setEnabled(bEnabled);
        cmbRMISeviceType.setEnabled(bEnabled);
    }

    private void initComponents()
    {
        Container cp = getContentPane();
        pnlCards = new JPanel();
        pnlCards.setLayout(new CardLayout());
        pnlCards.setPreferredSize(new Dimension(460, 220));
        pnlCards.setMinimumSize(new Dimension(460, 220));

        //  *** CARD 0 ***
        JPanel pnlCard0Center = new JPanel();
        pnlCard0Center.setLayout(new BoxLayout(pnlCard0Center, BoxLayout.Y_AXIS));
        pnlCard0Center.setBorder(new EmptyBorder(new Insets(10, 0, 0, 0)));

        JPanel pnlMode = new JPanel();
        ((FlowLayout)pnlMode.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlMode.setBorder(new EmptyBorder(new Insets(0, 5, 0, 0)));
        pnlMode.add(new JLabel("Select the mode of operation:"));
        pnlCard0Center.add(pnlMode);

        JPanel pnlCallingMode = new JPanel();
        pnlCallingMode.setLayout(new FlowLayout(FlowLayout.LEFT));
        pnlCallingMode.setBorder(new EmptyBorder(new Insets(5, 5, 5, 10)));

        JLabel lblCallingMode = new JLabel("Calling mode:");
        pnlCallingMode.add(lblCallingMode);

        ButtonGroup btngrpCallingMode = new ButtonGroup();
        btngrpCallingMode.add(btnLocal);
        btnLocal.setMnemonic('L');
        btngrpCallingMode.add(btnRemote);
        btnRemote.setMnemonic('R');
        pnlCallingMode.add(btnRemote);
        pnlCallingMode.add(btnLocal);
        pnlCard0Center.add(pnlCallingMode);

        JPanel pnlServerHost = new JPanel();
        pnlServerHost.setLayout(new BoxLayout(pnlServerHost, BoxLayout.X_AXIS));
        pnlServerHost.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblHost = new JLabel("RMI server host:");
        lblHost.setDisplayedMnemonic('H');
        pnlServerHost.add(lblHost);
        pnlServerHost.add(Box.createHorizontalStrut(5));
        lblHost.setLabelFor(txtfldRMIHost);
        pnlServerHost.add(txtfldRMIHost);
        pnlCard0Center.add(pnlServerHost);

        JPanel pnlServerPort = new JPanel();
        pnlServerPort.setLayout(new BoxLayout(pnlServerPort, BoxLayout.X_AXIS));
        pnlServerPort.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblPort = new JLabel("RMI server port:");
        lblPort.setDisplayedMnemonic('P');
        pnlServerPort.add(lblPort);
        pnlServerPort.add(Box.createHorizontalStrut(5));
        txtfldRMIPort = new JTextField(new LimitedStyledDocument(5, true), "9099", 0);
        lblPort.setLabelFor(txtfldRMIPort);
        pnlServerPort.add(txtfldRMIPort);
        pnlCard0Center.add(pnlServerPort);

        JPanel pnlServiceType = new JPanel();
        pnlServiceType.setLayout(new BoxLayout(pnlServiceType, BoxLayout.X_AXIS));
        pnlServiceType.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblRMIServiceMode = new JLabel("RMI service type:");
        lblRMIServiceMode.setDisplayedMnemonic('T');
        pnlServiceType.add(lblRMIServiceMode);
        pnlServiceType.add(Box.createHorizontalStrut(5));
        lblRMIServiceMode.setLabelFor(cmbRMISeviceType);
        cmbRMISeviceType.addItem("Standard");
        cmbRMISeviceType.addItem("Compression");
        cmbRMISeviceType.addItem("SSL");
        cmbRMISeviceType.setSelectedItem("Standard");
        pnlServiceType.add(cmbRMISeviceType);
        pnlCard0Center.add(pnlServiceType);
        pnlCard0Center.add(Box.createVerticalStrut(30));
        // Make labels the same size
        sizeUniformly(lblRMIServiceMode, new JComponent[] {lblCallingMode, lblHost, lblPort});
        pnlCards.add(pnlCard0Center, CARD_MODE_INFO);

        //  *** CARD 1 ***
        JPanel pnlCard1Center = new JPanel();
        pnlCard1Center.setLayout(new BoxLayout(pnlCard1Center, BoxLayout.Y_AXIS));
        pnlCard1Center.setBorder(new EmptyBorder(new Insets(10, 0, 0, 0)));

        JPanel pnlLogin = new JPanel();
        ((FlowLayout)pnlLogin.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlLogin.setBorder(new EmptyBorder(new Insets(0, 5, 0, 0)));
        pnlLogin.add(new JLabel("Enter your login information:"));
        pnlCard1Center.add(pnlLogin);

        JPanel pnlUserName = new JPanel();
        pnlUserName.setLayout(new BoxLayout(pnlUserName, BoxLayout.X_AXIS));
        pnlUserName.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblUserName = new JLabel("User name:");
        lblUserName.setDisplayedMnemonic('U');
        pnlUserName.add(lblUserName);
        pnlUserName.add(Box.createHorizontalStrut(5));
        lblUserName.setLabelFor(txtfldUserName);
        pnlUserName.add(txtfldUserName);
        pnlCard1Center.add(pnlUserName);

        JPanel pnlPassword = new JPanel();
        pnlPassword.setLayout(new BoxLayout(pnlPassword, BoxLayout.X_AXIS));
        pnlPassword.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setDisplayedMnemonic('P');
        pnlPassword.add(lblPassword);
        pnlPassword.add(Box.createHorizontalStrut(5));
        lblPassword.setLabelFor(txtfldPassword);
        pnlPassword.add(txtfldPassword);
        pnlCard1Center.add(pnlPassword);

        JPanel pnlDBInstance = new JPanel();
        pnlDBInstance.setLayout(new BoxLayout(pnlDBInstance, BoxLayout.X_AXIS));
        pnlDBInstance.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblDBInstance = new JLabel("Database:");
        lblDBInstance.setDisplayedMnemonic('D');
        pnlDBInstance.add(lblDBInstance);
        pnlDBInstance.add(Box.createHorizontalStrut(5));
        lblDBInstance.setLabelFor(cmbDBInstances);
        pnlDBInstance.add(cmbDBInstances);
        pnlCard1Center.add(pnlDBInstance);
        pnlCard1Center.add(Box.createVerticalStrut(78));
        // Make labels the same size
        sizeUniformly(lblUserName, new JComponent[] {lblPassword, lblDBInstance});
        pnlCards.add(pnlCard1Center, CARD_LOGIN_INFO);

        //  *** CARD 2 ***
        JPanel pnlCard2Center = new JPanel();
        pnlCard2Center.setLayout(new BoxLayout(pnlCard2Center, BoxLayout.Y_AXIS));
        pnlCard2Center.setBorder(new EmptyBorder(new Insets(10, 0, 0, 0)));

        JPanel pnlInputFile = new JPanel();
        pnlInputFile.setLayout(new BoxLayout(pnlInputFile, BoxLayout.X_AXIS));
        pnlInputFile.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblInputFile = new JLabel("XML Input file: ");
        lblInputFile.setDisplayedMnemonic('I');
        pnlInputFile.add(lblInputFile, BorderLayout.WEST);
        pnlInputFile.add(Box.createHorizontalStrut(5));
        txtfldInputFile = new JTextField();
        lblInputFile.setLabelFor(txtfldInputFile);
        pnlInputFile.add(txtfldInputFile, BorderLayout.CENTER);
        pnlInputFile.add(Box.createHorizontalStrut(5));

        JButton btnInputBrowse = new JButton(new InputBrowseAction());
        btnInputBrowse.setMnemonic('R');
        pnlInputFile.add(btnInputBrowse);
        pnlCard2Center.add(pnlInputFile);

        JPanel pnlLogFile = new JPanel();
        pnlLogFile.setLayout(new BoxLayout(pnlLogFile, BoxLayout.X_AXIS));
        pnlLogFile.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblLogFile = new JLabel("Log output file: ");
        lblLogFile.setDisplayedMnemonic('L');
        pnlLogFile.add(lblLogFile, BorderLayout.WEST);
        pnlLogFile.add(Box.createHorizontalStrut(5));
        txtfldLogFile = new JTextField();
        lblLogFile.setLabelFor(txtfldLogFile);
        pnlLogFile.add(txtfldLogFile, BorderLayout.CENTER);
        pnlLogFile.add(Box.createHorizontalStrut(5));

        JButton btnLogBrowse = new JButton(new LogBrowseAction());
        btnLogBrowse.setMnemonic('O');
        pnlLogFile.add(btnLogBrowse);
        pnlCard2Center.add(pnlLogFile);

        JPanel pnlImportOption = new JPanel();
        ((FlowLayout)pnlImportOption.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlImportOption.setBorder(new EmptyBorder(new Insets(10, 5, 0, 0)));
        pnlImportOption.add(new JLabel("Specify the XML import options:"));
        pnlCard2Center.add(pnlImportOption);

        JPanel pnlGlobalImportOption = new JPanel();
        pnlGlobalImportOption.setLayout(new BoxLayout(pnlGlobalImportOption, BoxLayout.X_AXIS));
        pnlGlobalImportOption.setBorder(new EmptyBorder(new Insets(5, 10, 5, 5)));

        JLabel lblGlobalImportOption = new JLabel("Global: ");
        lblGlobalImportOption.setDisplayedMnemonic('G');
        pnlGlobalImportOption.add(lblGlobalImportOption, BorderLayout.WEST);
        pnlGlobalImportOption.add(Box.createHorizontalStrut(5));
        cmbGlobalImportOption = new JComboBox(new Object[]
                {
                    ImportOption.CREATE_NEW, ImportOption.KEEP_EXISTING,
                    ImportOption.UPDATE_EXISTING, ImportOption.DO_NOT_IMPORT
                });
        cmbGlobalImportOption.setSelectedItem(ImportOption.KEEP_EXISTING);
        lblGlobalImportOption.setLabelFor(cmbGlobalImportOption);
        pnlGlobalImportOption.add(cmbGlobalImportOption, BorderLayout.CENTER);
        pnlGlobalImportOption.add(Box.createHorizontalStrut(5));
        pnlCard2Center.add(pnlGlobalImportOption);

        JPanel pnlProjectImportOption = new JPanel();
        pnlProjectImportOption.setLayout(new BoxLayout(pnlProjectImportOption, BoxLayout.X_AXIS));
        pnlProjectImportOption.setBorder(new EmptyBorder(new Insets(5, 10, 5, 5)));

        JLabel lblProjectImportOption = new JLabel("Project-specific: ");
        lblProjectImportOption.setDisplayedMnemonic('P');
        pnlProjectImportOption.add(lblProjectImportOption, BorderLayout.WEST);
        pnlProjectImportOption.add(Box.createHorizontalStrut(5));
        cmbProjectImportOption = new JComboBox(new Object[]
                {
                    ImportOption.KEEP_EXISTING, ImportOption.UPDATE_EXISTING,
                    ImportOption.DO_NOT_IMPORT
                });
        cmbProjectImportOption.setSelectedItem(ImportOption.KEEP_EXISTING);
        lblProjectImportOption.setLabelFor(cmbProjectImportOption);
        pnlProjectImportOption.add(cmbProjectImportOption, BorderLayout.CENTER);
        pnlProjectImportOption.add(Box.createHorizontalStrut(5));
        pnlCard2Center.add(pnlProjectImportOption);
        pnlCard2Center.add(Box.createVerticalStrut(40));
        // Make labels the same size
        sizeUniformly(lblProjectImportOption, new JComponent[]
            {
                lblInputFile, lblLogFile, lblGlobalImportOption
            });
        pnlCards.add(pnlCard2Center, CARD_MAIN);

        //  *** CARD 3 ***
        JPanel pnlCard3Center = new JPanel();
        pnlCard3Center.setLayout(new BoxLayout(pnlCard3Center, BoxLayout.Y_AXIS));
        pnlCard3Center.setBorder(new EmptyBorder(new Insets(10, 0, 0, 0)));

        JPanel pnlImport = new JPanel();
        ((FlowLayout)pnlImport.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlImport.setBorder(new EmptyBorder(new Insets(0, 5, 0, 0)));
        pnlImport.add(new JLabel("Select import option for project:"));
        pnlCard3Center.add(pnlImport);

        ButtonGroup btngrpImportOptions = new ButtonGroup();
        btngrpImportOptions.add(btnCreateNewProj);
        btnCreateNewProj.setMnemonic('C');
        btngrpImportOptions.add(btnUpdateProj);
        btnUpdateProj.setMnemonic('U');

        JPanel pnlCreateNew = new JPanel();
        ((FlowLayout)pnlCreateNew.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlCreateNew.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        pnlCreateNew.add(btnCreateNewProj);
        pnlCard3Center.add(pnlCreateNew);

        JPanel pnlEPS = new JPanel();
        pnlEPS.setLayout(new BoxLayout(pnlEPS, BoxLayout.X_AXIS));
        pnlEPS.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        pnlEPS.add(Box.createHorizontalStrut(25));

        JLabel lblEPS = new JLabel("EPS:");
        lblEPS.setDisplayedMnemonic('E');
        pnlEPS.add(lblEPS);
        pnlEPS.add(Box.createHorizontalStrut(5));
        lblEPS.setLabelFor(cmbEPSId);
        pnlEPS.add(cmbEPSId);
        pnlCard3Center.add(pnlEPS);

        JPanel pnlUpdate = new JPanel();
        ((FlowLayout)pnlUpdate.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlUpdate.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        pnlUpdate.add(btnUpdateProj);
        pnlCard3Center.add(pnlUpdate);

        JPanel pnlProject = new JPanel();
        pnlProject.setLayout(new BoxLayout(pnlProject, BoxLayout.X_AXIS));
        pnlProject.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));
        pnlProject.add(Box.createHorizontalStrut(25));

        JLabel lblProject = new JLabel("Project:");
        lblProject.setDisplayedMnemonic('P');
        pnlProject.add(lblProject);
        pnlProject.add(Box.createHorizontalStrut(5));
        lblProject.setLabelFor(cmbProjectId);
        pnlProject.add(cmbProjectId);
        pnlCard3Center.add(pnlProject);
        pnlCard3Center.add(Box.createVerticalStrut(30));
        btnCreateNewProj.setSelected(true);
        cmbProjectId.setEnabled(false);
        // Make labels the same size
        sizeUniformly(lblProject, new JComponent[] {lblEPS});
        pnlCards.add(pnlCard3Center, CARD_PROJ_IMPORT);
        cp.add(pnlCards, BorderLayout.NORTH);

        //  General controls ( not on any card )
        JPanel pnlSouth = new JPanel();
        pnlSouth.setBorder(new EmptyBorder(0, 5, 5, 5));
        ((FlowLayout)pnlSouth.getLayout()).setAlignment(FlowLayout.RIGHT);
        btnBack.setMnemonic('B');
        btnBack.setEnabled(false);
        pnlSouth.add(btnBack);
        btnNext.setMnemonic('N');
        pnlSouth.add(btnNext);
        getRootPane().setDefaultButton(btnNext);
        pnlSouth.add(btnCancel);
        cp.add(pnlSouth, BorderLayout.SOUTH);
        getRootPane().setDefaultButton(btnNext);
        // Make Back and Next buttons the same size as Cancel
        sizeUniformly(btnCancel, new JComponent[] {btnBack, btnNext});
        addWindowListener(new WindowAdapter()
            {
                public void windowClosing(WindowEvent we)
                {
                    shutdown(-1);
                }
            });
        addKeyListener(new KeyAdapter()
            {
                public void keyPressed(KeyEvent e)
                {
                    if (e.getKeyCode() == KeyEvent.VK_ENTER)
                    {
                        if (btnCancel.hasFocus())
                        {
                            processCancel();
                        }
                    }
                    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
                    {
                        processCancel();
                    }
                }
            });

        if (loginCallback.isRemoteModeAvailable())
        {
            btnRemote.setSelected(true);
        }
        else
        {
            btnLocal.setSelected(true);
            btnRemote.setEnabled(false);
            enableRMIFields(false);
        }

        pack();
        centerOnScreen(this);
    }

    private void populateEPSandProjectList()
    {
        // load only once
        if (epsMap == null)
        {
            epsMap = loginCallback.getEPS();

            if (!epsMap.isEmpty())
            {
                Set<String> names = epsMap.keySet();

                for (String name : names)
                {
                    cmbEPSId.addItem(name);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "No EPS object found. Please make sure you have access to EPS.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        if (projectMap == null)
        {
            projectMap = loginCallback.getProjects();

            if (!projectMap.isEmpty())
            {
                Set<String> names = projectMap.keySet();

                for (String name : names)
                {
                    cmbProjectId.addItem(name);
                }
            }
            else
            {
                JOptionPane.showMessageDialog(this, "No Project object found. Please make sure you have access to Project.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void shutdown(int iValue)
    {
        dispose();
        System.exit(iValue);
    }

    private void centerOnScreen(Container win)
    {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        Rectangle bounds = gd.getDefaultConfiguration().getBounds();
        Dimension winDim = win.getSize();
        Point loc = new Point((bounds.width - winDim.width) / 2, (bounds.height - winDim.height) / 2);
        win.setLocation(loc);
    }

    private LoginCallback.ConnectionInfo getConnectionInfo()
    {
        LoginCallback.ConnectionInfo connInfo = new LoginCallback.ConnectionInfo();

        if (btnRemote.isSelected())
        {
            connInfo.sCallingMode = LoginCallback.REMOTE_MODE;
            connInfo.sHost = txtfldRMIHost.getText().trim();

            if (connInfo.sHost.length() == 0)
            {
                JOptionPane.showMessageDialog(this, "RMI server host may not be blank.", "Error", JOptionPane.ERROR_MESSAGE);
                txtfldRMIHost.requestFocus();

                return null;
            }

            String sPort = txtfldRMIPort.getText().trim();

            if (sPort.length() == 0)
            {
                JOptionPane.showMessageDialog(this, "RMI server port may not be blank.", "Error", JOptionPane.ERROR_MESSAGE);
                txtfldRMIPort.requestFocus();

                return null;
            }
            else
            {
                try
                {
                    connInfo.iPort = Integer.parseInt(sPort);

                    if ((connInfo.iPort < 0) || (connInfo.iPort > 65535))
                    {
                        JOptionPane.showMessageDialog(this, "RMI server port must be in the range from 0 to 65535.", "Error", JOptionPane.ERROR_MESSAGE);
                        txtfldRMIPort.requestFocus();

                        return null;
                    }
                }
                catch (NumberFormatException ex)
                {
                    JOptionPane.showMessageDialog(this, "RMI server port is not valid.", "Error", JOptionPane.ERROR_MESSAGE);
                    txtfldRMIPort.requestFocus();

                    return null;
                }
            }

            connInfo.iRMIServiceMode = cmbRMISeviceType.getSelectedIndex() + 1;
        }
        else
        {
            connInfo.sCallingMode = LoginCallback.LOCAL_MODE;
        }

        return connInfo;
    }

    private LoginCallback.LoginInfo getLoginInfo()
    {
        LoginCallback.LoginInfo loginInfo = new LoginCallback.LoginInfo();
        loginInfo.sUserName = txtfldUserName.getText().trim();

        if (loginInfo.sUserName.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "User name may not be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldUserName.requestFocus();

            return null;
        }

        loginInfo.sPassword = new String(txtfldPassword.getPassword()).trim();

        DatabaseInstance dbi = (DatabaseInstance)cmbDBInstances.getSelectedItem();

        if (dbi == null)
        {
            JOptionPane.showMessageDialog(this, "No database instance is selected", "Error", JOptionPane.ERROR_MESSAGE);
            cmbDBInstances.requestFocus();

            return null;
        }

        loginInfo.sDatabaseId = dbi.getDatabaseId();

        return loginInfo;
    }

    private LoginCallback.DemoInfo getDemoInfo()
    {
        LoginCallback.DemoInfo demoInfo = new LoginCallback.DemoInfo();
        demoInfo.sInputFile = txtfldInputFile.getText().trim();

        if (demoInfo.sInputFile.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Input file must be specified.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldInputFile.requestFocus();

            return null;
        }

        File fInput = new File(demoInfo.sInputFile);

        if (!fInput.exists() || !fInput.isFile())
        {
            JOptionPane.showMessageDialog(this, "Input file does not exist.  Please specify a valid input file.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldInputFile.requestFocus();

            return null;
        }

        demoInfo.sLogFile = txtfldLogFile.getText().trim();

        if (demoInfo.sLogFile.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Log file must be specified.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldLogFile.requestFocus();

            return null;
        }

        if (demoInfo.sInputFile.equals(demoInfo.sLogFile))
        {
            JOptionPane.showMessageDialog(this, "Input file and log file may not be the same.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldLogFile.requestFocus();

            return null;
        }

        File fLog = new File(demoInfo.sLogFile);

        if (fLog.exists())
        {        	
        	if (!fLog.isFile())
        	{
        		JOptionPane.showMessageDialog(this, "Log file is not a valid file.", "Not a valid file", JOptionPane.ERROR_MESSAGE);
        		txtfldLogFile.requestFocus();
        		return null;
        	}
        	
            int iResponse = JOptionPane.showConfirmDialog(this, "Log file already exists.  Overwrite?", "File Exists", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

            if (iResponse == JOptionPane.NO_OPTION)
            {
                txtfldLogFile.requestFocus();

                return null;
            }
        }

        demoInfo.globalImportOption = (ImportOption)cmbGlobalImportOption.getSelectedItem();
        demoInfo.projectImportOption = (ImportOption)cmbProjectImportOption.getSelectedItem();

        return demoInfo;
    }

    private void processCancel()
    {
        if (JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?", sAppTitle, JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION)
        {
            shutdown(-1);
        }
    }

    private static void sizeUniformly(JComponent mainComponent, JComponent[] otherComponents)
    {
        Dimension dim = mainComponent.getPreferredSize();

        for (int i = 0; i < otherComponents.length; i++)
        {
            JComponent comp = otherComponents[i];
            comp.setMinimumSize(dim);
            comp.setMaximumSize(dim);
            comp.setPreferredSize(dim);
        }
    }

    //~ Inner Classes ------------------------------------------------------------------------------

    class BackAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        BackAction()
        {
            super("Back");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            if (iCurrentCard == 1)
            {
                btnBack.setEnabled(false);
            }
            else if (iCurrentCard == 2)
            {
                loginCallback.logout();
            }

            iCurrentCard--;

            CardLayout cl = (CardLayout)(pnlCards.getLayout());
            cl.show(pnlCards, (String)cards.get(iCurrentCard));
        }
    }

    class CancelAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        CancelAction()
        {
            super("Cancel");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            processCancel();
        }
    }

    class InputBrowseAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        InputBrowseAction()
        {
            super("Browse...");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            JFileChooser fc = new JFileChooser(txtfldInputFile.getText());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            fc.setFileFilter(new XMLFileFilter());

            if (fc.showOpenDialog(WizardFrame.this) != JFileChooser.APPROVE_OPTION)
            {
                return;
            }

            txtfldInputFile.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    static class LimitedStyledDocument
      extends DefaultStyledDocument
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Instance fields ------------------------------------------------------------------------

        private int iMaxCharacters;
        private boolean bNumericOnly;

        //~ Constructors ---------------------------------------------------------------------------

        public LimitedStyledDocument(int iMaxChars, boolean bNumericOnly)
        {
            iMaxCharacters = iMaxChars;
            this.bNumericOnly = bNumericOnly;
        }

        public LimitedStyledDocument(int iMaxChars)
        {
            iMaxCharacters = iMaxChars;
            bNumericOnly = false;
        }

        //~ Methods --------------------------------------------------------------------------------

        public void insertString(int offs, String str, AttributeSet a)
          throws BadLocationException
        {
            if (bNumericOnly)
            {
                try
                {
                    Integer.parseInt(str);
                }
                catch (NumberFormatException nfe)
                {
                    Toolkit.getDefaultToolkit().beep();

                    return;
                }
            }

            // Limit the length which can be entered
            if ((getLength() + str.length()) <= iMaxCharacters)
            {
                super.insertString(offs, str, a);
            }
            else
            {
                Toolkit.getDefaultToolkit().beep();
            }
        }
    }

    class LocalAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        LocalAction()
        {
            super("Local");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            enableRMIFields(false);
        }
    }

    class LogBrowseAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        LogBrowseAction()
        {
            super("Browse...");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            JFileChooser fc = new JFileChooser(txtfldLogFile.getText());
            fc.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fc.showSaveDialog(WizardFrame.this) != JFileChooser.APPROVE_OPTION)
            {
                return;
            }

            txtfldLogFile.setText(fc.getSelectedFile().getAbsolutePath());
        }
    }

    class NewProjAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        NewProjAction()
        {
            super("Create New");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            cmbEPSId.setEnabled(true);
            cmbProjectId.setEnabled(false);
        }
    }

    class NextAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        NextAction()
        {
            super("Next");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            if (iCurrentCard == 0)
            {
                // Leaving first card; validate connection
                try
                {
                    LoginCallback.ConnectionInfo connInfo = getConnectionInfo();

                    if (connInfo == null)
                    {
                        return;
                    }

                    dbInstances = loginCallback.getDatabaseInstances(connInfo);

                    if ((dbInstances == null) || (dbInstances.length == 0))
                    {
                        JOptionPane.showMessageDialog(null, "No database instances were found", "Error", JOptionPane.ERROR_MESSAGE);

                        return;
                    }

                    cmbDBInstances.removeAllItems();

                    for (int i = 0; i < dbInstances.length; i++)
                    {
                        cmbDBInstances.addItem(dbInstances[i]);
                    }
                }
                catch (PrimaveraException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "Unable to load database instances.", "Error", JOptionPane.ERROR_MESSAGE);

                    return;
                }
            }
            else if (iCurrentCard == 1)
            {
                // Leaving second card; validate login
                LoginCallback.ConnectionInfo connInfo = getConnectionInfo();

                if (connInfo == null)
                {
                    return;
                }

                LoginCallback.LoginInfo loginInfo = getLoginInfo();

                if (loginInfo == null)
                {
                    return;
                }

                try
                {
                    loginCallback.login(connInfo, loginInfo);
                }
                catch (PrimaveraException e)
                {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);

                    return;
                }
            }
            else if (iCurrentCard == 2)
            {
                importInfo = getDemoInfo();

                if (importInfo == null)
                {
                    return;
                }

                // get EPS and project ids for create new and update existing
                populateEPSandProjectList();
            }
            else
            {
                // Leaving fourth card; get additional information
                // get project import option and the destination object id. Store it in DemoIfo class
                if (btnCreateNewProj.isSelected())
                {
                    importInfo.projectImportMode = ImportOption.CREATE_NEW;
                    importInfo.destObjectId = epsMap.get((String)cmbEPSId.getSelectedItem());
                }
                else
                {
                    importInfo.projectImportMode = ImportOption.UPDATE_EXISTING;
                    importInfo.destObjectId = projectMap.get((String)cmbProjectId.getSelectedItem());
                }

                dispose();
                loginCallback.runDemo(importInfo);

                return;
            }

            iCurrentCard++;
            btnBack.setEnabled(true);

            CardLayout cl = (CardLayout)(pnlCards.getLayout());
            cl.show(pnlCards, (String)cards.get(iCurrentCard));
        }
    }

    class RemoteAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        RemoteAction()
        {
            super("Remote");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            enableRMIFields(true);
        }
    }

    class UpdateProjAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        UpdateProjAction()
        {
            super("Update Existing");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            cmbEPSId.setEnabled(false);
            cmbProjectId.setEnabled(true);
        }
    }

    static class XMLFileFilter
      extends javax.swing.filechooser.FileFilter
      implements java.io.FileFilter
    {
        //~ Methods --------------------------------------------------------------------------------

        public boolean accept(File f)
        {
            if (f.isDirectory())
            {
                return true;
            }
            else
            {
                return f.getName().trim().toLowerCase().endsWith(".xml");
            }
        }

        public String getDescription()
        {
            return "XML Files (*.xml)";
        }
    }
}
