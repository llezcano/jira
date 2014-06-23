package tesis.jira.client;

import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import org.codehaus.jettison.json.JSONException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class JiraFacade {
	
	private AsynchronousJiraRestClientFactory factory ;
	private JiraRestClient restClient ;
	/**
	 * Constructor con autentificacion para JIRA REST Java Cliente (JRJA).
	 * 
	 * @param URL	URL donde se ubica el Servidor JIRA con el cual se desea comunicar
	 * @param user	Nombre de usuario el cual realizara las consultas
	 * @param pass 	Contraseña del usuario
	 */
	public JiraFacade(String URL, String user, String pass) throws URISyntaxException, JSONException, IOException {
		factory = new AsynchronousJiraRestClientFactory();
		restClient = factory.createWithBasicHttpAuthentication(URI.create(URL), user, pass);
		
		/* TODO checkear version de JIRA
		int buildNumber = restClient.getMetadataClient().getServerInfo().claim().getBuildNumber();
		if (buildNumber < ServerVersionConstants.BN_JIRA_5) {
			// TODO generar una exepcion si la version de JIRA es menor a 5
		}
		*/
	}
	
	/**
	 * Constructor sin autentificacion para JIRA REST Java Cliente (JRJA).
	 * 
	 * @param URL	URL donde se ubica el Servidor JIRA con el cual se desea comunicar
	 */
	public JiraFacade(String URL) throws URISyntaxException, JSONException, IOException {
		//TODO
	}
	
	/**
	 * Consulta las Issues que cumplen con un determinado criterio, dicho criterio esta definido
	 * en Java Query Language (JQL).
	 * 
	 * @param query	Consulta a realizar en JQL 
	 * @return		Lista de Issues que satisfacen la consulta
	 */
	public List<BasicIssue> issueQuery(String query) {
		List<BasicIssue> result = new ArrayList<BasicIssue>() ;
		final SearchResult searchResult = restClient.getSearchClient().searchJql(query).claim();
		for (BasicIssue issue : searchResult.getIssues()) {
			// System.out.println(issue.getKey());
			result.add(issue) ;
		}
		return result;
	}
	
	
}
