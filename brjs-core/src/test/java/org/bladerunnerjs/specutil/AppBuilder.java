package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.NodeBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AppBuilder extends NodeBuilder<App> {
	private final App app;
	
	public AppBuilder(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
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
}
