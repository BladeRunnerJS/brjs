package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.model.engine.AbstractNode.Messages.*;
import static org.bladerunnerjs.plugin.commands.standard.CreateBladesetCommand.Messages.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.model.exception.name.InvalidDirectoryNameException;
import org.bladerunnerjs.api.model.exception.template.TemplateNotFoundException;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.TemplateGroup;
import org.bladerunnerjs.plugin.commands.standard.CreateBladesetCommand;
import org.junit.Before;
import org.junit.Test;


public class CreateBladesetCommandTest extends SpecTest {
	App app;
	Bladeset bladeset;
	Bladeset badBladeset;
	TemplateGroup angularTemplates;
	TemplateGroup defaultTemplates;
	TemplateGroup myTemplateTemplates;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CreateBladesetCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			bladeset = app.bladeset("bladeset");
			badBladeset = app.bladeset("bladeset#$@/");
			angularTemplates = brjs.sdkTemplateGroup("angular");
			defaultTemplates = brjs.sdkTemplateGroup("default");
			myTemplateTemplates = brjs.sdkTemplateGroup("myTemplate");
	}
	
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("create-bladeset", "a");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'new-bladeset-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("create-bladeset", "a", "b", "c");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: c"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheAppDoesntExist() throws Exception {
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(exceptions).verifyException(NodeDoesNotExistException.class, unquoted(app.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfTheBladesetAlreadyExists() throws Exception {
		given(bladeset).hasBeenCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(exceptions).verifyException(NodeAlreadyExistsException.class, unquoted(bladeset.getClass().getSimpleName()))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfBladesetNameIsInvalid() throws Exception {
		given(bladeset).hasBeenCreated()
			.and(defaultTemplates).templateGroupCreated()
			.and(logging).enabled();
		when(brjs).runCommand("create-bladeset", "app", "bladeset#$@/");
		then(logging).errorMessageReceived(NODE_CREATION_FAILED_LOG_MSG, "Bladeset", badBladeset.dir().getPath())
			.and(exceptions).verifyException(InvalidDirectoryNameException.class, "bladeset#$@/", badBladeset.dir().getPath())
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void bladeIsCreatedWhenAllArgumentsAreValid() throws Exception {
		given(app).hasBeenCreated()
			.and(defaultTemplates).templateGroupCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(bladeset).dirExists()
			.and(logging).containsFormattedConsoleMessage(BLADESET_CREATE_SUCCESS_CONSOLE_MSG, "bladeset")
			.and(logging).containsFormattedConsoleMessage(BLADESET_PATH_CONSOLE_MSG, bladeset.dir().getPath());
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(defaultTemplates).templateGroupCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(exceptions).verifyNoOutstandingExceptions();
	}
	
	@Test
	public void bladesetIsCreatedWithTheSpecifiedTemplate() throws Exception {
		given(app).hasBeenCreated()
			.and(angularTemplates).templateGroupCreated()
			.and(angularTemplates.template("bladeset")).containsFile("fileForBladeset.txt");
		when(brjs).runCommand("create-bladeset", "app", "bladeset", "--template", "angular");
		then(bladeset).dirExists()
			.and(bladeset).hasFile("fileForBladeset.txt");
	}
	
	@Test
	public void bladesetIsCreatedWithTheSpecifiedTemplateIfMoreTemplatesExist() throws Exception {
		given(app).hasBeenCreated()
			.and(angularTemplates).templateGroupCreated()
			.and(angularTemplates.template("bladeset")).containsFile("fileForBladesetAngular.txt")
			.and(defaultTemplates).templateGroupCreated()
			.and(defaultTemplates.template("bladeset")).containsFile("fileForBladesetDefault.txt")
			.and(myTemplateTemplates).templateGroupCreated()
			.and(myTemplateTemplates.template("bladeset")).containsFile("fileForBladesetMyTemplate.txt");
		when(brjs).runCommand("create-bladeset", "app", "bladeset", "--template", "myTemplate");
		then(bladeset).dirExists()
			.and(bladeset).hasFile("fileForBladesetMyTemplate.txt");
	}
	
	@Test
	public void defaultTemplateIsUsedIfNoneSpecifiedAndMultipleTemplatesExist() throws Exception {
		given(app).hasBeenCreated()
			.and(angularTemplates).templateGroupCreated()
			.and(angularTemplates.template("bladeset")).containsFile("fileForBladesetAngular.txt")
			.and(defaultTemplates).templateGroupCreated()
			.and(defaultTemplates.template("bladeset")).containsFile("fileForBladesetDefault.txt")
			.and(myTemplateTemplates).templateGroupCreated()
			.and(myTemplateTemplates.template("bladeset")).containsFile("fileForBladesetMyTemplate.txt");
		when(brjs).runCommand("create-bladeset", "app", "bladeset");
		then(bladeset).dirExists()
			.and(bladeset).hasFile("fileForBladesetDefault.txt");
	}
	
	@Test
	public void exceptionIsThrownIfSpecifiedTemplateDoesNotExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset", "--template", "nonexistent");
		then(exceptions).verifyException(TemplateNotFoundException.class);
	}
	
	public void emptyFilesAreCreatedIfTemplateForImplicitlyPopulatedTestUnitDefaultDoesNotExist() throws Exception {
		given(app).hasBeenCreated();
		when(brjs).runCommand("create-bladeset", "app", "bladeset", "--template", "angular");
		then(bladeset).hasDir("test-acceptance")
			.and(bladeset.file("test-acceptance")).isEmpty();
	}
}
