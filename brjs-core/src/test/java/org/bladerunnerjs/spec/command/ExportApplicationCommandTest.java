package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.ExportApplicationCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;

public class ExportApplicationCommandTest extends SpecTest {
	private App app;
	private Aspect aspect;
	private Bladeset bladeset;
	private Blade blade;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new ExportApplicationCommand())
			.and(brjs).automaticallyFindsBundlerPlugins()
			.and(brjs).automaticallyFindsMinifierPlugins()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			bladeset = app.bladeset("bs");
			blade = bladeset.blade("b1");
	}
	
	@Test
	public void exceptionIsThrownIfAppDoesNotExist() throws Exception {
		when(brjs).runCommand("export-app", "doesNotExist");
		then(exceptions).verifyException(CommandArgumentsException.class, unquoted("Could not find application 'doesNotExist'"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfNoAppNameIsProvided() throws Exception {
		when(brjs).runCommand("export-app");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTooManyParametersAreProvidedd() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("export-app", "app1", "bla", "blabla");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: blabla"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void appCanBeExportedWithDisclaimerForAppLibraryJsClasses() throws Exception {
		given(app).hasBeenCreated()
		.and(aspect).classFileHasContent("appns.Class1", "default aspect src")
		.and(app).containsFileWithContents("libs/appLib/Class1.js", "library class")
		.and(app).containsFile("WEB-INF/lib/a.jar")
		.and(app).containsFile("WEB-INF/lib/b.jar")
		.and(app).containsFile("WEB-INF/lib/c.jar")
		.and(app).containsFile("WEB-INF/web.xml")
		.and(app).containsFile("WEB-INF/classes/hibernate.cfg.xml");			
	when(brjs).runCommand("export-app", "app1", "--banner", "DISCLAIMER!")
		.and(brjs).zipFileIsExtractedTo("generated/exported-apps/app1.zip", "generated/exported-apps/app1");
	then(brjs).fileHasContents("generated/exported-apps/app1/app1/default-aspect/src/appns/Class1.js", "default aspect src")
		.and(brjs).fileHasContents("generated/exported-apps/app1/app1/libs/appLib/Class1.js", 
			"/*" + "\n" +
			"DISCLAIMER!" + "\n" +
			"*/" + "\n" +
			"\n" +
			"library class");
	}
	
	@Test
	public void appCanBeExportedAndZipContentsAreCorrect() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).classFileHasContent("appns.Class1", "default aspect src")
			.and(app).containsFile("WEB-INF/lib/include1.jar")
			.and(app).containsFile("WEB-INF/lib/include2.jar")
			.and(app).containsFile("WEB-INF/lib/brjs-core.jar")
			.and(app).containsFile("WEB-INF/web.xml")
			.and(app).containsFile("WEB-INF/classes/hibernate.cfg.xml");			
		when(brjs).runCommand("export-app", "app1")
			.and(brjs).zipFileIsExtractedTo("generated/exported-apps/app1.zip", "generated/exported-apps/app1");
		then(brjs).hasFile("generated/exported-apps/app1.zip")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/default-aspect/src/appns/Class1.js")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/WEB-INF/lib/include1.jar")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/WEB-INF/lib/include2.jar")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/WEB-INF/web.xml")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/WEB-INF/classes/hibernate.cfg.xml")
			.and(brjs).doesNotHaveFile("generated/exported-apps/app1/app1/WEB-INF/lib/brjs-core.jar");
	}
	
	@Test
	public void maintainsJsStyleFileWhenAppIsExported() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).hasBeenPopulated()
			.and(aspect).classFileHasContent("appns.Class1", "default aspect src")
			.and(aspect).containsFile("src/.js-style")
			.and(bladeset).hasBeenPopulated()
			.and(bladeset).containsFile("src/.js-style")
			.and(blade).hasBeenPopulated()
			.and(blade).containsFile("src/.js-style");
		when(brjs).runCommand("export-app", "app1")
			.and(brjs).zipFileIsExtractedTo("generated/exported-apps/app1.zip", "generated/exported-apps/app1");
		then(brjs).hasFile("generated/exported-apps/app1/app1/default-aspect/src/.js-style")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/bs-bladeset/src/.js-style")
			.and(brjs).hasFile("generated/exported-apps/app1/app1/bs-bladeset/blades/b1/src/.js-style");
	}
	
	@Test
	public void dotSvnDirsNotExported() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).hasBeenPopulated()
			.and(aspect).classFileHasContent("appns.Class1", "default aspect src")
			.and(aspect).containsFile("src/.svn/some-file");
		when(brjs).runCommand("export-app", "app1")
			.and(brjs).zipFileIsExtractedTo("generated/exported-apps/app1.zip", "generated/exported-apps/app1");
		then(brjs).hasFile("generated/exported-apps/app1/app1/default-aspect/src/appns/Class1.js")
			.and(brjs).doesNotHaveDir("generated/exported-apps/app1/app1/default-aspect/src/.svn");
	}
	
	@Test
	public void anOptionalExportPathCanBeProvided() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).classFileHasContent("appns.Class1", "default aspect src");			
		when(brjs).runCommand("export-app", "app1", "target")
			.and(brjs).zipFileIsExtractedTo("sdk/target/app1.zip", "sdk/target/app1");
		then(brjs).hasFile("sdk/target/app1.zip")
		.and(brjs).hasFile("sdk/target/app1/app1/default-aspect/src/appns/Class1.js");
	}
	
	@Test
	public void anOptionalAbsoluteExportPathCanBeProvided() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).classFileHasContent("appns.Class1", "default aspect src")
			.and(brjs).hasDir("target");
		when(brjs).runCommand("export-app", "app1", brjs.file("target").getAbsolutePath())
			.and(brjs).zipFileIsExtractedTo("target/app1.zip", "target/app1");
		then(brjs).hasFile("target/app1.zip")
			.and(brjs).hasFile("target/app1/app1/default-aspect/src/appns/Class1.js");
	}

}
