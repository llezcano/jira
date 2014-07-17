/*
 * Copyright (C) 2010 Atlassian
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package tesis.jira.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.domain.BasicIssue;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import tesis.jsonfilter.JSONFilter;

public class App {
	public static void main(String[] args) throws URISyntaxException, JSONException, IOException  {
		JiraFacade jira = new JiraFacade("http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/", "grodriguez", "654321");
		// Para probar las consultas JQL ingresar aqui http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/issues/ 
		// y hacer click "Busqueda Avanzada"
		// Parseo y envio los issues (tareas)

		ObjectMapper m = new ObjectMapper();
		Iterator<JsonNode> it ;
		JSONFilter jf =  new JSONFilter() ;
		
		
		Integer max = 49, min = 0 ;
		Integer gap = 50 ;
		boolean hasMore = true ;
		String issueJsonSchema = readFile( "issueSchema" , Charset.defaultCharset() ) ;
		JsonNode issueSchema = m.readTree(issueJsonSchema) ;

		
		while (hasMore) {
			ArrayNode issues = (ArrayNode) m.readTree(jira.getIssues("", min, max )).path("issues") ;
			it = issues.getElements() ;
			
			JsonNode issue = null ;
			hasMore = it.hasNext() ;
			while (it.hasNext()) {
				issue = it.next() ;
				//System.out.println( jf.filter(issue, issueSchema) ); //estos issues estan listos para ser enviados al ES
				 send( "jira", "issue", jf.filter(issue, issueSchema).toString()) ;
			
			}
			min = max ;
			max += gap ;					
		}
		
		// Parseo y envio los users
		
		
		max = 49;
		min = 0 ;
		hasMore = true ;
		String userJsonSchema = readFile( "userSchema" , Charset.defaultCharset() ) ;
		JsonNode userSchema = m.readTree(userJsonSchema) ;
		while (hasMore) {
			ArrayNode users = (ArrayNode) ( m.readTree(jira.getUsersByGroup("jira-developers", min, max)).path("users").path("items")) ;	
			it = users.getElements() ;
			JsonNode user = null ;
			hasMore = it.hasNext() ;
			while (it.hasNext()) {
				user = it.next() ;
				//System.out.println( jf.filter(user, userSchema) ); //estos usuarios estan listos para ser enviados al ES
				send( "jira", "user", jf.filter(user, userSchema).toString()) ;
			}
			min = max ;
			max += gap ;	
		}
		
		
		/*
		//aca ya tengo los usuarios por separados, listos para enviar al ElasticSearch
		JSONParser jp = new JSONParser () ;
		List<String> users = jp.filterUsers(jira.getUsersByGroup("jira-developers")) ;
		for (String u : users){
			System.out.println(u) ;
		}
	*/	
		
		
	}
		
	
	static String readFile(String path, Charset encoding) throws IOException  {
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded, encoding);
			
	}
	
	
	// TODO meterlo en alguna clase sacarle el hardcodeado
	static void send(String app, String dtype, String json) throws URISyntaxException, MalformedURLException, IOException {
		URI uri = new URIBuilder()
	    .setScheme("http")
	    .setHost("localhost")
	    .setPort(8091)
	    .setPath("/sender/SenderService")
	    .addParameter("app", app)
	    .addParameter("data", dtype)
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

	/* Redireccionar System.out a un archivo 
	File file = new File("test.txt");  
	FileOutputStream foStream = new FileOutputStream(file);  
	PrintStream out = new PrintStream(foStream);  
	System.setOut(out);  
	/* Redireccionar System.out a un archivo */	

}

