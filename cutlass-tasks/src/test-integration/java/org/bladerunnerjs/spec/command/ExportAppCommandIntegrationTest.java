package org.bladerunnerjs.spec.command;

import java.io.File;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.DirNode;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.command.export.ExportApplicationCommand;

public class ExportAppCommandIntegrationTest extends SpecTest
{
	App app;
	Aspect aspect;
	Bladeset bladeset;
	Blade blade;
	DirNode appJars;
	File sdkDir;
	
	@Before
	public void initTestObjects() throws Exception
	{	
		//TODO::have to create brjs first should remove when moved over to core
		given(brjs).hasBeenCreated();
		
		given(brjs).hasCommands(new ExportApplicationCommand(brjs));
			app = brjs.app("myapp");
			aspect = app.aspect("myaspect");
			bladeset = app.bladeset("mybladeset");
			blade = bladeset.blade("myblade");
			sdkDir = new File(brjs.dir(), "sdk");
	}
	
	@Test
	public void exportAppAndVerifyZipContents() throws Exception
	{
		File sdkDir = new File(brjs.dir(), "sdk");
		
		given(app).hasBeenCreated().and(aspect).hasBeenCreated().and(aspect).containsFileWithContents("index.html", "Hello World");
		when(brjs).runCommand("export-app", "myapp");
		then(sdkDir).containsFile("myapp.zip");
		// TODO verify contents
	}
}