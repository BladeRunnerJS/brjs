package org.bladerunnerjs.api.spec.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.NodeBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.StaticContentAccessor;


public class AppBuilder extends NodeBuilder<App> {
	private final App app;
	private SpecTest specTest;
	
	public AppBuilder(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
		this.specTest = modelTest;
	}
	
	public BuilderChainer hasBeenPopulated(String requirePrefix, String templateGroup) throws Exception
	{
		app.populate(requirePrefix, templateGroup);
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuilt(MemoizedFile targetDir) throws Exception {
		app.build( targetDir );
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuilt(File targetDir) throws Exception {
		app.build( specTest.brjs.getMemoizedFile(targetDir) );
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuiltAsWar(MemoizedFile targetDir) throws Exception {
		MemoizedFile warExportFile = targetDir.file(app.getName()+".war");
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
		return hasReceivedRequest(requestPath, null);	
	}
	
	public BuilderChainer hasReceivedRequest(String requestPath, StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, IOException, ModelOperationException 
	{
		ResponseContent content = app.requestHandler().handleLogicalRequest(requestPath, new StaticContentAccessor(app));
		if (response == null) {
			return builderChainer;
		}
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		content.write( baos );
		response.append(baos.toString());
		
		return builderChainer;	
	}

	public BuilderChainer hasLocaleCookieName(String cookieName) throws ConfigException
	{
		app.appConf().setLocaleCookieName(cookieName);
		app.appConf().write();
		
		return builderChainer;
	}
}
