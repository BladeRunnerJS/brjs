package com.caplin.cutlass.command.create;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.output.NullOutputStream;
import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateLibraryCommand;

import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.testing.utility.MockLoggerFactory;

import com.caplin.cutlass.testing.BRJSTestFactory;

public class CreateLibraryCommandTest
{
	private static final File testResourcesSdkDir = new File("src/test/resources/CreateLibraryCommand");
	
	private CreateLibraryCommand createLibraryCommand;
	private BRJS brjs;
	
	@Before
	public void setup() throws IOException
	{
		File tempDirRoot = FileUtility.createTemporaryDirectory(this.getClass().getSimpleName());
		FileUtils.copyDirectory(testResourcesSdkDir, tempDirRoot);
		brjs = BRJSTestFactory.createBRJS(tempDirRoot, new MockLoggerFactory(), new PrintStream(new NullOutputStream()));
		createLibraryCommand = new CreateLibraryCommand();
		createLibraryCommand.setBRJS(brjs);
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectNumberArgumentsArePassedIn() throws Exception
	{
		createLibraryCommand.doCommand(new String[] { "application" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfIncorrectApplicationNameIsPassedIn() throws Exception
	{
		createLibraryCommand.doCommand(new String[] { "non-existent-app", "the-lib" });
	}
	
	@Test (expected=CommandArgumentsException.class)
	public void commandThrowsErrorIfLibraryAlreadyExists() throws Exception
	{
		createLibraryCommand.doCommand(new String[] { "the-app", "pre-existing-lib" });
	}
	
	@Test
	public void commandCopiesOverTemplateBladeset() throws Exception
	{
		JsLib newLib = brjs.app("the-app").jsLib("the-lib");
		
		assertFalse(newLib.dirExists());
		
		createLibraryCommand.doCommand(new String[] { "the-app", "the-lib", "novox" });
		
		assertTrue(newLib.dirExists());
		assertTrue(newLib.containsFile("stuff.txt"));
		assertTrue(newLib.containsFile("novox/stuff.txt"));
	}
	
	@Test
	public void commandIsAutomaticallyDiscovered() throws Exception
	{
		JsLib newLib = brjs.app("the-app").jsLib("the-lib");
		
		assertFalse(newLib.dirExists());
		
		brjs.runCommand( new String[] { createLibraryCommand.getCommandName(), "the-app", "the-lib", "novox" } );
		
		assertTrue(newLib.dirExists());
		assertTrue(newLib.containsFile("stuff.txt"));
		assertTrue(newLib.containsFile("novox/stuff.txt"));
	}
	
}
