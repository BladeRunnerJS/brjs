package com.caplin.cutlass.command;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.AbstractPlugin;
import org.bladerunnerjs.plugin.command.CommandPlugin;

/* 
 * This class does not actually test the abstract CommandTask class.
 * 
 * It contains tests that verify whether a class extending CommandTask
 * does not have an empty help message, description etc.
 * 
 * It should be extended by any tests that are testing a class that implements CommandTask
 * and the commandTask variable replaced with an instance of the class being tested.
 */
public class CommandTaskTest
{
	protected CommandPlugin commandTask;
	protected ConsoleWriter out;
	
	@Before
	public void setup() throws IOException
	{
		commandTask = new DummyCommandTask(mock(File.class), "test");
	}
	
	@Test
	public void testDescriptionIsNotEmpty() throws Exception
	{
		assertTrue(commandTask.getCommandDescription().toString().length() > 0);
	}

	private class DummyCommandTask extends AbstractPlugin implements LegacyCommandPlugin
	{
		public DummyCommandTask(File sdkBaseDir, String commandName)
		{
		}
		
		@Override
		public void setBRJS(BRJS brjs)
		{
		}
		
		@Override
		public String getCommandName()
		{
			return "DummyCommandTask.getCommandName";
		}
		
		@Override
		public String getCommandDescription()
		{
			return "DummyCommandTask.getCommandDescription";
		}
		
		@Override
		public String getCommandUsage()
		{
			return "DummyCommandTask.getCommandUsage";
		}

		@Override
		public String getCommandHelp() {
			return getCommandUsage();
		}
		
		@Override
		public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException
		{
			out.println("DummyCommandTask.doCommand");
		}
	}

}
