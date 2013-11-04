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

	List<Issue> getClosedIssuesForMilestone(int milestoneID)
	{
		try {
			String restUrl = getRestUrl('issues')
			String restQueryString = 'milestone='+milestoneID+'&state=closed&per_page=100'
			
			logger.quiet "getting closed issues for milestoneID ${milestoneID}"
			logger.quiet " - making GitHub API 'GET' request for '${restUrl}' with query string '${restQueryString}"
			
			def response = new RESTClient( apiPrefix ).get( 
				uri: apiPrefix,
				requestContentType: URLENC,
				headers: [
					'Authorization': "token ${authToken}",
					'Accept': 'application/vnd.github.manifold-preview'
				],
				path: restUrl,
				queryString : restQueryString
			)
			logger.debug " - GitHub response was: ${response.data.toString()}"
			
			List<Issue> issues = new ArrayList<Issue>();
			response.data.each {
				Issue issue = new Issue(it.html_url, it.id, it.title)
				issues.add( issue )
				logger.info " - creating Issue object:  ${issue.toString()}"
			}
			logger.quiet " - got ${issues.size()} issues back from GitHub"
			
			return issues
		}
		catch( ex ) { 
			if (ex.hasProperty("response")) { logger.error "error getting milestones, response data was: '${ex.response.data}'" }
			throw ex
		}
	}
	
	Release createReleaseForTag(String tagVersion, HashMap releaseJson, String releaseDescription)
	{	
		try {
			logger.quiet "creating/editting release for tag ${tagVersion}"
			
			String httpMethod = "post"
			String httpUrl = 'releases'
			int releaseId = getIdForExistingRelease(tagVersion)
			if (releaseId >= 0) {
				 httpMethod = "patch"
				 httpUrl += '/'+releaseId
			}
			String restUrl = getRestUrl(httpUrl)
			
			logger.quiet " - making GitHub API '${httpMethod.toUpperCase()}' request for ${restUrl}"
			
			def bodyData = [
				tag_name: tagVersion,
				name: releaseJson.name,
				body: releaseDescription,
				prerelease: releaseJson.prerelease
			] 
			logger.info " - request body data is ${bodyData.toString()}"
			
			def response = new RESTClient( apiPrefix )."${httpMethod}"(
				uri: apiPrefix,
				path: getRestUrl(httpUrl),
				requestContentType: JSON,
				headers: [
					'Authorization': "token ${authToken}",
					'Accept': 'application/vnd.github.manifold-preview'
				],
				body: bodyData
			)
			logger.debug " - GitHub response was: ${response.data.toString()}"
			
			def jsonData = response.data
			
			Release release = new Release(jsonData.url, jsonData.upload_url, jsonData.id, jsonData.name, jsonData.tag_name)
			logger.quiet " - created/editted ${release.toString()}"
			return release
		}
		catch( ex ) {
			if (ex.hasProperty("response")) { logger.error "error creating release, response data was: '${ex.response.data}'" }
			throw ex
		}
	}
	
	void uploadAssetForRelease(File brjsZip, Release release)
	{
		try {
			logger.quiet "uploading file ${brjsZip.path} for release ${release.tagVersion}"
			
			String restUrl = release.getAssetUrl(brjsZip)
			logger.quiet " - making GitHub API 'POST' request for '${restUrl}'"
			
			def restClient = new RESTClient( apiPrefix )
			restClient.encoder.'application/zip' = this.&encodeZipFile
			def response = restClient.post( 
				uri: apiPrefix,
				requestContentType: 'application/zip',
				headers: [
					'Authorization': "token ${authToken}",
					'Accept': 'application/vnd.github.manifold-preview'
				],
				path: restUrl,
				body: brjsZip
			)
			logger.debug " - GitHub response was: ${response.data.toString()}"
			logger.quiet " - successfully added release asset"
		}
		catch( ex ) { 
			if (ex.hasProperty("response")) { logger.error "error adding release asset, response data was: '${ex.response.data}'" }
			throw ex
		}
	}

	private String getRestUrl(String suffix)
	{
		return "/repos/${repoOwner}/${repo}/${suffix}"
	}
	
	private int getIdForExistingRelease(String tagVersion)
	{
		try {
			String restUrl = getRestUrl('releases')
			logger.quiet "checking if release for tag ${tagVersion} already exists"
			logger.quiet " - making GitHub API GET request for ${restUrl}"
			
			def response = new RESTClient( apiPrefix ).get(
				uri: apiPrefix,
				requestContentType: URLENC,
				headers: [
					'Authorization': "token ${authToken}",
					'Accept': 'application/vnd.github.manifold-preview'
				],
				path: getRestUrl('releases')
			)
			logger.debug " - GitHub response was: ${response.data.toString()}"
			
			int releaseId = -1
			response.data.each {
				if (it.tag_name.equals(tagVersion))
				{
					releaseId = it.id
				}
			}
			if (releaseId >= 0)
			{
				logger.quiet " - release for tag ${tagVersion}, already exists, ID is ${releaseId}"
			}
			else 
			{
				logger.quiet " - no release exists for tag ${tagVersion}"
			}
			
			return releaseId
		}
		catch( ex ) {
			if (ex.hasProperty("response")) { logger.error "error getting releases list, response data was: '${ex.response.data}'" }
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
