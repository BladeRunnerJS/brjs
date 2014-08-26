package org.bladerunnerjs.spec.command;

import static org.bladerunnerjs.plugin.plugins.commands.standard.CopyThemeCommand.Messages.*;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CopyThemeCommand;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.junit.Before;
import org.junit.Test;


public class CopyThemeCommandTest extends SpecTest {
	App app;
	Aspect aspect;
	Bladeset bladeset1;
	Bladeset bladeset2;
	Blade blade11;
	Blade blade12;
	Blade blade21;
	
	@Before
	public void initTestObjects() throws Exception
	{
		given(brjs).hasCommandPlugins(new CopyThemeCommand())
			.and(brjs).hasBeenCreated();
			app = brjs.app("app");
			aspect = app.aspect("aspect");
			bladeset1 = app.bladeset("bladeset1");
			bladeset2 = app.bladeset("bladeset2");
			blade11 = bladeset1.blade("blade11");
			blade12 = bladeset1.blade("blade12");
			blade21 = bladeset1.blade("blade21");
	}	
	
	@Test
	public void exceptionIsThrownIfThereAreTooFewArguments() throws Exception {
		when(brjs).runCommand("copy-theme", "a", "b");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Parameter 'copy-to-theme-name' is required"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void exceptionIsThrownIfThereAreTooManyArguments() throws Exception {
		when(brjs).runCommand("copy-theme", "a", "b", "c", "d");
		then(exceptions).verifyException(ArgumentParsingException.class, unquoted("Unexpected argument: d"))
			.whereTopLevelExceptionIs(CommandArgumentsException.class);
	}
	
	@Test
	public void themeIsNotCopiedWhenDestinationAlreadyExists() throws Exception{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(bladeset1).hasBeenCreated()
			.and(blade11).hasBeenCreated()
			.and(blade11).containsFile("themes/red/style.css")
			.and(blade11).containsFile("themes/blue/secondStyle.css");
		when(brjs).runCommand("copy-theme", "app", "red", "blue");
		then(blade11).doesNotHaveFile("themes/blue/style.css");
	}
	
	@Test
	public void styleCopiedInOneBladeProperly() throws Exception{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(bladeset1).hasBeenCreated()
			.and(blade11).hasBeenCreated()
			.and(blade11).containsFile("themes/red/style.css");
		when(brjs).runCommand("copy-theme", "app", "red", "blue");
		then(blade11).hasFile("themes/blue/style.css");
	}	
	
	@Test
	public void styleCopiedInAllThemeDirectoriesAcrossSpecifiedApp() throws Exception{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(aspect).hasBeenCreated()
			.and(aspect).containsFile("themes/red/style.css")
			.and(bladeset1).hasBeenCreated()
			.and(bladeset2).hasBeenCreated()
			.and(bladeset1).containsFile("themes/red/style.css")
			.and(blade11).hasBeenCreated()
			.and(blade21).hasBeenCreated()
			.and(blade11).containsFile("themes/red/style.css")
			.and(blade21).containsFile("themes/red/style.css");
		when(brjs).runCommand("copy-theme", "app", "red", "blue");
		then(aspect).hasFile("themes/blue/style.css")
			.and(bladeset1).hasFile("themes/blue/style.css")
			.and(blade11).hasFile("themes/blue/style.css")
			.and(blade21).hasFile("themes/blue/style.css");
	}
	
	@Test
	public void existingDestinationThemeDirectoryThrowsWarningWithWarning() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(logging).enabled()
			.and(app).hasBeenCreated()
			.and(bladeset1).hasBeenCreated()
			.and(blade11).hasBeenCreated()
			.and(blade11).containsFile("themes/red/style.css")
			.and(blade11).containsFolder("themes/blue");
		when(brjs).runCommand("copy-theme", "app", "red", "blue");
		then(logging).warnMessageReceived(THEME_FOLDER_EXISTS, "apps/app/bladeset1-bladeset/blades/blade11/themes/blue");
	}
	
	@Test
	public void showsWarningExplanationIfSourceThemeDoesNotExist() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(logging).enabled()
			.and(app).hasBeenCreated()
			.and(bladeset1).hasBeenCreated()
			.and(blade11).hasBeenCreated()
			.and(blade11).containsFile("themes/existing-theme/style.css");
		when(brjs).runCommand("copy-theme", "app", "non-existant-theme", "copied-theme");
		then(logging).warnMessageReceived(THEME_FOLDER_DOES_NOT_EXIST, "non-existant-theme");
	}
	
	@Test
	public void onlySpecifiedThemeFolderIsCopied() throws Exception {
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(bladeset1).hasBeenCreated()
			.and(bladeset2).hasBeenCreated()
			.and(bladeset1).containsFile("themes/red/style.css")
			.and(bladeset2).containsFile("themes/red/style.css");
		when(brjs).runCommand("copy-theme", "app/bladeset1-bladeset/themes", "red", "blue");
		then(bladeset1).hasFile("themes/blue/style.css")
			.and(bladeset2).doesNotHaveFile("themes/blue/style.css");
	}
	
	@Test
	public void successfulThemeCopyGivesSuccessMessage() throws Exception{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated()
			.and(bladeset1).hasBeenCreated()
			.and(bladeset1).containsFile("themes/red/style.css");
		when(brjs).runCommand("copy-theme", "app", "red", "blue");
		then(bladeset1).hasFile("themes/blue/style.css")
			.and(bladeset2).doesNotHaveFile("themes/blue/style.css");
		then(logging).containsFormattedConsoleMessage( COPY_THEME_SUCCESS_CONSOLE_MSG, "bladeset1-bladeset/themes/red", "bladeset1-bladeset/themes/blue");
	}
	
	@Test
	public void commandIsAutomaticallyLoaded() throws Exception
	{
		given(brjs).hasBeenAuthenticallyCreated()
			.and(app).hasBeenCreated();
		when(brjs).runCommand("copy-theme", "app", "red", "blue");
		then(exceptions).verifyNoOutstandingExceptions();
	}
}
