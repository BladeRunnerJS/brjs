package com.caplin.cutlass.command.testIntegration;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.command.LegacyCommandPlugin;
import com.caplin.cutlass.conf.TestRunnerConfLocator;
import com.caplin.cutlass.testIntegration.WebDriverProvider;

public class TestIntegrationCommand extends AbstractPlugin implements LegacyCommandPlugin 
{
	// TODO: get rid of the idea of alpha commands now that commands are plugins, and so can easily be kept separate from the product
	private static final String ALPHA_PREFIX = "alpha-";
	
	private static final String URL_FLAG = "--url";
	private static final String NO_WORKBENCH_FLAG = "--no-workbench";
	private ConsoleWriter out;
	
	public TestIntegrationCommand(File sdkBaseDir)
	{
		out = BRJSAccessor.root.getConsoleWriter();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getCommandName()
	{
		return ALPHA_PREFIX + "test-integration";
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
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException
	{
		validateArguments(args);
		File testRoot = getTestRoot(args);
		WebDriverProvider.setBaseUrl(getUrl(args));
				
		TestCompiler testCompiler = new TestCompiler();
		
		out.println("Running integration tests in " + testRoot.getPath());
		out.println("Running integration tests using root URL: " + WebDriverProvider.getBaseUrl(""));
		out.println("");
		
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
		
		List<File> testContainerDirs = new IntegrationTestFinder().findTestContainerDirs(testRoot, ignoreWorkbenches(args));
		if (testContainerDirs.size() < 1) 
		{
			throw new CommandOperationException("No tests found.");
		}
		out.println("Found tests in " + testContainerDirs.size() + " location(s).");
		
		List<File> classDirs = testCompiler.compileTestDirs(testContainerDirs);
		
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
	}
	
	private void printTestReport(Result testResult) 
	{
		out.println("");
		out.println("== Test report ==");
		out.println("Tests run: " + testResult.getRunCount());
		out.println("Failed tests: " + testResult.getFailureCount());
		out.println("Ignored tests: " + testResult.getIgnoreCount());
		out.println("");
		BRJSAccessor.root.getConsoleWriter().flush();
		if (testResult.getFailures().size() > 0)
		{
			out.println("- Failures -");
			for (Failure fail : testResult.getFailures())
			{
				out.println("");
				out.println("--------------------------------");
				out.println(fail.getDescription().toString());
				out.println(fail.getException().toString());
				out.println(fail.getTrace());
				out.println("--------------------------------");
				out.println("");
				out.println("");
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
	
	private File getTestRoot(String[] args) throws CommandOperationException
	{
		File testRoot = new File(args[0]);
		
		if(testRoot.exists() == false)
		{
			throw new CommandOperationException("Supplied test path does not exist.");
		}
		
		if(testRoot.isDirectory())
		{
			return testRoot;
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