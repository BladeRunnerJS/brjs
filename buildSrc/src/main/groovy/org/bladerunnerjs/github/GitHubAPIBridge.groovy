package org.bladerunnerjs.github

import java.util.List;

import org.gradle.api.Project

import groovyx.net.http.RESTClient
import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC

import groovy.json.JsonSlurper

class GitHubAPIBridge
{
	
	static apiPrefix = "https://api.github.com"
	
	String repoOwner
	String repo
	String username
	String authToken
	
	public GitHubAPIBridge(String repoOwner, String repo, String username, String authToken)
	{
		this.repoOwner = repoOwner
		this.repo = repo
		this.username = username
		this.authToken = authToken
	}

	List<Issue> getClosedIssuesForMilestone(int milestoneID)
	{
		try {
			def response = getGitHubRestClient().get( 
				uri: apiPrefix,
				path: getRestUrl('issues'),
				queryString : 'milestone='+milestoneID+'&state=closed&per_page=100'
			)
			def jsonData = new JsonSlurper().parseText( response.data.toString() )
			
			List<Issue> issues = new ArrayList<Issue>();
			jsonData.each {
				issues.add( new Issue(it.html_url, it.id, it.title) )
			}
			
			return issues
		}
		catch( ex ) { 
			println "error getting milestones, response data was: '${ex.response.data}'"
			throw ex
		}
	}
	
	
	private RESTClient getGitHubRestClient()
	{
		RESTClient client = new RESTClient( apiPrefix )
		client.auth.basic username, authToken
		return client
	}

	private String getRestUrl(String suffix)
	{
		return "/repos/${repoOwner}/${repo}/${suffix}"
	}
	
	
}
