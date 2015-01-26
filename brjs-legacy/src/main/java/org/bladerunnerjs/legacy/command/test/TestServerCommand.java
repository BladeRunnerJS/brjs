package org.bladerunnerjs.legacy.command.test;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.base.AbstractPlugin;
import org.bladerunnerjs.legacy.command.LegacyCommandPlugin;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunnerController;

public class TestServerCommand extends AbstractPlugin implements LegacyCommandPlugin
{
	private TestRunnerController testRunner;
	private BRJS brjs;
	
	public TestServerCommand()
	{
		testRunner = new TestRunnerController(TestRunnerController.RunMode.RUN_SERVER);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
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
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		return testRunner.run(brjs, args, this);
	}
}
