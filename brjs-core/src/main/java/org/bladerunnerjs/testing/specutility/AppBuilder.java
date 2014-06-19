package org.bladerunnerjs.testing.specutility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;

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
import org.bladerunnerjs.utility.SimplePageAccessor;


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
		File appExportDir = new File(targetDir, app.getName());
		app.build( appExportDir );
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuiltAsWar(File targetDir) throws Exception {
		File warExportFile = new File(targetDir, app.getName()+".war");
		warExportFile.getParentFile().mkdir();
		app.buildWar( warExportFile );
		
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
	
	public BuilderChainer hasReceivedRequest(String requestPath) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException 
	{
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		app.handleLogicalRequest(requestPath, responseOutput, new SimplePageAccessor());
		
		return builderChainer;	
	}
}
