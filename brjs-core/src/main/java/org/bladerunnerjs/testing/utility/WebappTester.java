package org.bladerunnerjs.testing.utility;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.net.SocketTimeoutException;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

public class WebappTester 
{
	
	private static final int MAX_POLL_REQUESTS = 10;
	private static final int POLL_INTERVAL = 500;
	
	private int defaultSocketTimeout = 1000;
	private int defaultConnectionTimeout = 1000;
	
	private File filePathBase;
	private int statusCode;
	
	private HttpResponse httpResponse;
	private String response;
	private String contentType;
	private String url;
	
	public WebappTester(File filePathBase, int defaultSocketTimeout, int defaultConnectionTimeout)
	{
		this(filePathBase);
		this.defaultSocketTimeout = defaultSocketTimeout;
		this.defaultSocketTimeout = defaultConnectionTimeout;
	}
	
	public WebappTester(File filePathBase)
	{
		this.filePathBase = filePathBase;
	}
	
	public WebappTester whenRequestMadeTo(String url, boolean followRedirects) throws ClientProtocolException, IOException
	{
		return whenRequestMadeTo(url, defaultSocketTimeout, defaultConnectionTimeout, followRedirects);
	}
	
	public WebappTester whenRequestMadeTo(String url) throws ClientProtocolException, IOException
	{
		return whenRequestMadeTo(url, defaultSocketTimeout, defaultConnectionTimeout, true);
	}
	
	public WebappTester whenRequestMadeTo(String url, int socketTimeout, int connectionTimeout, boolean followRedirects) throws ClientProtocolException, IOException {
		this.url = url;
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, socketTimeout);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, connectionTimeout);	
		
		HttpGet get = new HttpGet(url);
		HttpParams params = get.getParams();
		params.setParameter(ClientPNames.HANDLE_REDIRECTS, followRedirects);
		get.setParams(params);
		
		httpResponse = httpClient.execute( get );
		statusCode = httpResponse.getStatusLine().getStatusCode();
		response = EntityUtils.toString(httpResponse.getEntity());
		contentType = ContentType.getOrDefault(httpResponse.getEntity()).getMimeType();
		EntityUtils.consume(httpResponse.getEntity());
		httpClient.getConnectionManager().shutdown();
		return this;
	}
	
	public WebappTester requestTimesOut(String url) throws IOException
	{
		try {
			whenRequestMadeTo(url, 150, 150, false);
			fail("Expected request to " + url + " to timeout, but came back with status code " + statusCode);
		}
		catch (SocketTimeoutException | HttpHostConnectException | ConnectTimeoutException ex)
		{
		}
		
		return this;		
	}
	
	public void pollServerForStatusCode(String url, int requiredStatusCode) throws ClientProtocolException, IOException, InterruptedException
	{
		int requestCount = 0;
		while(statusCode != requiredStatusCode && requestCount < MAX_POLL_REQUESTS)
		{
			whenRequestMadeTo(url);
			if (statusCode == requiredStatusCode)
			{
				return;
			}
			requestCount++;
			Thread.sleep(POLL_INTERVAL);
		}
		assertEquals("Never got required status code", requiredStatusCode, statusCode);
	}
	
	public WebappTester printOutcome() 
	{
		System.out.println("URL:		"+url);
		System.out.println("StatusCode:	"+statusCode);
		System.out.println("ContentType:	"+contentType);
		System.out.println("Content:");
		System.out.println(response);
		return this;
	}
	
	public WebappTester statusCodeIs(int statusCode)
	{
		if(statusCode != this.statusCode) {
			assertEquals("Status codes don't match.", statusCodeText(statusCode, null), statusCodeText(this.statusCode, response));
		}
		return this;
	}
	
	public WebappTester contentTypeIs(String contentType)
	{
		if(!contentType.equals(this.contentType)) {
			assertEquals("Content types don't match.", contentTypeText(contentType, null), contentTypeText(this.contentType, response));
		}
		return this;
	}
	
	public WebappTester responseIs(String response)
	{
		assertEquals("response wasnt the same", response, this.response);
		return this;
	}
	
	public WebappTester responseContains(String response)
	{
		assertThat(this.response, containsString(response));
		return this;
	}
	
	public WebappTester responseDoesntContain(String response)
	{
		assertThat(this.response, not(containsString(response)));
		return this;
	}
	
	public WebappTester redirectUrlIs(String redirectPath)
	{
		httpResponse.getFirstHeader("Location").equals(redirectPath);
		return this;
	}
	
	
	public WebappTester responseIsContentsOfFile(String filePath) throws IOException
	{
		return responseIsConcatenationOfFiles(new String[]{filePath}, "\n");
	}
	
	public WebappTester responseIsConcatenationOfFiles(String[] filePaths) throws IOException
	{
		return responseIsConcatenationOfFiles(filePaths, "\n");
	}
	
	public WebappTester responseIsConcatenationOfFiles(String[] filePaths, String delimiter) throws IOException
	{
		Writer writer = new StringWriter();
		for(String path: filePaths)
		{
			File sourceFile = new File(filePathBase, path);
			try(FileReader input = new FileReader(sourceFile))
			{
				IOUtils.copy(input, writer);
				writer.write("\n\n");
			}
		}
		assertEquals("response wasn't concatenation of specified files", contentType, this.contentType);
		return this;
	}
	
	public WebappTester sameAsRequestFor(String url) throws ClientProtocolException, IOException 
	{
		new WebappTester(filePathBase).whenRequestMadeTo(url)
			.statusCodeIs(statusCode)
			.contentTypeIs(contentType)
			.responseIs(response);
		return this;
	}
	
	
	
	private String statusCodeText(int statusCode, String contentBody) {
		return contentBodyText("Status Code", statusCode, contentBody);
	}
	
	private String contentTypeText(String contentType, String contentBody) {
		return contentBodyText("Content Type", contentType, contentBody);
	}
	
	private String contentBodyText(String comparisonTitle, Object comparisonValue, String contentBody) {
		return comparisonTitle + ": " + comparisonValue + "\nContent Body: " + ((contentBody == null) ? "<ANYTHING>" : contentBody);
	}
	
}