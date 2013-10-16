package org.bladerunnerjs.specutil.engine;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.bladerunnerjs.model.appserver.ApplicationServer;


public class AppServerBuilder
{

	BuilderChainer builderChainer;
	ApplicationServer appServer;
	SpecTest specTest;
	
	public AppServerBuilder(SpecTest specTest, ApplicationServer appServer)
	{
		this.appServer = appServer;
		this.specTest = specTest;
		builderChainer = new BuilderChainer(specTest);
	}

	public BuilderChainer started() throws Exception
	{
		appServer.start();
		
		return builderChainer;
	}
	
	public BuilderChainer stopped() throws Exception
	{
		appServer.stop();
		
		return builderChainer;
	}

	public BuilderChainer requestTimesOutFor(String urlPath) throws ClientProtocolException, IOException
	{
		String url = String.format("%s:%s%s", SpecTest.HTTP_REQUEST_PREFIX, specTest.appServerPort, urlPath);
		specTest.webappTester.requestTimesOut(url);
		
		return builderChainer;
	}

}
