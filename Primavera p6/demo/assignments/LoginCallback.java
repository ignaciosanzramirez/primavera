package demo.assignments;

import com.primavera.PrimaveraException;

public interface LoginCallback
{
    public static final String REMOTE_MODE = "Remote";
    public static final String LOCAL_MODE = "Local";

    public boolean isRemoteModeAvailable();

    public com.primavera.integration.common.DatabaseInstance[] getDatabaseInstances(ConnectionInfo connInfo)
      throws PrimaveraException;

    public void login(ConnectionInfo connInfo, LoginInfo loginInfo)
      throws PrimaveraException;

    public void logout();

    public void runDemo(DemoInfo demoInfo);

    public static class ConnectionInfo
    {
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
        // Filename of output HTML file
        public String sFileName;

        // Maxmimum number of resources
        public int iMaxResources;

        // Field for ordering the resources
        public String sOrderResourcesBy;

        // Field for ordering the resource assignments
        public String sOrderAssignmentsBy;

        // Order of results
        public boolean bAscending;
    }

    public static class LoginInfo
    {
        // User name for logging in
        public String sUserName;

        // Password for logging in
        public String sPassword;

        // Database instance ID
        public String sDatabaseId;
    }
}
