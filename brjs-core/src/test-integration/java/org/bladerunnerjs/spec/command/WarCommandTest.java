package org.bladerunnerjs.spec.command;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.plugins.commands.standard.WarCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.utility.ServerUtility;
import org.eclipse.jetty.server.Server;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class WarCommandTest extends SpecTest {
	private Server server = new Server(ServerUtility.getTestPort());
	private App app;
	private Aspect aspect;
	private StringBuffer response = new StringBuffer();
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommands(new WarCommand())
			.and(brjs).automaticallyFindsBundlers()
			.and(brjs).automaticallyFindsMinifiers()
			.and(brjs).usesProductionTemplates()
			.and(brjs).hasBeenCreated();
			app = brjs.app("app1");
			aspect = app.aspect("default");
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
		given(app).hasBeenCreated();
		when(brjs).runCommand("war", "app1");
		then(brjs).hasFile("app1.war");
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
		given(aspect).hasClass("appns.Class1")
			.and(aspect).indexPageRefersTo("appns.Class1");
		when(brjs).runCommand("war", "app1", "unminified.war")
			.and(brjs).runCommand("war", "app1", "-m", "closure-whitespace", "minified.war");
		then(brjs).firstFileIsLarger("unminified.war", "minified.war");
	}
	
	// TODO: this currently doesn't work because the usesProductionTemplates() method has not yet been implemented
	@Ignore
	@Test
	public void exportedWarCanBeDeployedOnAnAppServer() throws Exception {
		// TODO: this hasBeenPopulated() can't currently work correctly because we don't have the templates available
		given(app).hasBeenPopulated()
			.and(brjs).commandHasBeenRun("war", "app1")
			.and(server).hasWar("app1.war", "app")
			.and(server).hasStarted();
		when(server).receivesRequestFor("/app", response);
		then(response).containsText("Application succesfully created.");
	}
}
