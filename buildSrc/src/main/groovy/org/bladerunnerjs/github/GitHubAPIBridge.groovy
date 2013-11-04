package org.bladerunnerjs.github

import java.util.List;

import org.gradle.api.Project
import org.gradle.api.logging.Logger

import org.apache.commons.codec.binary.Base64;

import groovyx.net.http.*
import groovyx.net.http.HTTPBuilder.RequestConfigDelegate
import org.apache.http.client.ClientProtocolException
import org.apache.http.client.methods.HttpPatch

import groovy.util.slurpersupport.GPathResult
import static groovyx.net.http.ContentType.URLENC
import static groovyx.net.http.ContentType.JSON


class GitHubAPIBridge
{
	
	static apiPrefix = "https://api.github.com"
	
	Logger logger
	String repoOwner
	String repo
	String authToken
	
	public GitHubAPIBridge(Project project, String repoOwner, String repo, String authToken)
	{
		this.logger = project.logger
		this.repoOwner = repoOwner
		this.repo = repo
		this.authToken = authToken
	}
	
	List<Issue> getClosedIssuesForMilestone(int milestoneID, List<String> includeIssueLabels)
	{
		logger.quiet "getting closed issues for milestoneID ${milestoneID}"
		
		def response = doRequest("get", getRestUrl('issues'), 'milestone='+milestoneID+'&state=closed&per_page=100', URLENC, null)
		
		List<Issue> issues = new ArrayList<Issue>();
		response.data.each {
			Issue issue = new Issue(it.html_url, it.id, it.title)
			boolean addIssue = false
			it.labels.each {
				if (includeIssueLabels.contains(it.name))
				{
					addIssue = true
					return
				}
			}
			if (addIssue)
			{
				issues.add( issue )
				logger.info "creating Issue object:  ${issue.toString()}"
			}
			else
			{
				logger.info "ignoring Issue:  ${issue.toString()}"
			}
		}
		logger.quiet "got ${issues.size()} issues matching label filters back from GitHub"
		
		return issues
	}
	
	Release createReleaseForTag(String tagVersion, HashMap releaseJson, String releaseDescription)
	{	
		logger.quiet "creating/editting release for tag ${tagVersion}"
		
		String httpMethod = "post"
		String httpUrl = 'releases'
		int releaseId = getIdForExistingRelease(tagVersion)
		if (releaseId >= 0) {
			 httpMethod = "patch"
			 httpUrl += '/'+releaseId
		}
		String restUrl = getRestUrl(httpUrl)
		
		def response = doRequest(httpMethod, restUrl, null, JSON, [
            tag_name: tagVersion,
            name: releaseJson.name,
            body: releaseDescription,
            prerelease: releaseJson.prerelease
        ] )
		
		def jsonData = response.data
		
		Release release = new Release(jsonData.url, jsonData.upload_url, jsonData.id, jsonData.name, jsonData.tag_name)
		logger.quiet "created/editted release '${release.toString()}'"
		return release
	}
	
	void uploadAssetForRelease(File brjsZip, Release release)
	{
		logger.quiet "uploading file ${brjsZip.path} for release ${release.tagVersion}"
		
		def response = doRequest("post", release.upload_url, "name=${brjsZip.name}", "application/zip", brjsZip )
		logger.quiet "successfully added release asset, ${brjsZip.toString()}"
	}

	private String getRestUrl(String suffix)
	{
		return "/repos/${repoOwner}/${repo}/${suffix}"
	}
	
	private int getIdForExistingRelease(String tagVersion)
	{ 
		logger.quiet "checking if release for tag ${tagVersion} already exists"			
		
		def response = doRequest("get", getRestUrl('releases'), null, URLENC, null )
		
		int releaseId = -1
		response.data.each {
			if (it.tag_name.equals(tagVersion))
			{
				releaseId = it.id
			}
		}
		if (releaseId >= 0)
		{
			logger.quiet "release for tag ${tagVersion}, already exists, ID is ${releaseId}"
		}
		else 
		{
			logger.quiet "no release exists for tag ${tagVersion}"
		}
		
		return releaseId
	}
	
	
	private Object doRequest(String httpMethod, String restUrl, String queryString, Object contentType, Object requestBody)
	{
		try {
			logger.quiet "making GitHub API ${httpMethod.toUpperCase()} request for '${restUrl}', query string is '${queryString}, body is '${requestBody.toString()}'"
    		
			def restClient = new RESTClient( apiPrefix )
			restClient.encoder.'application/zip' = this.&encodeZipFile
			
			def response = restClient."${httpMethod}"(
    			uri: apiPrefix,
    			requestContentType: contentType,
    			headers: [
    				'Authorization': "token ${authToken}",
    				'Accept': 'application/vnd.github.manifold-preview'
    			],
    			path: restUrl,
    			queryString : queryString,
				body: requestBody
    		)
    		logger.debug "GitHub response was: ${response.data.toString()}"
    		return response
    	}
    	catch( ex ) {
    		if (ex.hasProperty("response")) { logger.error "error making request, response data was: '${ex.response.data}'" }
    		throw ex
    	}
	}
	
	
	/* from http://agileice.blogspot.co.uk/2009/08/groovy-restclient-and-putting-zip-files.html */
	def encodeZipFile( Object data ) throws UnsupportedEncodingException {
		if ( data instanceof File ) {
			def entity = new org.apache.http.entity.FileEntity( (File) data, "application/zip" );
			entity.setContentType( "application/zip" );
			return entity
		} else {
			throw new IllegalArgumentException(
				"Don't know how to encode ${data.class.name} as a zip file" );
		}
	}
	
}
