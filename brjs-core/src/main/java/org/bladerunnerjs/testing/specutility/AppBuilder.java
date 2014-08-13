package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.Locale;
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
		List<Locale> createdLocales = new ArrayList<Locale>();
		for (String locale : locales.split(",")) {
			createdLocales.add( new Locale(locale) );
		}
		app.appConf().setLocales( createdLocales.toArray(new Locale[0]) );
		app.appConf().write();
		
		return builderChainer;
	}

	public BuilderChainer hasLibs(JsLib... libs)
	{
		new AppVerifier(specTest, app).hasLibs(libs);
		
		return builderChainer;
	}
	
	public BuilderChainer hasReceivedRequest(String requestPath) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, IOException, ModelOperationException 
	{
		app.handleLogicalRequest(requestPath, new StaticContentAccessor(app));
		
		return builderChainer;	
	}

	public BuilderChainer hasLocaleCookieName(String cookieName) throws ConfigException
	{
		app.appConf().setLocaleCookieName(cookieName);
		app.appConf().write();
		
		return builderChainer;
	}
}
