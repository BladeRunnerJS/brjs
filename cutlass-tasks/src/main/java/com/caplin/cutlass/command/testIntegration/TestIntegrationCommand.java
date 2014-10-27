package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;
import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;

import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.caplin.cutlass.conf.TestRunnerConfLocator;
import com.caplin.cutlass.testIntegration.WebDriverProvider;

public class TestIntegrationCommand extends AbstractPlugin implements LegacyCommandPlugin 
{
	private static final String URL_FLAG = "--url";
	private static final String NO_WORKBENCH_FLAG = "--no-workbench";
	private Logger logger;
	private BRJS brjs;
	
	public TestIntegrationCommand()
	{
		this.logger = ThreadSafeStaticBRJSAccessor.root.logger(this.getClass());
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
		this.brjs = brjs;
	}
	
	@Override
	public String getCommandName()
	{
		return "test-integration";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Runs all webdriver integration tests in the provided directory.";
	}	
	
	@Override 
	public String getCommandUsage()
	{
		return "<path-to-tests>";
	}

	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		validateArguments(args);
		MemoizedFile testRoot = getTestRoot(args);
		WebDriverProvider.setBaseUrl(getUrl(args));
				
		TestCompiler testCompiler = new TestCompiler();
		
		logger.println("Running integration tests in " + testRoot.getPath());
		logger.println("Running integration tests using root URL: " + WebDriverProvider.getBaseUrl(""));
		logger.println("");
		
		File classesRoot = null;
		try
		{
			classesRoot = testCompiler.getClassesRoot(testRoot);
		}
		catch (IOException ex)
		{
			throw new CommandOperationException("Error creating directory for compiled tests.", ex);
		}
		if (classesRoot.exists()) 
		{
			FileUtils.deleteQuietly(classesRoot);
		}
		
		List<File> testContainerDirs = new IntegrationTestFinder().findTestContainerDirs(brjs, testRoot, ignoreWorkbenches(args));
		if (testContainerDirs.size() < 1) 
		{
			throw new CommandOperationException("No tests found.");
		}
		logger.println("Found tests in " + testContainerDirs.size() + " location(s).");
		
		List<MemoizedFile> classDirs = testCompiler.compileTestDirs(brjs, testContainerDirs);
		
		List<Class<?>> testClasses = testCompiler.loadClasses(classDirs);
		
		Result testResult = null;
		try {
			File runnerConf = TestRunnerConfLocator.getTestRunnerConf();
			testResult = new IntegrationTestRunner().runTests(runnerConf, testClasses);
		}
		catch (Exception ex)
		{
			throw new CommandOperationException(ex);
		}
		
		printTestReport(testResult);
		if (testResult.getFailures().size() > 0)
		{
			throw new CommandOperationException("There were failing tests.");
		}
		return 0;
	}
	
	private void printTestReport(Result testResult) 
	{
		logger.println("");
		logger.println("== Test report ==");
		logger.println("Tests run: " + testResult.getRunCount());
		logger.println("Failed tests: " + testResult.getFailureCount());
		logger.println("Ignored tests: " + testResult.getIgnoreCount());
		logger.println("");
		if (testResult.getFailures().size() > 0)
		{
			logger.println("- Failures -");
			for (Failure fail : testResult.getFailures())
			{
				logger.println("");
				logger.println("--------------------------------");
				logger.println(fail.getDescription().toString());
				logger.println(fail.getException().toString());
				logger.println(fail.getTrace());
				logger.println("--------------------------------");
				logger.println("");
				logger.println("");
			}
		}
		System.out.flush();
	}
	
	private void validateArguments(String[] args) throws CommandOperationException
	{
		if (args.length < 1)
		{
			throw new CommandOperationException("Invalid arguments provided.");
		}
	}
	
	private MemoizedFile getTestRoot(String[] args) throws CommandOperationException
	{
		File testRoot = new File(args[0]);
		
		if(testRoot.exists() == false)
		{
			throw new CommandOperationException("Supplied test path does not exist.");
		}
		
		if(testRoot.isDirectory())
		{
			return brjs.getMemoizedFile(testRoot);
		}
		
		throw new CommandOperationException("Supplied test path does not exist.");
	}
	
	private String getUrl(String[] args) throws CommandOperationException
	{
		if (args.length >= 3 && args[1].equals(URL_FLAG))
		{
			try
			{
				return new URL(args[2]).toString();
			}
			catch (MalformedURLException e)
			{
				throw new CommandOperationException("'" + args[2] + "' is an invalid URL.");
			}
		}
		
		return "";
	}
	
	private boolean ignoreWorkbenches(String[] args)
	{
		return ( (args.length >= 2 && args[1].equals(NO_WORKBENCH_FLAG)) || (args.length >= 4 && args[3].equals(NO_WORKBENCH_FLAG)) );
	}
}