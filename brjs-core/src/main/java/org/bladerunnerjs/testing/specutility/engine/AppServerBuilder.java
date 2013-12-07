package org.bladerunnerjs.testing.specutility.engine;

import java.io.IOException;

import javax.servlet.Servlet;

import org.apache.http.client.ClientProtocolException;
import org.bladerunnerjs.appserver.ApplicationServer;
import org.bladerunnerjs.appserver.BRJSApplicationServer;
import org.bladerunnerjs.model.App;


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

	public BuilderChainer appHasServlet(App app, Servlet servlet, String path) throws Exception
	{
		if ( !(appServer instanceof BRJSApplicationServer) )
		{
			throw new RuntimeException("appHasServlet can only be called when application server is an instance of  " + BRJSApplicationServer.class.getSimpleName());
		}
		BRJSApplicationServer brjsAppServer = (BRJSApplicationServer) appServer;
		brjsAppServer.addServlet(app, servlet, path);
		
		return builderChainer;
	}

}
