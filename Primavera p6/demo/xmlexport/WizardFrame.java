package demo.xmlexport;

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
import java.util.HashSet;
import java.util.List;

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
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;

import com.primavera.PrimaveraException;
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
    private JTextField txtfldOutputDir;
    private JList listClassesToSkip;
    private JButton btnBack = new JButton(new BackAction());
    private JButton btnNext = new JButton(new NextAction());
    private final JButton btnCancel = new JButton(new CancelAction());

    //~ Constructors -------------------------------------------------------------------------------

    WizardFrame(LoginCallback loginCallback)
    {
        sAppTitle = "XML Export Demo";
        setTitle(sAppTitle);
        setResizable(false);
        this.loginCallback = loginCallback;
        cards.add(CARD_MODE_INFO);
        cards.add(CARD_LOGIN_INFO);
        cards.add(CARD_MAIN);
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

        JPanel pnlPanel2Txt1 = new JPanel();
        ((FlowLayout)pnlPanel2Txt1.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlPanel2Txt1.setBorder(new EmptyBorder(new Insets(0, 5, 0, 0)));
        pnlPanel2Txt1.add(new JLabel("Enter the output directory for the XML files:"));
        pnlCard2Center.add(pnlPanel2Txt1);

        JPanel pnlOutputDir = new JPanel();
        pnlOutputDir.setLayout(new BoxLayout(pnlOutputDir, BoxLayout.X_AXIS));
        pnlOutputDir.setBorder(new EmptyBorder(new Insets(5, 10, 5, 10)));

        JLabel lblOutputDir = new JLabel("Output directory: ");
        lblOutputDir.setDisplayedMnemonic('O');
        pnlOutputDir.add(lblOutputDir, BorderLayout.WEST);
        pnlOutputDir.add(Box.createHorizontalStrut(5));
        txtfldOutputDir = new JTextField(System.getProperty("user.dir"));
        lblOutputDir.setLabelFor(txtfldOutputDir);
        pnlOutputDir.add(txtfldOutputDir, BorderLayout.CENTER);
        pnlOutputDir.add(Box.createHorizontalStrut(5));

        JButton btnInputBrowse = new JButton(new BrowseAction());
        btnInputBrowse.setMnemonic('R');
        pnlOutputDir.add(btnInputBrowse);
        pnlCard2Center.add(pnlOutputDir);

        JPanel pnlPanel2Txt2 = new JPanel();
        ((FlowLayout)pnlPanel2Txt2.getLayout()).setAlignment(FlowLayout.LEFT);
        pnlPanel2Txt2.setBorder(new EmptyBorder(new Insets(10, 5, 0, 0)));

        JLabel lblSelectClasses = new JLabel("Select any classes you don't wish to export:");
        lblSelectClasses.setDisplayedMnemonic('S');
        pnlPanel2Txt2.add(lblSelectClasses);
        pnlCard2Center.add(pnlPanel2Txt2);

        JPanel pnlListSkipClasses = new JPanel();
        pnlListSkipClasses.setBorder(new EmptyBorder(new Insets(0, 5, 0, 5)));
        listClassesToSkip = new JList(new String[]
                {
                    "ProjectBudgetChangeLog", "ProjectIssue", "ProjectRisk", "WBSNote", "WBSMilestone",
                    "ActivityNote", "ActivityStep"
                });
        lblSelectClasses.setLabelFor(listClassesToSkip);
        listClassesToSkip.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listClassesToSkip.setLayoutOrientation(JList.VERTICAL);
        listClassesToSkip.setVisibleRowCount(3);
        pnlListSkipClasses.add(new JScrollPane(listClassesToSkip));
        pnlCard2Center.add(pnlListSkipClasses);
        pnlCard2Center.add(Box.createVerticalStrut(50));
        pnlCards.add(pnlCard2Center, CARD_MAIN);
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
        // Get output directory
        LoginCallback.DemoInfo demoInfo = new LoginCallback.DemoInfo();
        demoInfo.sOutputDir = txtfldOutputDir.getText().trim();

        if (demoInfo.sOutputDir.length() == 0)
        {
            JOptionPane.showMessageDialog(this, "Output directory may not be blank.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldOutputDir.requestFocus();

            return null;
        }

        File f = new File(demoInfo.sOutputDir);

        if (!f.exists())
        {
            JOptionPane.showMessageDialog(this, "Output directory does not exist.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldOutputDir.requestFocus();

            return null;
        }

        if (!f.isDirectory())
        {
            JOptionPane.showMessageDialog(this, "A directory needs to be specified.", "Error", JOptionPane.ERROR_MESSAGE);
            txtfldOutputDir.requestFocus();

            return null;
        }

        if (demoInfo.sOutputDir.charAt(demoInfo.sOutputDir.length() - 1) != File.separatorChar)
        {
            demoInfo.sOutputDir += File.separatorChar;
        }

        // Get set of classes to skip, if any
        demoInfo.hsClassesToSkip = new HashSet<String>();

        Object[] itemsToSkip = listClassesToSkip.getSelectedValues();

        for (int i = 0; i < itemsToSkip.length; i++)
        {
            demoInfo.hsClassesToSkip.add((String)itemsToSkip[i]);
        }

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
            cl.show(pnlCards, cards.get(iCurrentCard));
        }
    }

    class BrowseAction
      extends AbstractAction
    {
        //~ Static fields/initializers -------------------------------------------------------------

        private static final long serialVersionUID = 1L;

        //~ Constructors ---------------------------------------------------------------------------

        BrowseAction()
        {
            super("Browse...");
        }

        //~ Methods --------------------------------------------------------------------------------

        public void actionPerformed(ActionEvent ae)
        {
            JFileChooser fc = new JFileChooser(txtfldOutputDir.getText());
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fc.showDialog(WizardFrame.this, "Select Directory") != JFileChooser.APPROVE_OPTION)
            {
                return;
            }

            txtfldOutputDir.setText(fc.getSelectedFile().getAbsolutePath());
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
            else
            {
                // Leaving third card; get additional information
                LoginCallback.DemoInfo demoInfo = getDemoInfo();

                if (demoInfo == null)
                {
                    return;
                }

                dispose();
                loginCallback.runDemo(demoInfo);

                return;
            }

            iCurrentCard++;
            btnBack.setEnabled(true);

            CardLayout cl = (CardLayout)(pnlCards.getLayout());
            cl.show(pnlCards, cards.get(iCurrentCard));
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
}
