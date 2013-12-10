package com.caplin.cutlass.command.test;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;

import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.caplin.cutlass.command.test.testrunner.TestRunnerController;

public class TestServerCommand extends AbstractPlugin implements LegacyCommandPlugin
{
	private TestRunnerController testRunner;
	
	public TestServerCommand()
	{
		testRunner = new TestRunnerController(TestRunnerController.RunMode.RUN_SERVER);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getCommandName()
	{
		return "test-server";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Start a js-test-driver server which can then be used to run tests.";
	}
	
	@Override 
	public String getCommandUsage()
	{
		return testRunner.getUsage();
	}

	@Override
	public String getCommandHelp() {
		return testRunner.getHelp();
	}
	
	@Override
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException
	{
		testRunner.run(args, this);
	}
}
