package demo.xmlimport;

import java.util.Map;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.primavera.PrimaveraException;
import com.primavera.common.value.ObjectId;
import com.primavera.integration.client.RMIURL;
import com.primavera.integration.client.Session;
import com.primavera.integration.client.xml.xmlimporter.ImportOption;
import com.primavera.integration.client.xml.xmlimporter.XMLImporter;
import com.primavera.integration.common.DatabaseInstance;

public class ImportDemoApp
  implements LoginCallback
{
    //~ Static fields/initializers -----------------------------------------------------------------

    //~ Instance fields ----------------------------------------------------------------------------

    private Session session;

    //~ Methods ------------------------------------------------------------------------------------

    public static void main(String[] args)
    {
        WizardFrame lf = new WizardFrame(new ImportDemoApp());
        lf.setVisible(true);
    }

    private String getURL(ConnectionInfo connInfo)
    {
        String sRmiUrl = null;

        if (REMOTE_MODE.equalsIgnoreCase(connInfo.sCallingMode))
        {
            sRmiUrl = RMIURL.getRmiUrl(connInfo.iRMIServiceMode, connInfo.sHost, connInfo.iPort);
        }

        return sRmiUrl;
    }

    public boolean isRemoteModeAvailable()
    {
        return Session.isRemoteModeAvailable();
    }

    public DatabaseInstance[] getDatabaseInstances(ConnectionInfo connInfo)
      throws PrimaveraException
    {
        // Load the available database instances
        return Session.getDatabaseInstances(getURL(connInfo));
    }

    public void login(ConnectionInfo connInfo, LoginInfo loginInfo)
      throws PrimaveraException
    {
        session = Session.login(getURL(connInfo), loginInfo.sDatabaseId, loginInfo.sUserName, loginInfo.sPassword);
    }

    public Map<String, ObjectId> getEPS()
    {
        return BusinessObjectLoader.loadEPS(session);
    }

    public Map<String, ObjectId> getProjects()
    {
        return BusinessObjectLoader.loadProjects(session);
    }

    public void logout()
    {
        session.logout();
    }

    public void runDemo(DemoInfo demoInfo)
    {
        System.out.println("Starting XML Import");
        System.out.println("Input file: " + demoInfo.sInputFile);
        System.out.println("Log file:   " + demoInfo.sLogFile);
        System.out.println();
        System.out.println("Global import option:  " + demoInfo.globalImportOption);
        System.out.println("Project import option: " + demoInfo.projectImportOption);
        System.out.println();

        try
        {
            XMLImporter importer = new XMLImporter(session);
            importer.setLogFile(demoInfo.sLogFile);            
            importer.setLogLevel(Level.ALL);
            
            // Set the import options.  The XMLImporter allows you to control
            // how individual types of business objects are imported, but for
            // the purposes of this demo we will set all global and all project
            // import options with the following two method calls
            importer.setDefaultGlobalImportOption(demoInfo.globalImportOption);
            importer.setDefaultProjectImportOption(demoInfo.projectImportOption);

            // Create a new project under selected EPS node
            if (demoInfo.projectImportMode.equals(ImportOption.CREATE_NEW))
            {
                importer.createNewProject(demoInfo.destObjectId, demoInfo.sInputFile);
            }
            else
            {
                importer.updateExistingProject(demoInfo.destObjectId, demoInfo.sInputFile);
            }

            JOptionPane.showMessageDialog(null, "XML import completed successfully.\n\nLog file: " + demoInfo.sLogFile, "Import Successful", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            System.out.println();
            session.logout();
        }

        // Shutdown completely and kill all threads
        System.exit(0);
    }
}
