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
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;

public class TestCommand extends JSAPArgsParsingCommandPlugin implements LegacyCommandPlugin
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
	protected void configureArgsParser(JSAP argsParser) throws JSAPException
	{
		argsParser.registerParameter(new UnflaggedOption("dir").setRequired(true).setHelp("the directory from which to start looking for tests"));
		argsParser.registerParameter(new UnflaggedOption("testType").setDefault("UTsAndATs").setStringParser(EnumeratedStringParser.getParser("UTs;ATs;ITs;UTsAndATs;ALL;", true)).setHelp("(UTs|ATs|ITs|ALL)"));
		argsParser.registerParameter(new FlaggedOption("browsers").setShortFlag('b').setList(true).setListSeparator(',').setHelp("you can use ALL to specify that the tests should be run on all browsers"));
		argsParser.registerParameter(new Switch("report").setLongFlag("report").setDefault("false").setHelp("if supplied, generate the HTML reports after running tests"));
		argsParser.registerParameter(new FlaggedOption("js-minifier").setLongFlag("js-minifier").setDefault("combined").setHelp("set the minifier used for JS bundles"));
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException
	{
		return testRunner.run(brjs, parsedArgs, this);
	}
	
}
