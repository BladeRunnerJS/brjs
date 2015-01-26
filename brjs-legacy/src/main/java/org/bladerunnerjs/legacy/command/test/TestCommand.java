package org.bladerunnerjs.legacy.command.test;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.base.AbstractPlugin;
import org.bladerunnerjs.legacy.command.LegacyCommandPlugin;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunnerController;

public class TestCommand extends AbstractPlugin implements LegacyCommandPlugin
{
	private TestRunnerController testRunner;
	private BRJS brjs;
	
	public TestCommand()
	{
		testRunner = new TestRunnerController(TestRunnerController.RunMode.RUN_TESTS);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
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
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		if (validArgs(args))
		{
			return testRunner.run(brjs, args, this);
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
