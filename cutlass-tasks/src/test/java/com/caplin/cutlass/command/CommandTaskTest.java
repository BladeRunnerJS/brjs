package com.caplin.cutlass.command;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.TestModelAccessor;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.base.AbstractPlugin;

/* 
 * This class does not actually test the abstract CommandTask class.
 * 
 * It contains tests that verify whether a class extending CommandTask
 * does not have an empty help message, description etc.
 * 
 * It should be extended by any tests that are testing a class that implements CommandTask
 * and the commandTask variable replaced with an instance of the class being tested.
 */
public class CommandTaskTest extends TestModelAccessor
{
	protected CommandPlugin commandTask;
	
	@Before
	public void setup() throws Exception
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
		private Logger logger;

		public DummyCommandTask(File sdkBaseDir, String commandName)
		{
		}
		
		@Override
		public void setBRJS(BRJS brjs)
		{
			this.logger = brjs.logger(this.getClass());
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
		public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
		{
			logger.println("DummyCommandTask.doCommand");
			return 0;
		}
	}

}
