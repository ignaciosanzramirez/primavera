package demo.xmlexport;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;

import javax.swing.JOptionPane;

import com.primavera.PrimaveraException;
import com.primavera.ServerException;
import com.primavera.common.value.ObjectId;
import com.primavera.integration.client.GlobalObjectManager;
import com.primavera.integration.client.RMIURL;
import com.primavera.integration.client.Session;
import com.primavera.integration.client.bo.BOIterator;
import com.primavera.integration.client.bo.BusinessObjectException;
import com.primavera.integration.client.bo.object.Project;
import com.primavera.integration.client.xml.xmlexporter.XMLExporter;
import com.primavera.integration.client.xml.xmlexporter.XMLExporterEvent;
import com.primavera.integration.client.xml.xmlexporter.XMLExporterException;
import com.primavera.integration.client.xml.xmlexporter.XMLExporterListener;
import com.primavera.integration.client.xml.xmlexporter.XMLExporterParams;
import com.primavera.integration.common.DatabaseInstance;
import com.primavera.integration.network.NetworkException;

public class ExportDemoApp
  implements LoginCallback
{
    //~ Instance fields ----------------------------------------------------------------------------

    private Session session;

    //~ Methods ------------------------------------------------------------------------------------

    public static void main(String[] args)
    {
        WizardFrame lf = new WizardFrame(new ExportDemoApp());
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

    public void logout()
    {
        session.logout();
    }

    public void runDemo(DemoInfo demoInfo)
    {
        try
        {
            XMLExporter exporter = new XMLExporter(session);
            GlobalObjectManager gom = session.getGlobalObjectManager();

            // Load the first 10 projects with the fields ObjectId, ProjectId,
            // and Name
            int iNumProjects = 0;
            BOIterator<Project> boi = gom.loadProjects(new String[] {"ObjectId", "Id", "Name"}, null, null);

            while (boi.hasNext() && (iNumProjects < 10))
            {
                // Get next project in result set
                Project project = boi.next();

                // Display status message
                String sId = project.getId();
                System.out.println("Exporting project: " + sId + " - " + project.getName());
                // Process this project
                processProject(exporter, demoInfo.sOutputDir, project.getObjectId(), sId, demoInfo.hsClassesToSkip);
                iNumProjects++;
            }

            JOptionPane.showMessageDialog(null, iNumProjects + " projects were successfully exported to " + demoInfo.sOutputDir, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            session.logout();
        }

        // Shutdown completely and kill all threads
        System.exit(0);
    }

    private void processProject(XMLExporter exporter, String sPath, ObjectId objectId, String sProjectId, Set<String> hsClassesToSkip)
    {
        FileOutputStream fos = null;

        // Create the full path in the format:  <Path>\<project ID>.xml
        StringBuilder sbFile = new StringBuilder(sPath);
        sbFile.append(sProjectId);
        sbFile.append(".xml");

        try
        {
            // Create a new print stream for this output file.  Note that the
            // encoding must match that in the XMLExporter.  The default is
            // set to "UTF-8".
            fos = new FileOutputStream(sbFile.toString());
            // Use the XMLExporter to export the project to the print stream
            exporter.exportFullProject(fos, objectId, Project.getMainFields(), new MyExportListener(hsClassesToSkip));
        }
        catch (ServerException e)
        {
            System.out.println("Server exception occurred while processing project: " + sProjectId);
            System.out.println();
            e.printStackTrace();
        }
        catch (NetworkException e)
        {
            System.out.println("Network exception occurred while processing project: " + sProjectId);
            System.out.println();
            e.printStackTrace();
        }
        catch (BusinessObjectException e)
        {
            System.out.println("Business object exception occurred while processing project: " + sProjectId);
            System.out.println();
            e.printStackTrace();
        }
        catch (XMLExporterException e)
        {
            String msg = e.getMessage();
            if(msg.indexOf("Cannot export") >= 0)
            {
                System.out.println(sProjectId + " - " + msg);
                System.out.println();
            }
            else
            {
                System.out.println("Export exception occurred while processing project: " + sProjectId);
                System.out.println();
                e.printStackTrace();
            }
        }
        catch (IOException e)
        {
            System.out.println("Error creating output file: " + sbFile.toString());
            System.out.println();
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (fos != null)
                {
                    fos.flush();
                    fos.close();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}


class MyExportListener
  implements XMLExporterListener
{
    //~ Instance fields ----------------------------------------------------------------------------

    private Set<String> hsClassesToSkip;

    //~ Constructors -------------------------------------------------------------------------------

    public MyExportListener(Set<String> hsClassesToSkip)
    {
        this.hsClassesToSkip = hsClassesToSkip;
    }

    //~ Methods ------------------------------------------------------------------------------------

    public void businessObjectExporting(XMLExporterEvent e)
    {
        XMLExporterParams bop = e.getParameters();
        String sClassName = bop.getClassName();

        // Don't export this class if the user specified to skip it
        if (hsClassesToSkip.contains(sClassName))
        {
            bop.setIncluded(false);
        }
    }
}
