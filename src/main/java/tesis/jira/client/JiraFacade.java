package tesis.jira.client;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.ws.rs.core.MultivaluedMap;


public class JiraFacade {

	private String URL;
	private HTTPBasicAuthFilter authorization ;
	
	private static String API = "rest/api/2/" ;
	
	//resources from Jira API
	private static final String RESOURCE_GROUP = "group" ;
	private static final String RESOURCE_SEARCH = "search" ;
	
	//parameters for each resource
	// group
	private static final String RGROUP_GROUPNAME = "groupname" ;
	private static final String RGROUP_EXPAND = "expand" ;
	
	// search
	private static final String RSEARCH_QUERY = "jql" ;
	private static final String RSEARCH_START = "startAt"; 
	private static final String RSEARCH_MAX = "maxResults";
	
	/**
	 * Constructor con autentificacion para cliente REST de JIRA
	 * 
	 * @param URL	URL donde se ubica el Servidor JIRA con el cual se desea comunicar
	 * @param user	Nombre de usuario el cual realizara las consultas
	 * @param pass 	Contraseña del usuario
	 */
	public JiraFacade(String url, String user, String pass) throws URISyntaxException, JSONException, IOException {
		URL = url ;
		authorization = new HTTPBasicAuthFilter(user, pass) ;
	}

	
	/**
	 * Metodo HTTP GET para acceder a la API de JIRA.
	 * 
	 * @param resource	Recurso de la API al cual se desea hacer el GET.
	 * @param params	Parametros con los cuales se realizara el GET.
	 * @return			JSON como respuesta el GET.
	 */
	private String get( String resource, MultivaluedMap<String, String> params ) {
		try {
			Client client = Client.create();
			
			WebResource webResource = client.resource(URL + API + resource) ;
			webResource.addFilter(authorization);
			webResource.setProperty("Content-Type", "application/json;charset=UTF-8");
			
			ClientResponse response = webResource
	                .queryParams(params)
					.accept("application/json")
					.get(ClientResponse.class) ;
					 
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatus());
			}
			String output = response.getEntity(String.class);
			return output ;
				
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null ;
	
	}
	
	
	/**
	 * @param issueKey	clave del issue que se desea obtener
	 * @return			Un json correspondiente al issue con toda la informacion asociada a este.
	 * 
	 */
	public String getIssue(String issueKey) {
		return null ;
	}
	

	/**
	 * Consulta las Issues que cumplen con un determinado criterio, dicho criterio esta definido
	 * en Java Query Language (JQL).
	 * 
	 * @param query	Consulta a realizar en JQL 
	 * @return		Lista de Basic Issues que satisfacen la consulta, un basic Issue tiene poca informacion del 
	 * 				issue en si, pero tiene la Key.
	 * 
	 */
	public String getIssues( String query, Integer min, Integer max ) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl(); 
		params.add(RSEARCH_QUERY, query ); 
		params.add(RSEARCH_START, min.toString()) ;
		params.add(RSEARCH_MAX, max.toString()) ;
		return get(RESOURCE_SEARCH, params) ;
	}


	/**
	 * Retorna todos los issues de JIRA 
	 * @param min	
	 * @param max	
	 * @return
	 */
	public String getIssues(Integer min, Integer max) {
		return getIssues("", min, max) ;
	}
	
	
	public String getUser(String userKey) {
		//TODO
		return null ;
	}
	
	
	public String getUsers( String group, Integer min, Integer max ) {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl(); 
		params.add(RGROUP_GROUPNAME, group );
		params.add(RGROUP_EXPAND, "users[" + min.toString() + ":" + max.toString() + "]") ;
		return get(RESOURCE_GROUP, params) ;
	}

	
	
	
	
}
