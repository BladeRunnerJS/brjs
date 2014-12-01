package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand.Messages.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


public class CreateBladeCommandTest extends SpecTest {
	App app;
	Bladeset bladeset;
	Blade blade;
	Blade badBlade;
	Blade blade1InDefaultBladeset;
	Blade blade2InDefaultBladeset;	
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CreateBladeCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladeset = app.bladeset("bladeset");
			blade = bladeset.blade("blade");
			badBlade = bladeset.blade("!$%$^");
			blade1InDefaultBladeset = app.defaultBladeset().blade("blade1");
			blade2InDefaultBladeset = app.defaultBladeset().blade("blade2");
	}
	
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-blade", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-blade-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-blade", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "app", unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladesetDoesntExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeDoesNotExistException.class, "bladeset", unquoted(bladeset.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladeAlreadyExists() throws Exception {
		given(blade).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, "blade", unquoted(blade.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfBladeNameIsInvalid() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-blade", "app", "bladeset", "!$%$^");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "Blade", badBlade.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "!$%$^", badBlade.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void bladeIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade")).containsFile("fileForBlade.txt")
			.and(brjs.templateGroup("default").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(blade).dirExists()
			.and(logging).containsFormattedConsoleMessage(BLADE_CREATE_SUCCESS_CONSOLE_MSG, "blade")
			.and(logging).containsFormattedConsoleMessage(BLADE_PATH_CONSOLE_MSG, blade.dir().getPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade")).containsFile("fileForBlade.txt")
			.and(brjs.templateGroup("default").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "bladeset", "blade");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void bladeIsCreatedInTheDefaultBladesetIfBladesetNotSpecified() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade")).containsFile("fileForBlade.txt")
			.and(brjs.templateGroup("default").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "default", "blade1");
		then(blade1InDefaultBladeset).dirExists()
			.and(logging).containsFormattedConsoleMessage(BLADE_CREATE_SUCCESS_CONSOLE_MSG, "blade1")
			.and(logging).containsFormattedConsoleMessage(BLADE_PATH_CONSOLE_MSG, blade1InDefaultBladeset.dir().getPath());
	}
	
	@Test
	public void whenASecondBladeIsCreatedInTheDefaultBladesetTheBladesetDirIsntCreatedAgain() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "default", "blade1")
			.and(brjs).runCommand("create-blade", "app", "default", "blade2");
		then(blade1InDefaultBladeset).dirExists()
			.and(blade2InDefaultBladeset).dirExists()
			.and(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void bladeIsCreatedWithTheSpecifiedTemplateLongFlag() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade")).containsFile("fileForBlade.txt")
			.and(brjs.templateGroup("angular").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "default", "blade1", "--template", "angular");
		then(blade1InDefaultBladeset).dirExists()
			.and(blade1InDefaultBladeset).hasFile("fileForBlade.txt");
	}
	
	@Test
	public void bladeIsCreatedWithTheSpecifiedTemplateShortFlag() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade")).containsFile("fileForBlade.txt")
			.and(brjs.templateGroup("angular").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "default", "blade1", "-T", "angular");
		then(blade1InDefaultBladeset).dirExists()
			.and(blade1InDefaultBladeset).hasFile("fileForBlade.txt");
	}
	
	@Test
	public void bladeIsCreatedWithTheSpecifiedTemplateIfMoreTemplatesExist() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade")).containsFile("fileForBladeAngular.txt")
			.and(brjs.templateGroup("angular").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("workbench")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade")).containsFile("fileForBladeDefault.txt")
			.and(brjs.templateGroup("default").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("workbench")).hasBeenCreated()
			.and(brjs.templateGroup("myTemplate").template("blade")).containsFile("fileForBladeMyTemplate.txt")
			.and(brjs.templateGroup("myTemplate").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("myTemplate").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("myTemplate").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "default", "blade1", "--template", "myTemplate");
		then(blade1InDefaultBladeset).dirExists()
			.and(blade1InDefaultBladeset).hasFile("fileForBladeMyTemplate.txt");
	}
	
	@Test
	public void defaultTemplateIsUsedIfNoneSpecifiedAndMultipleTemplatesExist() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade")).containsFile("fileForBladeAngular.txt")
			.and(brjs.templateGroup("angular").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("angular").template("workbench")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade")).containsFile("fileForBladeDefault.txt")
			.and(brjs.templateGroup("default").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("default").template("workbench")).hasBeenCreated()
			.and(brjs.templateGroup("myTemplate").template("blade")).containsFile("fileForBladeMyTemplate.txt")
			.and(brjs.templateGroup("myTemplate").template("blade-test-unit-default")).hasBeenCreated()
			.and(brjs.templateGroup("myTemplate").template("blade-test-acceptance-default")).hasBeenCreated()
			.and(brjs.templateGroup("myTemplate").template("workbench")).hasBeenCreated();
		when(brjs).runCommand("create-blade", "app", "default", "blade1");
		then(blade1InDefaultBladeset).dirExists()
			.and(blade1InDefaultBladeset).hasFile("fileForBladeDefault.txt");
	}
	
	//TODO figure out where to throw the exception
	@Ignore
	@Test
	public void exceptionIsThrownIfSpecifiedTemplateDoesNotExist() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(brjs).containsFile("conf/templates/angular/blade/fileForBladeAngular.txt")
			.and(brjs).containsFile("conf/templates/default/blade/fileForBladeDefault.txt")
			.and(brjs).containsFile("conf/templates/myTemplate/blade/fileForBladeMyTemplate.txt");
		when(brjs).runCommand("create-blade", "app", "default", "blade1", "--template", "nonexistent");
		then(exceptions).verifyException(TemplateInstallationException.class);
	}
	
}
