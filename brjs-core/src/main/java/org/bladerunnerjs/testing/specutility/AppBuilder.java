package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.zip.ZipFile;

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
import org.bladerunnerjs.utility.FileUtility;


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
	
	public BuilderChainer hasBeenBuiltAsWar(File targetDir) throws Exception {
		app.build(targetDir, true);
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuilt(File targetDir, MutableLong versionNumber) throws Exception {
		hasBeenBuilt(targetDir);
		
		for(File dir : new File(targetDir, app.getName() + "/v").listFiles()) {
			versionNumber.setValue(Long.valueOf(dir.getName()));
			break;
		}
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenBuiltAsWar(File targetDir, MutableLong versionNumber) throws Exception {
		hasBeenBuiltAsWar(targetDir);
		
		File warFile = new File(targetDir, app.getName() + ".war");
		File tempDir = FileUtility.createTemporaryDirectory(AppBuilder.class.getSimpleName());
		FileUtility.unzip(new ZipFile(warFile), tempDir);
		
		// TODO: we need unzip before we can figure out what the version number is
		for(File dir : new File(tempDir, "v").listFiles()) {
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
