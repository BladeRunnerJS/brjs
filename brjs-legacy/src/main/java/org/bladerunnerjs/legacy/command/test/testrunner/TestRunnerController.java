package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunner.TestType;
import org.bladerunnerjs.legacy.conf.TestRunnerConfLocator;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;
import org.bladerunnerjs.api.DirNode;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;

public class TestRunnerController
{
	public static final String REPORT_SWITCH = "report";
	public static final String NO_BROWSER_SWITCH = "no-browser";

	public enum RunMode {
		RUN_TESTS, RUN_SERVER
	}
	
	private RunMode mode;

	public TestRunnerController(RunMode mode)
	{
		this.mode = mode;
	}

	public String getUsage() {
		try
		{
			return createArgsParser(mode).getUsage();
		}
		catch (CommandOperationException e)
		{
			return null;
		}
	}
	
	public String getHelp() {
		try
		{
			return createArgsParser(mode).getHelp();
		}
		catch (CommandOperationException e)
		{
			return null;
		}
	}
	
	public int run(BRJS brjs, String[] args, CommandPlugin testCommand) throws CommandArgumentsException, CommandOperationException
	{
		JSAP argsParser = createArgsParser(mode);
		JSAPResult config = argsParser.parse(args);
		
		assertValidTestDirectory(brjs, testCommand, config);
		
		MemoizedFile configFile = null;
		try {
			configFile = TestRunnerConfLocator.getTestRunnerConf();
		} catch (FileNotFoundException ex)
		{
			throw new CommandOperationException(ex);
		}
		
		MemoizedFile resultDir = getResultsDir();

		boolean success = true;
		if (!config.success())
		{
			throw new CommandArgumentsException("Invalid arguments provided.", testCommand);
		}
		else
		{
			TestRunner testRunner;
			
			try
			{				
				boolean testServerOnly = mode == RunMode.RUN_SERVER;
				boolean generateReports = (mode == RunMode.RUN_TESTS) && config.getBoolean(REPORT_SWITCH);
				boolean noBrowser = (mode == RunMode.RUN_SERVER) && config.getBoolean(NO_BROWSER_SWITCH);
				
				testRunner = new TestRunner(configFile, resultDir, Arrays.asList(config.getStringArray("browsers")), testServerOnly, noBrowser, generateReports);
			}
			catch (Exception ex)
			{
				throw new CommandOperationException("Error parsing test runner configuration file '" + configFile.getAbsolutePath() + "'.", ex);
			}

			if (mode == RunMode.RUN_SERVER)
			{
				try
				{
					testRunner.runServer();
				}
				catch (Exception ex)
				{
					throw new CommandOperationException("Error running server.", ex);
				}
			}
			else
			{
				try
				{
					success = testRunner.runTests( brjs.getMemoizedFile(config.getString("dir")), getTestTypeEnum(config.getString("testType")));
				}
				catch (Exception ex)
				{
					testRunner.showExceptionInConsole(ex);
					throw new CommandOperationException("Error running tests.", ex);
				}
			}
		}
		if (!success) {  return 1;  }
		return 0;
	}

	private void assertValidTestDirectory(BRJS brjs, CommandPlugin testCommand,
			JSAPResult config) throws CommandArgumentsException {
		String dirArg = config.getString("dir");
		if (dirArg == null) {
			return;
		}
		File testDir = new File(dirArg);
		List<MemoizedFile> validTestDirs = Arrays.asList(brjs.appsFolder(), brjs.sdkFolder().file("libs"), 
				brjs.sdkFolder().file("system-applications"));
		for (MemoizedFile validTestDir : validTestDirs) {
			try {
				if (testDir.getCanonicalPath().toLowerCase().contains(validTestDir.getCanonicalPath().toLowerCase())) {
					return;
				}
			} catch (IOException e) {
				throw new CommandArgumentsException("The test location could not be successfully established for the entity "
						+ "you are attempting to test.", testCommand);
			}
		}
		throw new CommandArgumentsException("The entity you are attempting to test does not exist inside a recognized app. "
					+ "The current apps directory is '" + brjs.appsFolder().getAbsolutePath() + "'.", testCommand);
	}

	private TestType getTestTypeEnum(String testType) 
	{
		return TestRunner.TestType.valueOf(testType.replaceAll("UTs and ATs", "UTsAndATs"));
	}

	private JSAP createArgsParser(RunMode mode) throws CommandOperationException
	{
		JSAP argsParser = new JSAP();
		try
		{
			if (mode == RunMode.RUN_TESTS)
			{
				argsParser.registerParameter(new UnflaggedOption("dir").setRequired(true).setHelp("the directory from which to start looking for tests"));
				argsParser.registerParameter(new UnflaggedOption("testType").setDefault("UTsAndATs").setStringParser(EnumeratedStringParser.getParser("UTs;ATs;ITs;UTsAndATs;ALL;", true)).setHelp("(UTs|ATs|ITs|ALL)"));
			}
			argsParser.registerParameter(new FlaggedOption("browsers").setShortFlag('b').setList(true).setListSeparator(',').setHelp("you can use ALL to specify that the tests should be run on all browsers"));
			// this isnt in the if block above so it appears as the last option in the help menu
			if (mode == RunMode.RUN_TESTS)
			{
				argsParser.registerParameter(new Switch(REPORT_SWITCH).setLongFlag(REPORT_SWITCH).setDefault("false").setHelp("if supplied, generate the HTML reports after running tests"));
			}
			if (mode == RunMode.RUN_SERVER)
			{
				argsParser.registerParameter(new Switch(NO_BROWSER_SWITCH).setLongFlag(NO_BROWSER_SWITCH).setDefault("false").setHelp("you can start the test-server on it's own without a browser"));
			}
		}
		catch (Exception ex)
		{
			throw new CommandOperationException("Error initialising configuration.", ex);
		}
		return argsParser;
	}

	private MemoizedFile getResultsDir() throws CommandOperationException
	{
		DirNode testResults = ThreadSafeStaticBRJSAccessor.root.testResults();
		
		if(!testResults.dirExists())
		{
			try
			{
				testResults.create();
			}
			catch(InvalidNameException | ModelUpdateException e)
			{
				throw new CommandOperationException("Cannot create test results dir at '" + testResults.dir().getPath() + "'");
			}
		}
		
		return testResults.dir();
	}
}
