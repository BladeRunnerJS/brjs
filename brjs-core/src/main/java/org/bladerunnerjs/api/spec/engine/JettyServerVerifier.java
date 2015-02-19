package org.bladerunnerjs.api.spec.engine;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.eclipse.jetty.server.Server;

public class JettyServerVerifier {
	
	private SpecTest specTest;
	private VerifierChainer verifierChainer;
	private Server jettyServer;


	public JettyServerVerifier(SpecTest specTest, Server jettyServer) {
		this.specTest = specTest;
		verifierChainer = new VerifierChainer(specTest);
		this.jettyServer = jettyServer;
	}

	public VerifierChainer requestForUrlReturns(String urlPath, String response) throws ClientProtocolException, IOException
	{
		String url = getUrl(urlPath);
		specTest.webappTester.whenRequestMadeTo(url,false).responseIs(response);
		
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
		return String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, jettyServer.getConnectors()[0].getPort(), urlPath);
	}
}
