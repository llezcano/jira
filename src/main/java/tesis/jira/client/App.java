package tesis.jira.client;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;

import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;

import tesis.jsonfilter.JSONFilter;

public class App {
	private static JiraFacade jira ;
	
	
	public static void main(String[] args) throws URISyntaxException, JSONException, IOException  {
			
		if (args[0].equals("help")) {
			System.out.println("AYUDA");
			System.out.println();
			System.out.println("Argumentos");
			System.out.println("Args[0] - Comando a ejecutar: ");
			System.out.println("	'help': envia todos los usuarios de jira al sender");
			System.out.println("	'issues': envia todos los usuarios de jira al sender");
			System.out.println("	'users': envia todos los usuarios de jira al sender");		
			
		} else {
			//TODO checkear si la URL, user y pass son validas
			jira = new JiraFacade("http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/", "grodriguez", "654321") ;
			if (args[0].equals("issues"))
				sendIssues() ;
			else if (args[0].equals("users"))
				sendUsers() ;
			else 
				System.out.println("Commando invalido: '"+args[0]+"'") ;
		
		}
		
	
	}
		
	
	static String readFile(String path, Charset encoding) throws IOException  {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
			
	}
	
	
	// TODO meterlo en alguna clase sacarle el hardcodeado
	static void send(String app, String dtype, String key, String json) throws URISyntaxException, MalformedURLException, IOException {
		URI uri = new URIBuilder()
	    .setScheme("http")
	    .setHost("localhost")
	    .setPort(8091)
	    .setPath("/sender/SenderService")
	    .addParameter("app", app)
	    .addParameter("data", dtype)
	    .addParameter("key", key)
	    .build();
		
		
		HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
		
		conn.setDoOutput(true);
		conn.setRequestMethod("POST");
		conn.addRequestProperty("Content-Type", "application/json");
		conn.setRequestProperty("Accept", "application/json");
		OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
		out.write(json);
		out.flush();
		out.close();
		System.err.println(conn.getResponseCode());
		
	}

	
	static public void sendIssues() throws IOException, URISyntaxException {
		
		// Para probar las consultas JQL ingresar aqui http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/issues/ 
		// y hacer click "Busqueda Avanzada"
		// Parseo y envio los issues (tareas)
		ObjectMapper m = new ObjectMapper();
		Iterator<JsonNode> it ;
		JSONFilter jf =  new JSONFilter() ;
		
		
		Integer size = 1000 ;
		Integer start = 0 ;
		boolean hasMore = true ;
		
		String issueJsonSchema = readFile( "issueSchema" , Charset.defaultCharset() ) ;
		JsonNode issueSchema = m.readTree(issueJsonSchema) ;
	
		while (hasMore) {
			ArrayNode issues = (ArrayNode) m.readTree(jira.getIssues("", start, size )).path("issues") ;
			
			it = issues.getElements() ;
			
			JsonNode issue = null ;
			hasMore = it.hasNext() ;
			
			while (it.hasNext()) {
				issue = it.next() ;
				JsonNode json =  jf.filter(issue, issueSchema) ;
				System.out.println( json.get("key").getTextValue() ); //estos issues estan listos para ser enviados al ES
				send( "jira", "issue", json.get("key").getTextValue(), json.toString()) ;
			}
			start += size ;						
		}
		
	}
	
	static public void sendUsers() throws IOException, URISyntaxException {
		ObjectMapper m = new ObjectMapper();
		Iterator<JsonNode> it ;
		JSONFilter jf =  new JSONFilter() ;
		
		
		Integer max = 49, min = 0 ;
		Integer gap = 49 ;
		boolean hasMore = true ;
		
		// Parseo y envio los users

		String userJsonSchema = readFile( "userSchema" , Charset.defaultCharset() ) ;
		JsonNode userSchema = m.readTree(userJsonSchema) ;
		while (hasMore) {
			ArrayNode users = (ArrayNode) ( m.readTree(jira.getUsers("jira-developers", min, max)).path("users").path("items")) ;	
			
			it = users.getElements() ;
			JsonNode user = null ;
			hasMore = it.hasNext() ;
			while (it.hasNext()) {
				user = it.next() ;
				JsonNode json =  jf.filter(user, userSchema) ; 
				System.out.println( json ); //estos usuarios estan listos para ser enviados al ES
				send( "jira", "user", json.get("name").getTextValue(), json.toString()) ;
				

			}
			min = max+1 ;
			max = min + gap ;	
		}
	}
	
	/* Redireccionar System.out a un archivo 
	 * import java.io.File;
	 * import java.io.FileOutputStream;
	 * import java.io.PrintStream;

	File file = new File("test.txt");  
	FileOutputStream foStream = new FileOutputStream(file);  
	PrintStream out = new PrintStream(foStream);  
	System.setOut(out);  
	*/

}

