package com.caplin.cutlass.command.test;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.caplin.cutlass.command.test.testrunner.TestRunnerController;

public class TestCommand implements LegacyCommandPlugin
{
	private TestRunnerController testRunner;
	
	public TestCommand()
	{
		testRunner = new TestRunnerController(TestRunnerController.RunMode.RUN_TESTS);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getCommandName()
	{
		return "test";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Run specified js-test-driver tests.";
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
		if (validArgs(args))
		{
			testRunner.run(args, this);
		}
		else 
		{
			throw new CommandArgumentsException("Invalid arguments provided.", this);
		}
	}
	
	private boolean validArgs(String[] args)
	{
		if (args != null && args.length > 0)
		{
			return true;
		}
		return false;
	}
}
