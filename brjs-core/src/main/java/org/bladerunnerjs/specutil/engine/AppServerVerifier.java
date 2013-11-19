package org.bladerunnerjs.specutil.engine;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.bladerunnerjs.model.appserver.ApplicationServer;


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
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.whenRequestMadeTo(url).statusCodeIs(200);
		
		return verifierChainer;
	}

	public VerifierChainer requestCannotBeMadeFor(String urlPath) throws ClientProtocolException, IOException
	{
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.whenRequestMadeTo(url).statusCodeIs(404);
		
		return verifierChainer;
	}

	public VerifierChainer requestIsRedirected(String urlPath, String redirectPath) throws ClientProtocolException, IOException
	{
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.whenRequestMadeTo(url, false).statusCodeIs(302).redirectUrlIs(redirectPath);
		
		return verifierChainer;
	}

	public VerifierChainer requestCanEventuallyBeMadeFor(String urlPath) throws ClientProtocolException, IOException, InterruptedException
	{
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.pollServerForStatusCode(url, 200);
		
		return verifierChainer;
	}

	public VerifierChainer requestForUrlReturns(String urlPath, String response) throws ClientProtocolException, IOException
	{
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).responseIs(response);
		
		return verifierChainer;		
	}

}
