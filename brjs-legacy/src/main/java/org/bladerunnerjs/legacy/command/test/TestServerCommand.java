package org.bladerunnerjs.legacy.command.test;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.legacy.command.LegacyCommandPlugin;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunnerController;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;

public class TestServerCommand extends JSAPArgsParsingCommandPlugin implements LegacyCommandPlugin
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
	protected void configureArgsParser(JSAP argsParser) throws JSAPException
	{
		argsParser.registerParameter(new FlaggedOption("browsers").setShortFlag('b').setList(true).setListSeparator(',').setHelp("you can use ALL to specify that the tests should be run on all browsers"));
		argsParser.registerParameter(new Switch("no-browser").setLongFlag("no-browser").setDefault("false").setHelp("you can start the test-server on it's own without a browser"));
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException
	{
		return testRunner.run(brjs, parsedArgs, this);
	}
}
