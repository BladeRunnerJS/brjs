package com.caplin.cutlass.testIntegration;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.bladerunnerjs.logging.Logger;

import org.bladerunnerjs.model.BRJSModelAccessor;
import com.caplin.cutlass.conf.TestRunnerConfLocator;
import com.caplin.cutlass.conf.TestRunnerConfiguration;

public class WebDriverProvider
{
	private static String DEFAULT_BASE_URL = "http://localhost:7070/";
	
	private static String commandLineSpecifiedBrowser = null;
	private static List<DriverFactory> driverFactories = Arrays.asList(new FirefoxDriverFactory(), new ChromeDriverFactory());
	
	private static String baseUrl = null;
	
	
	// methods used by junit tests
	
	// TODO: The long term goal is that tests will normally use this constructor to elect the default browser,
	// and only explicitly specify a browser if the test only works with a specific browser or if the developer
	// is temporarily testing with another browser while running from Eclipse. In this way, a --b switch can
	// eventually be added to the test-runner to allow the browser to be specified at the command line.
	public static WebDriver getDriver() throws Exception
	{
		File runnerConfPath = getTestRunnerConfig();
		TestRunnerConfiguration runnerConf = TestRunnerConfiguration.getConfiguration(runnerConfPath, null);
		
		return getDriver(runnerConf.getDefaultBrowser());
	}
	
	@Deprecated
	public static WebDriver getDriver(Class<? extends WebDriver> testSpecifiedDriverClass) throws Exception
	{
		return getDriver("firefox-webdriver");
	}
	
	public static WebDriver getDriver(String testSpecifiedBrowser) throws Exception
	{
		return getDriver(testSpecifiedBrowser, new HashMap<String, String>());
	}
	
	public static WebDriver getDriver(String testSpecifiedBrowser, Map<String, String> driverOptions) throws Exception
	{
		Logger logger = BRJSModelAccessor.root.logger(WebDriverProvider.class);
		
		String browserName = (commandLineSpecifiedBrowser != null) ? commandLineSpecifiedBrowser : testSpecifiedBrowser;
		WebDriver driver = null;
		
		for(DriverFactory driverFactory : driverFactories)
		{
			if(driverFactory.canHandleBrowser(browserName))
			{
				File browserPath = getBrowserPathFromConf(browserName);
				logger.debug("using '" + browserName + "' browser ('" + browserPath + "')");
				
				driver = driverFactory.createDriver(browserPath, driverOptions);
			}
		}
		
		return driver;
	}
	
	public static void closeDriver(WebDriver driver)
	{
		driver.close();
		try
		{
			driver.quit();
		}
		catch (Exception ex)
		{
		}
	}
	
	
	// methods used by the test runner
	
	public static void setBaseUrl(String url)
	{
		baseUrl = url;
	}
	
	public static String getBaseUrl(String appName)
	{
		String url = ((baseUrl != null) && (!baseUrl.equals(""))) ? baseUrl : DEFAULT_BASE_URL;
		
		if (url.endsWith("/"))
		{
			url = StringUtils.substringBeforeLast(url, "/");
		}
		url += appName;
		
		return url;
	}
	
	
	// private methods
	
	private static File getBrowserPathFromConf(String browser) throws Exception
	{
		String browserPath = null;
		File runnerConfPath = getTestRunnerConfig();
		TestRunnerConfiguration runnerConf = TestRunnerConfiguration.getConfiguration(runnerConfPath, null);
		
		Map<String, String> browserPaths = runnerConf.getBrowserPathsForOS();
		
		if (!browserPaths.containsKey(browser))
		{
			throw new Exception("No config specified for browser " + browser + " in configuration file '" + runnerConfPath.getPath() + "'");
		}
		browserPath = sanitizeBrowserPathForWebdriver(browserPaths.get(browser));
		
		return new File(runnerConfPath.getParentFile(), browserPath);

	}
	
	private static String sanitizeBrowserPathForWebdriver(String path)
	{
		path = StringUtils.substringBefore(path, "$");
		path = StringUtils.substringBeforeLast(path, ".app/");
		return path;
	}
	
	private static File getTestRunnerConfig() throws IOException
	{
		return TestRunnerConfLocator.getTestRunnerConf();
	}
}
