package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.WarCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WarCommandTest extends SpecTest {
	private App app;
	private Aspect aspect, mobileAspect;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new WarCommand())
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
			mobileAspect = app.aspect("mobile");
	}
	
	@Test
	public void exceptionIsThrownIfNoAppIsProvided() throws Exception {
		when(brjs).runCommand("war");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'app-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTooManyParametersAreProvidedd() throws Exception {
		when(brjs).runCommand("war", "app", "app.war", "x");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: x"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exportingANonExistentAppCausesAnException() throws Exception {
		when(brjs).runCommand("war", "app1");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "app1");
	}
	
	@Test
	public void exportingAnAppWithoutSpecifyingAnWarLocationCausesAWarToBeCreatedWithTheSameNameAsTheApp() throws Exception {
		given(app).hasBeenCreated()
			.and(aspect).hasClass("Class1")
			.and(aspect).indexPageHasContent("default aspect")
			.and(mobileAspect).hasClass("Class1")
			.and(mobileAspect).indexPageHasContent("mobile aspect");
		when(brjs).runCommand("war", "app1");
		then(brjs).hasFile("app1.war");
	}
	
	@Test
	public void exportingAnAppWithMultipleLocales() throws Exception {
		given(app).hasBeenCreated()
			.and(app).containsFileWithContents("app.conf", "requirePrefix: app1\nlocales: en_EN, de_DE")
			.and(aspect).hasClass("Class1")
			.and(aspect).indexPageHasContent("default aspect")
			.and(mobileAspect).hasClass("Class1")
			.and(mobileAspect).indexPageHasContent("mobile aspect");
		when(brjs).runCommand("war", "app1");
		then(brjs).hasFile("app1.war")
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void specifyingAWarLocationWhenExportingAnAppCausesItToBeCreatedAtTheGivenLocation() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("war", "app1", "myapp.war");
		then(brjs).hasFile("myapp.war");
	}
	
	@Test
	public void specifyingAnAbolusteWarLocationAlsoWorks() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("war", "app1", brjs.workingDir().file("myapp.war").getAbsolutePath());
		then(brjs).hasFile("myapp.war");
	}
	
	@Test
	public void specifyingAWarLocationWithoutAWarSuffixCausesTheSuffixToBeAutomaticallyAdded() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("war", "app1", "myapp");
		then(brjs).hasFile("myapp.war");
	}
	
	@Test
	public void specifyingAnAbsoluteWarLocationWithoutAWarSuffixAlsoWorks() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("war", "app1", brjs.workingDir().file("myapp").getAbsolutePath());
		then(brjs).hasFile("myapp.war");
	}
	
	@Test
	public void providingADirectoryAsTheWarLocationCausesTheWarToBeWrittenIntoThatDirectory() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).containsFolder("target-dir");
		when(brjs).runCommand("war", "app1", "target-dir");
		then(brjs).hasFile("target-dir/app1.war");
	}
	
	@Test
	public void providingAnAbsoluteDirectoryAsTheWarLocationAlsoWorks() throws Exception {
		given(app).hasBeenCreated()
			.and(brjs).containsFolder("target-dir");
		when(brjs).runCommand("war", "app1", brjs.workingDir().file("target-dir").getAbsolutePath());
		then(brjs).hasFile("target-dir/app1.war");
	}
	
	// TODO: this currently doesn't work because we are creating one bundler for every minification level available, whether we need it or not
	@Ignore
	@Test
	public void minifyingCausesTheWarToBeSmaller() throws Exception {
		given(app).hasBeenCreated()
		.and(aspect).classFileHasContent("appns/Class1", "var reallyLongStringThatWillBeMinifiedToAFarSmallerSize = 'foo';")
			.and(aspect).indexPageHasContent(
					"<head><@js.bundle prod-minifier='combined'@/></head>\n" +
					"require('appns/Class1');");
		when(brjs).runCommand("war", "app1", "unminified.war")
			.and(aspect).indexPageHasContent(
					"<head><@js.bundle prod-minifier='closure-simple'@/></head>\n" +
					"require('appns/Class1');")
			.and(brjs).runCommand("war", "app1", "-m", "closure-whitespace", "minified.war");
		then(brjs).firstFileIsLarger("unminified.war", "minified.war");
	}
}
