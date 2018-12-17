package demo.xmlimport;

import java.util.HashMap;
import java.util.Map;

import com.primavera.common.value.ObjectId;
import com.primavera.integration.client.Session;
import com.primavera.integration.client.bo.BOIterator;
import com.primavera.integration.client.bo.object.EPS;
import com.primavera.integration.client.bo.object.Project;

public class BusinessObjectLoader
{
    //~ Methods ------------------------------------------------------------------------------------

    public static Map<String, ObjectId> loadEPS(Session session)
    {
        Map<String, ObjectId> epsMap = new HashMap<String, ObjectId>();

        try
        {
            String[] fields = new String[] {"ObjectId", "Id"};
            BOIterator<EPS> boiEPS = session.getGlobalObjectManager().loadEPS(fields, null, null);

            while (boiEPS.hasNext())
            {
                EPS eps = boiEPS.next();
                epsMap.put(eps.getId(), eps.getObjectId());
            }
        }
        catch (Exception e)
        {
            // Do nothing here. Display a message afterwards.
        }

        return epsMap;
    }

    public static Map<String, ObjectId> loadProjects(Session session)
    {
        Map<String, ObjectId> projMap = new HashMap<String, ObjectId>();

        try
        {
            String[] fields = new String[] {"ObjectId", "Id"};
            BOIterator<Project> boiProjects = session.getGlobalObjectManager().loadProjects(fields, null, null);

            while (boiProjects.hasNext())
            {
                Project project = boiProjects.next();
                projMap.put(project.getId(), project.getObjectId());
            }
        }
        catch (Exception e)
        {
            // Do nothing here. Display a message afterwards.
        }

        return projMap;
    }
}
