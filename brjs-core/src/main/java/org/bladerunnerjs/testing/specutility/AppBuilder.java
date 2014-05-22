package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.io.UnsupportedEncodingException;

import org.apache.commons.lang3.mutable.MutableLong;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
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
	
	public BuilderChainer hasBeenPopulated(String requirePrefix) throws Exception
	{
		app.populate(requirePrefix);
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuilt(File targetDir) throws Exception {
		app.build(targetDir);
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuilt(File targetDir, MutableLong versionNumber) throws Exception {
		hasBeenBuilt(targetDir);
		
		for(File dir : new File(targetDir, "v").listFiles()) {
			versionNumber.setValue(Long.valueOf(dir.getName()));
			break;
		}
		
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

	public BuilderChainer hasReceivedRequst(String requestPath) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException 
	{
		AppCommander appCommander = new AppCommander(this.specTest, this.app);
		appCommander.requestReceived(requestPath, new StringBuffer());
			
		return builderChainer;	
	}
}
