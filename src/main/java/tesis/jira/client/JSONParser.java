
/**
 *  DEPRECATED
 */
package tesis.jira.client;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class JSONParser {
	public JSONParser(){
	}
	
	
	/**
	 * 
	 * Como entrada tendriamos el JSON resultante de  :
	 * http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/group?groupname=jira-developers
	 * 
	 * 
	 * y como salida una lista de usuarios individuales, un ejemplo de usuario seria el siguiente:
	 *{
	 *	"name":"croldan",
	 *	"emailAddress":"croldan1990@gmail.com",
	 *	"displayName":"Cristian Roldan",
	 *	"active":true
	 *}
	 */
	public List<String> filterUsers( String jsonInput ) {
		JSONObject obj;
		List<String> result = new ArrayList<String>() ;
		try {
			obj = new JSONObject(jsonInput);
			System.out.println(obj);
			// Agarro a todos los usuarios dentro del json de forma separada
			JSONArray users = ((JSONObject) obj.get("users")).getJSONArray("items") ;
			JSONObject user ;
			for ( int i = 0; i<users.length(); i++) {
				//aqui filtro la informacion que se va a almacenar (lo que se considere necesario)
				user = users.getJSONObject(i) ;
				user.remove("self") ;
				user.remove("avatarUrls");
				result.add(user.toString()) ;
				System.out.println(user);			
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result ;
	}
	
	
	
	
	public List<String> filterIssues( String jsonInput ) {
		// Forma cabeza
		JSONObject obj;
		List<String> result = new ArrayList<String>() ;
		try {
			obj = new JSONObject(jsonInput);
			//System.out.println(obj);
			// Agarro a todos los usuarios dentro del json de forma separada
			JSONArray issues = obj.getJSONArray("issues") ;
			JSONObject issue ;
			for ( int i = 0; i<issues.length(); i++) {
				//aqui filtro la informacion que se va a almacenar (lo que se considere necesario)
				issue = issues.getJSONObject(i) ;
				issue.remove("expand") ;
				issue.remove("self");
				
				// Cada issue tiene una seccion (JSONObject) "fields" donde guarda la información asociada al mismo
				JSONObject fields = (JSONObject) issue.get("fields") ;
				fields.remove("summary");
				
				//Extraigo el issueType, remuevo todo excepto "id"
				JSONObject issueType = (JSONObject) fields.get("issuetype") ;
				issueType.remove("self");
				issueType.remove("description");
				issueType.remove("iconUrl");
				issueType.remove("name");
				issueType.remove("subtask");
				//Vuevo a colocar issuetype modificado al objeto fields (elimino el issuetype viejo)
				fields.remove("issuetype");
				fields.put("issuetype", issueType);
				
				
				//TODO campos posiblemente para editar (puede que no se tenga que editar)
				// los que estan totalmente comentados es por que no se deben editar
				// los que tienen un comentario, este indica los campos que debo guardar (el resto se debe filtrar)
				//fields.remove("created") ;
				//fields.remove("updated") ;				
				//fields.remove("workratio") ;
				//fields.remove("aggregatetimeestimate") ;
				//fields.remove("resolutiondate");
				//fields.remove("aggregatetimeoriginalestimate") ;
				
				//fields.remove("reporter") ; // name, email
				
				JSONObject reporter = (JSONObject) fields.get("reporter") ;
				reporter.remove("self");
				reporter.remove("avatarUrls");
				reporter.remove("displayName");
				reporter.remove("active");
				fields.remove("reporter");
				fields.put("reporter", reporter);
				
				JSONObject priority = (JSONObject) fields.get("priority") ;
				priority.remove("self");
				priority.remove("iconUrl");
				fields.remove("priority") ; // name, id
				fields.put("priority", priority);	
				
				JSONObject status = (JSONObject) fields.get("status") ;
				status.remove("self");
				status.remove("description");
				status.remove("iconUrl");
				fields.remove("status") ; //name, id
				fields.put("status", status);
				
	
				JSONObject project = (JSONObject) fields.get("project") ;
				project.remove("self");
				project.remove("name");
				project.remove("avatarUrls");
				fields.remove("project") ; //id, key
				fields.put("project", project);

				JSONObject assignee = (JSONObject) fields.get("assignee") ;
				assignee.remove("self");
				assignee.remove("displayName");
				assignee.remove("avatarUrls");
				assignee.remove("active");
				fields.remove("assignee") ; //name, email
				fields.put("assignee", assignee);


				//agarro todas las subtasks
				JSONArray subtasks = fields.getJSONArray("subtasks") ;
				JSONObject subtask ;
				
				//por cada subtask...
				for ( int j = 0; j<subtasks.length(); j++) {
					subtask = subtasks.getJSONObject(j) ;
					JSONObject subtask_fields = (JSONObject) subtask.get("fields") ;
					subtask.remove("self") ;
					// por cada subtask : id, key, status {id}, priority {id}, issuetype {id}
					
					// TODO por cada subtask dejar solo los id de status, priority, issuetype
					JSONObject subtask_status = (JSONObject) subtask_fields.get("status") ;
					subtask_status.remove("self");
					subtask_status.remove("description");
					subtask_status.remove("iconUrl");
					subtask_status.remove("name");
					
					subtask_fields.remove("status") ;
					subtask_fields.put("status", subtask_status) ;
			
					JSONObject subtask_issuetype = (JSONObject) subtask_fields.get("issuetype") ;
					subtask_issuetype.remove("self");
					subtask_issuetype.remove("description");
					subtask_issuetype.remove("iconUrl");
					subtask_issuetype.remove("name");
					subtask_issuetype.remove("subtask") ;
					
					subtask_fields.remove("issuetype") ;
					subtask_fields.put("issuetype", subtask_issuetype) ;

					JSONObject subtask_priority = (JSONObject) subtask_fields.get("priority") ;
					subtask_priority.remove("self");
					subtask_priority.remove("iconUrl");
					subtask_priority.remove("name");
					
					subtask_fields.remove("priority") ;
					subtask_fields.put("priority", subtask_priority) ;

					
				
				}
				
				fields.remove("subtasks") ; //id, key, fields {status, priority, issuetype}
				fields.put("subtasks", subtasks) ;
				
				
				fields.remove("parent") ; //id, key // no lo encontre
				
				//campos que se descartan
				

				fields.remove("votes") ;
				fields.remove("resolution");
				fields.remove("fixVersions");
				
				fields.remove("timespent") ;
				fields.remove("customfield_10203") ;
				fields.remove("customfield_10204") ;
				fields.remove("customfield_10205") ;
				fields.remove("customfield_10206") ;
				fields.remove("customfield_10207") ;

				fields.remove("customfield_10208") ;
				fields.remove("description") ;

				fields.remove("duedate") ;
				fields.remove("customfield_10001") ;
				fields.remove("customfield_10002") ;
				fields.remove("issuelinks") ;
				fields.remove("watches") ;
				fields.remove("customfield_10000") ;

				fields.remove("customfield_10100") ;
				fields.remove("customfield_10009") ;
				fields.remove("customfield_10008") ;
				
				fields.remove("customfield_10007") ;
				fields.remove("customfield_10006") ;
				fields.remove("labels") ;
				
				fields.remove("customfield_10202") ;
				fields.remove("customfield_10201") ;
				fields.remove("customfield_10200") ;
				
				fields.remove("versions") ;
				fields.remove("environment") ;
				fields.remove("timeestimate") ;
				fields.remove("aggregateprogress") ;
				fields.remove("lastViewed") ;
				fields.remove("components") ;
				fields.remove("timeoriginalestimate") ;
				fields.remove("aggregatetimespent") ;

				
				result.add(issue.toString()) ;
			//	System.out.println(issue);			
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return result ;
		
	}
	
	
	
	
	
}
