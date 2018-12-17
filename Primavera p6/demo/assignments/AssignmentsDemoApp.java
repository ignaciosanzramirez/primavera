package demo.assignments;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

import javax.swing.JOptionPane;

import com.primavera.PrimaveraException;
import com.primavera.integration.client.GlobalObjectManager;
import com.primavera.integration.client.RMIURL;
import com.primavera.integration.client.Session;
import com.primavera.integration.client.bo.BOIterator;
import com.primavera.integration.client.bo.object.Resource;
import com.primavera.integration.client.bo.object.ResourceAssignment;
import com.primavera.integration.common.DatabaseInstance;

public class AssignmentsDemoApp
  implements LoginCallback
{
    private Session session;

    public static void main(String[] args)
    {
        WizardFrame lf = new WizardFrame(new AssignmentsDemoApp());
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
        FileOutputStream fos = null;
        PrintStream ps = null;

        try
        {
            File f = new File(demoInfo.sFileName);

            if (f.exists())
            {
                f.delete();
            }

            fos = new FileOutputStream(f);
            ps = new PrintStream(new BufferedOutputStream(fos), false, "UTF-8");
            ps.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            ps.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"");
            ps.println("    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
            ps.println("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">");
            ps.println("<head>");
            ps.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
            ps.println("<title>Integration API Resource Assignment Load Demonstration</title>");
            ps.println("<style type=\"text/css\">");
            ps.println("/*<![CDATA[*/");
            ps.println("<!--");
            ps.print("\tth { font-family: Verdana, Arial, Helvetica, sans-serif; ");
            ps.print("font-size: 11px; font-style: normal; ");
            ps.println("line-height: 14px; width:15%; }");
            ps.println("\tth.wide { width:40%; }");
            ps.print("\ttr { font-family: Verdana, Arial, Helvetica, sans-serif; ");
            ps.print("font-size: 11px; font-style: normal; line-height: 14px; ");
            ps.println("font-weight: normal; }");
            ps.println("\ttr.head { background: #8888ff; }");
            ps.println("\ttr.light { background: #EEEEFF; }");
            ps.println("\ttr.dark { background: #DDDDFF; }");
            ps.print("\th2 { font-family: Verdana, Arial, Helvetica, sans-serif; ");
            ps.print("font-size: 18px; font-style: normal; font-weight: bolder; ");
            ps.println("color: #000000; line-height: 20px; text-align: center; }");
            ps.println("-->");
            ps.println("/*]]>*/");
            ps.println("</style>");
            ps.println("</head>");
            ps.println("<body>");
            ps.println("<h2>Integration API Resource Assignment Load Demonstration</h2>");
            ps.println("<table border=\"1\" width=\"100%\" cellpadding=\"10\">");
            ps.print("<tr class=\"head\">");
            ps.print("<th>Resource ID</th>");
            ps.print("<th>Project ID</th>");
            ps.print("<th>Role ID</th>");
            ps.print("<th>Cost Account</th>");
            ps.print("<th class=\"wide\">Activity Name</th>");
            ps.println("</tr>");

            GlobalObjectManager gom = session.getGlobalObjectManager();

            // Get order by clause for resources
            StringBuilder sbOrderResourcesBy = new StringBuilder();
            sbOrderResourcesBy.append(demoInfo.sOrderResourcesBy);
            sbOrderResourcesBy.append((demoInfo.bAscending) ? " asc" : " desc");

            boolean bLightRow = true;
            int iNumResources = 0;
            BOIterator<Resource> boi = gom.loadResources(new String[] {"Id"}, null, sbOrderResourcesBy.toString());

            // Get order by clause for resource assignments
            StringBuilder sbOrderAssignmentsBy = new StringBuilder();
            sbOrderAssignmentsBy.append(demoInfo.sOrderAssignmentsBy);
            sbOrderAssignmentsBy.append((demoInfo.bAscending) ? " asc" : " desc");

            // Load the first X number of resources, where X is specified by
            // the user
            while (boi.hasNext() && (iNumResources < demoInfo.iMaxResources))
            {
                Resource resource = boi.next();
                String sResourceId = resource.getId();
                System.out.println("Loading assignments for resource " + resource.getId());

                BOIterator<ResourceAssignment> boiAsgn = resource.loadResourceAssignments(new String[]
                    {
                        "ObjectId", "ProjectId", "RoleId", "ActivityName", "CostAccountName"
                    }, null, sbOrderAssignmentsBy.toString());

                while (boiAsgn.hasNext())
                {
                    ResourceAssignment asgn = boiAsgn.next();
                    ps.print("<tr class=\"" + ((bLightRow) ? "light" : "dark") + "\">");
                    ps.print("<td>" + processString(sResourceId) + "</td>");
                    ps.print("<td>" + processString(asgn.getProjectId()) + "</td>");
                    ps.print("<td>" + processString(asgn.getRoleId()) + "</td>");
                    ps.print("<td>" + processString(asgn.getCostAccountName()) + "</td>");
                    ps.print("<td>" + processString(asgn.getActivityName()) + "</td>");
                    ps.println("</tr>");
                    bLightRow = !bLightRow;
                }

                iNumResources++;
            }

            ps.println("</table>");
            ps.print("<p><a href=\"http://validator.w3.org\">");
            ps.print("<img src=\"http://www.w3.org/Icons/valid-xhtml10\"");
            ps.print(" alt=\"Valid XHTML 1.0!\" height=\"31\" width=\"88\" />");
            ps.println("</a></p>");
            ps.println("</body>");
            ps.println("</html>");
            displayResults(iNumResources, demoInfo.sFileName);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            session.logout();

            if (ps != null)
            {
                ps.flush();
                ps.close();
            }
        }

        System.exit(0);
    }

    private String processString(String s)
    {
        if ((s == null) || (s.length() == 0))
        {
            return "&nbsp;";
        }

        if (s.indexOf('&') == -1)
        {
            return s;
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);

            if (c == '&')
            {
                sb.append("&amp;");
            }
            else if (c == '>')
            {
                sb.append("&gt;");
            }
            else if (c == '<')
            {
                sb.append("&lt;");
            }
            else
            {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    private void displayResults(int iNumResources, String sPath)
    {
        String sOS = System.getProperty("os.name").toLowerCase();

        try
        {
            // Check for 95, 98, ME
            if ((sOS.indexOf("windows 9") != -1) || (sOS.indexOf("windows me") != -1))
            {
                // Windows 95, 98, ME
                Runtime.getRuntime().exec("start \"" + sPath + "\"");
            }
            else if (sOS.indexOf("windows ") != -1)
            {
                // Windows NT, 2000, XP, and any future versions
                Runtime.getRuntime().exec("cmd /c \"" + sPath + "\"");
            }
            else
            {
                // Assume UNIX environment
                Runtime.getRuntime().exec("netscape file://" + sPath);
            }
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null, "Assignments for " + iNumResources + " resources were successfully loaded across all projects.\n\n" + "Output file: " + sPath, "Load Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
