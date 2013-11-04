package com.caplin.cutlass.command.test.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import javax.naming.InvalidNameException;

import org.apache.commons.io.IOUtils;

import com.caplin.cutlass.command.test.testrunner.TestRunner.TestType;
import com.caplin.cutlass.conf.TestRunnerConfLocator;
import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.core.plugin.command.CommandPlugin;
import org.bladerunnerjs.model.DirNode;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;

public class TestRunnerController
{
	public static final String REPORT_SWITCH = "report";

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
	
	public void run(String[] args, CommandPlugin testCommand) throws CommandArgumentsException, CommandOperationException
	{
		File configFile = null;
		try {
			configFile = TestRunnerConfLocator.getTestRunnerConf();
		} catch (FileNotFoundException ex)
		{
			throw new CommandOperationException(ex);
		}
		
		File resultDir = getResultsDir();
		JSAP argsParser = createArgsParser(mode);

		JSAPResult config = argsParser.parse(args);

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
				File templateConfigFile = BRJSAccessor.root.template("brjs").file("conf/test-runner.conf");
				
				try(FileReader configFileReader = new FileReader(configFile);
					FileReader templateConfigFileReader = new FileReader(templateConfigFile))
				{
					if( IOUtils.contentEquals(configFileReader, templateConfigFileReader) )
					{
						throw new CommandOperationException("Browsers must first be defined within the test runner configuration file ('" +
							configFile.getAbsolutePath() + "') before any tests can be run.");
					}
					
					boolean generateReports = (mode == RunMode.RUN_TESTS) && config.getBoolean(REPORT_SWITCH);
					testRunner = new TestRunner(configFile, resultDir, Arrays.asList(config.getStringArray("browsers")), generateReports);
				}
			}
			catch (CommandOperationException ex)
			{
				throw ex;
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
					success = testRunner.runTests(new File(config.getString("dir")), getTestTypeEnum(config.getString("testType")));
				}
				catch (Exception ex)
				{
					testRunner.showExceptionInConsole(ex);
					throw new CommandOperationException("Error running tests.", ex);
				}
			}
		}
		if (!success)
		{
			throw new CommandOperationException("Test failure or error while running tests.");
		}
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
		}
		catch (Exception ex)
		{
			throw new CommandOperationException("Error initialising configuration.", ex);
		}
		return argsParser;
	}

	private File getResultsDir() throws CommandOperationException
	{
		DirNode testResults = BRJSAccessor.root.testResults();
		
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
