package org.bladerunnerjs.api.spec.engine;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.bladerunnerjs.api.appserver.ApplicationServer;

import com.google.common.base.Predicate;


public class AppServerVerifier
{
	
	VerifierChainer verifierChainer;
	SpecTest specTest;

	public AppServerVerifier(SpecTest specTest, ApplicationServer appServer)
	{
		this.specTest = specTest;
		verifierChainer = new VerifierChainer(specTest);
	}

	public VerifierChainer requestCanBeMadeFor(String urlPath) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url).statusCodeIs(200);
		
		return verifierChainer;
	}
	
	public VerifierChainer requestCannotBeMadeFor(String urlPath) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url).statusCodeIs(404);
		
		return verifierChainer;
	}

	public VerifierChainer requestIs302Redirected(String urlPath, String redirectPath) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url, false).statusCodeIs(302).redirectUrlIs(redirectPath);
		
		return verifierChainer;
	}
	
	public VerifierChainer requestIs301Redirected(String urlPath, String redirectPath) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url, false).statusCodeIs(301).redirectUrlIs(redirectPath);
		
		return verifierChainer;
	}

	public VerifierChainer requestCanEventuallyBeMadeFor(String urlPath) throws ClientProtocolException, IOException, InterruptedException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.pollServerForStatusCode(url, 200);
		
		return verifierChainer;
	}

	public VerifierChainer requestForUrlReturns(String urlPath, String response) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).responseIs(response);
		
		return verifierChainer;	
	}
	
	public VerifierChainer requestForUrlHasResponseCode(String urlPath, int statusCode) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).statusCodeIs(statusCode);
		
		return verifierChainer;
	}
	
	public VerifierChainer requestForUrlContains(String urlPath, String contains) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url).responseContains(contains);
		
		return verifierChainer;
	}

	public VerifierChainer contentTypeForRequestIs(String urlPath, String mimeType) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).contentTypeIs(mimeType);
		
		return verifierChainer;		
	}
	
	public VerifierChainer characterEncodingForRequestIs(String urlPath, String characterEncoding) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).characterEncodingIs(characterEncoding);
		
		return verifierChainer;		
	}

	public VerifierChainer requestCanEventuallyBeMadeWhereResponseMatches(String urlPath, Predicate<String> predicate) throws ClientProtocolException, IOException, InterruptedException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.pollServerUntilMatchesPredicate(url, predicate);
		
		return verifierChainer;	
	}

	public VerifierChainer contentLengthForRequestIs(String urlPath, int length) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).contentLengthIs(length);
		
		return verifierChainer;
	}
	
	
	private String getUrl(String urlPath)
	{
		return String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
	}
}
