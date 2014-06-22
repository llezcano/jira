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


import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.BasicIssue;
import com.atlassian.jira.rest.client.domain.BasicProject;
import com.atlassian.jira.rest.client.domain.BasicWatchers;
import com.atlassian.jira.rest.client.domain.Comment;
import com.atlassian.jira.rest.client.domain.Issue;
import com.atlassian.jira.rest.client.domain.SearchResult;
import com.atlassian.jira.rest.client.domain.Transition;
import com.atlassian.jira.rest.client.domain.input.ComplexIssueInputFieldValue;
import com.atlassian.jira.rest.client.domain.input.FieldInput;
import com.atlassian.jira.rest.client.domain.input.TransitionInput;
import com.atlassian.jira.rest.client.internal.ServerVersionConstants;
import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory;
import com.atlassian.jira.rest.client.domain.User;
import com.atlassian.jira.rest.client.JiraRestClient;
import com.atlassian.jira.rest.client.domain.User;


import com.google.common.collect.Lists;

import org.codehaus.jettison.json.JSONException;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;





/**
 * A sample code how to use JRJC library
 *
 * @since v0.1
 */
public class App {

	private static URI jiraServerUri = URI.create("http://ing.exa.unicen.edu.ar:8086/atlassian-jira-6.0/");
	private static boolean quiet = false;

	public static void main(String[] args) throws URISyntaxException, JSONException, IOException {
		parseArgs(args);

		final AsynchronousJiraRestClientFactory factory = new AsynchronousJiraRestClientFactory();
		final JiraRestClient restClient = factory.createWithBasicHttpAuthentication(jiraServerUri, "grodriguez", "654321");
		try {
			final int buildNumber = restClient.getMetadataClient().getServerInfo().claim().getBuildNumber();

			// first let's get and print all visible projects (only jira4.3+)
			if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
				final Iterable<BasicProject> allProjects = restClient.getProjectClient().getAllProjects().claim();
				for (BasicProject project : allProjects) {
					println(project);
				}
			}
			
			System.out.println("Issue key");
			// let's now print all issues matching a JQL string (here: all assigned issues)
			if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
				final SearchResult searchResult = restClient.getSearchClient().searchJql("assignee is not EMPTY").claim();
				for (BasicIssue issue : searchResult.getIssues()) {
					println(issue.getKey());
				}
			}

			final Issue issue = restClient.getIssueClient().getIssue("TST-7").claim();

			println(issue);

			// now let's watch it
			final BasicWatchers watchers = issue.getWatchers();
			if (watchers != null) {
				restClient.getIssueClient().watch(watchers.getSelf()).claim();
			}
			
			
			// print users 
			if (buildNumber >= ServerVersionConstants.BN_JIRA_5) {
			//	JiraClientRest users ;
			}			

		} finally {
			((PrintStream) restClient).close();
		}
	}

	private static void println(Object o) {
		if (!quiet) {
			System.out.println(o);
		}
	}

	private static void parseArgs(String[] argsArray) throws URISyntaxException {
		final List<String> args = Lists.newArrayList(argsArray);
		if (args.contains("-q")) {
			quiet = true;
			args.remove(args.indexOf("-q"));
		}

		if (!args.isEmpty()) {
			jiraServerUri = new URI(args.get(0));
		}
	}

	private static Transition getTransitionByName(Iterable<Transition> transitions, String transitionName) {
		for (Transition transition : transitions) {
			if (transition.getName().equals(transitionName)) {
				return transition;
			}
		}
		return null;
	}

}

