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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.codehaus.jettison.json.JSONException;

import com.atlassian.jira.rest.client.domain.BasicIssue;

public class App {
	public static void main(String[] args) throws URISyntaxException, JSONException, IOException  {
		
		JiraFacade jira = new JiraFacade("http://", "hu", "dwqdwq");
		
		// TODO probar varios tipos de consultas y analizar la estructura de los resultados
		// Para probar las consultas JQL ingresar aqui http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/issues/ 
		// y hacer click "Busqueda Avanzada"
		
		List<BasicIssue> queryResult = jira.issueQuery("assignee = asdasd") ;
		
		for (BasicIssue issue : queryResult ) {
		//	jira.getIssue(issue.getKey()) ;
			System.out.println(jira.getIssue(issue.getKey())) ;
			break ;
		}
		
		
	}

}

