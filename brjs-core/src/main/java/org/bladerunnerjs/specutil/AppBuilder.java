package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


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
}
