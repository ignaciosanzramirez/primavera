package demo.xmlexport;

import java.util.Set;

import com.primavera.PrimaveraException;
public interface LoginCallback
{
    //~ Static fields/initializers -----------------------------------------------------------------

    public static final String REMOTE_MODE = "Remote";
    public static final String LOCAL_MODE = "Local";

    //~ Methods ------------------------------------------------------------------------------------

    public boolean isRemoteModeAvailable();

    public com.primavera.integration.common.DatabaseInstance[] getDatabaseInstances(ConnectionInfo connInfo)
      throws PrimaveraException;

    public void login(ConnectionInfo connInfo, LoginInfo loginInfo)
      throws PrimaveraException;

    public void logout();

    public void runDemo(DemoInfo demoInfo);

    //~ Inner Classes ------------------------------------------------------------------------------

    public static class ConnectionInfo
    {
        //~ Instance fields ------------------------------------------------------------------------

        // Mode of operation: local or remote
        public String sCallingMode = REMOTE_MODE;
  

        // Host for remote mode
        public String sHost;

        // Port for remote mode
        public int iPort;

        // RMI service mode used by remote mode
        public int iRMIServiceMode;
    }

    public static class DemoInfo
    {
        //~ Instance fields ------------------------------------------------------------------------

        // Directory for output files
        public String sOutputDir;

        // Set of classes to not include in the export
        public Set<String> hsClassesToSkip;
    }

    public static class LoginInfo
    {
        //~ Instance fields ------------------------------------------------------------------------

        // User name for logging in
        public String sUserName;

        // Password for logging in
        public String sPassword;

        // Database instance ID
        public String sDatabaseId;
    }
}
