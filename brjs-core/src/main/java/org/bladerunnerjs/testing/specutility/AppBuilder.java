package org.bladerunnerjs.testing.specutility;

import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class AppBuilder extends NodeBuilder<App> {
	private final App app;
	private SpecTest specTest;
	
	public AppBuilder(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
		this.specTest = modelTest;
	}
	
	public BuilderChainer hasBeenPopulated(String appNamespace) throws Exception
	{
		app.populate(appNamespace);
		
		return builderChainer;
	}

	public BuilderChainer hasBeenDeployed() throws TemplateInstallationException
	{
		app.deploy();
		
		return builderChainer;
	}

	public BuilderChainer hasSupportedLocales(String locales) throws ConfigException
	{
		app.appConf().setLocales( locales );
		
		return builderChainer;
	}

	public BuilderChainer hasLibs(JsLib... libs)
	{
		new AppVerifier(specTest, app).hasLibs(libs);
		
		return builderChainer;
	}

	public BuilderChainer hasReceivedRequst(String requestPath) throws MalformedRequestException, ResourceNotFoundException, BundlerProcessingException, UnsupportedEncodingException 
	{
		AppCommander appCommander = new AppCommander(this.specTest, this.app);
		appCommander.requestReceived(requestPath, new StringBuffer());
			
		return builderChainer;	
	}
}
