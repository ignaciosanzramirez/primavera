package demo.xmlimport;

import java.util.Map;

import com.primavera.PrimaveraException;
import com.primavera.common.value.ObjectId;
import com.primavera.integration.client.xml.xmlimporter.ImportOption;

public interface LoginCallback
{
    public static final String REMOTE_MODE = "Remote";
    public static final String LOCAL_MODE = "Local";

    public boolean isRemoteModeAvailable();

    public com.primavera.integration.common.DatabaseInstance[] getDatabaseInstances(ConnectionInfo connInfo)
      throws PrimaveraException;

    public void login(ConnectionInfo connInfo, LoginInfo loginInfo)
      throws PrimaveraException;

    public Map<String,ObjectId> getEPS();

    public Map<String,ObjectId> getProjects();

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
        // Name of input XML file
        public String sInputFile;

        // Name of log file
        public String sLogFile;

        // XML import option for global objects
        public ImportOption globalImportOption;

        // XML import option for project-specific objects
        public ImportOption projectImportOption;

        // XML import option for project data
        public ImportOption projectImportMode;

        //EPS or project object Id. Depends on 'projectImportMode' option
        public ObjectId destObjectId;
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
