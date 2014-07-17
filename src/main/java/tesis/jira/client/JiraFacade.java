package tesis.jira.client;



import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;

import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jettison.json.JSONException;

import sun.misc.BASE64Encoder;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.io.InputStreamReader;
import java.io.BufferedReader;


@SuppressWarnings("restriction")
public class JiraFacade {
	
	private AsynchronousJiraRestClientFactory factory ;
	private JiraRestClient restClient ;
	private String _url,  _user, _pass ;
	
	/**
	 * Constructor con autentificacion para JIRA REST Java Cliente (JRJA).
	 * 
	 * @param URL	URL donde se ubica el Servidor JIRA con el cual se desea comunicar
	 * @param user	Nombre de usuario el cual realizara las consultas
	 * @param pass 	Contraseña del usuario
	 */
	public JiraFacade(String URL, String user, String pass) throws URISyntaxException, JSONException, IOException {
		factory = new AsynchronousJiraRestClientFactory();
		_user = user ;
		_pass = pass ;
		_url = URL ;
		restClient = factory.createWithBasicHttpAuthentication(URI.create(URL) , user, pass);
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
	
	
	public List<BasicIssue> issueQuery(String query) {
		List<BasicIssue> result = new ArrayList<BasicIssue>() ;
		final SearchResult searchResult = restClient.getSearchClient().searchJql(query).claim();
		for (BasicIssue issue : searchResult.getIssues()) {
			result.add(issue) ;
		}
		return result;
	}
	
	/**
	 * @param issueKey	clave del issue que se desea obtener
	 * @return			Un issue con toda la informacion asociada a este.
	 * 
	 */
	public Issue getIssue(String issueKey) {
		return restClient.getIssueClient().getIssue(issueKey).claim();
	}
	
	public String getUsersByGroup(String group, Integer min, Integer max) {
	    String result = "" ;
		try {
		//	uri = new URI( "http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/group"+ "?groupname=jira-developers" );
			
			//TODO  aca se tendria que usar la variable _url y que no quede harcodeado
			URI uri = new URIBuilder()
		    .setScheme("http")
		    .setHost("ing.exa.unicen.edu.ar")
		    .setPort(8086)
		    .setPath("/atlassian-jira-6.0/rest/api/2/group")
		    .addParameter("groupname", group)
		    .addParameter("expand", "users[" + min + ":" + max + "]")
		    .build();
			
			HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");		
			conn.setRequestProperty("charset", "utf-8");

		    String userpassword = _user + ":" + _pass;
		    BASE64Encoder enc = new BASE64Encoder() ;
		    String encodedAuthorization = enc.encode(userpassword.getBytes()) ;
		    conn.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);
		    
		    InputStream response = conn.getInputStream();
		    /* printing inputStream*/ 
		    
		    BufferedReader in = new BufferedReader(new InputStreamReader(response));

		    String inputLine;
		    try {
		    	while ((inputLine = in.readLine()) != null)
		    	    result += inputLine ;
		    	in.close();
		    } catch (IOException e1) {
		    	// TODO Auto-generated catch block
		    	e1.printStackTrace();
		    }
		    
		    System.err.println(conn.getResponseCode());
		
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(uri.toString());
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result ;
		
	}
	
	
	
	public String getIssues( String query , Integer min, Integer max) {
		
		//http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/rest/api/2/search?startAt=2&maxResults=3
	    String result = "" ;
		try {
			URI uri = new URIBuilder()
		    .setScheme("http")
		    .setHost("ing.exa.unicen.edu.ar")
		    .setPort(8086)
		    .setPath("/atlassian-jira-6.0/rest/api/2/search")
		  //  .addParameter("jql", query)
		    .addParameter("startAt", min.toString())
		    .addParameter("maxResults", max.toString())// permite hasta 1000 como maximo
		    .build();
			
			HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
			conn.setDoOutput(true);
			conn.setRequestMethod("GET");		
			conn.setRequestProperty("charset", "utf-8");

		    String userpassword = _user + ":" + _pass;
		    BASE64Encoder enc = new BASE64Encoder() ;
		    String encodedAuthorization = enc.encode(userpassword.getBytes()) ;
		    conn.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);
		    
		    InputStream response = conn.getInputStream();
		    /* printing inputStream*/ 
		    
		    BufferedReader in = new BufferedReader(new InputStreamReader(response));

		    String inputLine;
		    try {
		    	while ((inputLine = in.readLine()) != null)
		    	    result += inputLine ;
		    	in.close();
		    } catch (IOException e1) {
		    	// TODO Auto-generated catch block
		    	e1.printStackTrace();
		    }
		    
		    System.err.println(conn.getResponseCode());
		
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println(uri.toString());
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return result;
	}




}
