package org.bladerunnerjs.api.spec.engine;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.bladerunnerjs.api.appserver.ApplicationServer;


public class AppServerCommander extends ModelCommander
{
	
	ApplicationServer appServer;
	BuilderChainer chainer;
	SpecTest specTest;
	
	public AppServerCommander(SpecTest specTest, ApplicationServer appServer)
	{
		super(specTest);
		this.appServer = appServer;
		chainer = new BuilderChainer(specTest);
		this.specTest = specTest;
	}

	public BuilderChainer started() throws Exception
	{
		call(new Command() {
			public void call() throws Exception {
				appServer.start();
			}
		});
		
		return chainer;
	}
	
	public BuilderChainer stopped() throws Exception
	{
		call(new Command() {
			public void call() throws Exception {
				appServer.stop();
			}
		});
		
		return chainer;
	}

	public BuilderChainer requestIsMadeFor(String urlPath, StringBuffer response) throws Exception
	{
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.whenRequestMadeTo(url).storeContentIn(response);
		
		return chainer;
	}

	public BuilderChainer requestCanEventuallyBeMadeFor(String urlPath, StringBuffer response) throws ClientProtocolException, IOException, InterruptedException
	{
		String url = getUrl(urlPath);
		response.append( specTest.webappTester.pollServerForStatusCode(url, 200) );
		
		return chainer;	
	}
	
	private String getUrl(String urlPath)
	{
		return String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
	}

}
