package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.core.plugin.command.standard.CreateApplicationCommand;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.junit.Before;

public class WARCommandTest extends SpecTest {
	App app;
	App badApp;
	DirNode appJars;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(pluginLocator).hasCommand(new CreateApplicationCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			badApp = brjs.app("app#$@/");
			appJars = brjs.appJars();
	}
	

}
