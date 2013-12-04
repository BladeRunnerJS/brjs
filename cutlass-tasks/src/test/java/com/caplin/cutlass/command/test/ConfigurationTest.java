package com.caplin.cutlass.command.test;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.exception.test.NoBrowsersDefinedException;
import com.caplin.cutlass.CutlassConfig;
import org.junit.Before;
import org.junit.Test;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.conf.TestRunnerConfiguration;
import com.caplin.cutlass.testing.BRJSTestFactory;

public class ConfigurationTest {
	TestRunnerConfiguration config;
	
	private List<String> browserList(String browsers) {
		return Arrays.asList(browsers.split(", *"));
	}
	
	@Before
	public void beforeTest() throws Exception {
		// we're cheekily using another tests sdk structure so the test can work
		File sdkBaseDir = new File("src/test/resources/AnalyseApplicationCommandTest/structure-tests/" + CutlassConfig.SDK_DIR);
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(sdkBaseDir));
				
		config = TestRunnerConfiguration.getConfiguration(new File("src/test/resources/TestCommand/test-runner.conf"), browserList("browser1"));
		config.setOperatingSystem("OS1");
	}
	
	@Test
	public void configurationIsCorrectlyCreatedFromAFile()
	{
		assertEquals("1a", "browser1", config.getBrowserNames().get(0));
		assertEquals("1b", 1, config.getBrowserNames().size());
		assertEquals("1c", new File("src/test/resources/TestCommand").getAbsolutePath(), config.getRelativeDir().getAbsolutePath());
		
		assertEquals("2a", "jar-path/JsTestDriver.jar", config.getJsTestDriverJar());
		assertEquals("2b", 999, config.getPortNumber());
		assertEquals("2c", "browser2", config.getDefaultBrowser());
		
		assertEquals("3a", 2, config.getBrowserPaths().size());
		
		assertEquals("4a", "os1/path1/browser1.exe", config.getBrowserPaths().get("OS1").get("browser1"));
		assertEquals("4b", "os1/path2/browser2.exe", config.getBrowserPaths().get("OS1").get("browser2"));
		assertEquals("4c", 2, config.getBrowserPaths().get("OS1").size());
		
		assertEquals("5a", "os2/path1/browser1.exe", config.getBrowserPaths().get("OS2").get("browser1"));
		assertEquals("5b", "os2/path2/browser2.exe", config.getBrowserPaths().get("OS2").get("browser2"));
		assertEquals("5c", 2, config.getBrowserPaths().get("OS2").size());
	}
	
	@Test
	public void browsersListIsCorrect() throws NoBrowsersDefinedException, IOException
	{
		List<String> browsers = config.getBrowsers();
		
		assertEquals("1a", "os1/path1/browser1.exe", browsers.get(0));
		assertEquals("1b", 1, browsers.size());
	}
	
	@Test
	public void notSpecifyingAnyBrowsersShouldCauseTheDefaultToBeUsed() throws NoBrowsersDefinedException, IOException
	{
		config.setBrowserNames(new ArrayList<String>());
		List<String> browsers = config.getBrowsers();
		
		assertEquals("1a", "os1/path2/browser2.exe", browsers.get(0));
		assertEquals("1b", 1, browsers.size());
	}
	
	@Test
	public void specifyingANonExistentBrowserShouldCauseTheDefaultToBeUsed() throws NoBrowsersDefinedException, IOException
	{
		config.setBrowserNames(Arrays.asList("non-existent-browser"));
		List<String> browsers = config.getBrowsers();
		
		assertEquals("1a", "os1/path2/browser2.exe", browsers.get(0));
		assertEquals("1b", 1, browsers.size());
	}
	
	@Test
	public void specifyingALLShouldCauseAllBrowsersToBeReturned() throws NoBrowsersDefinedException, IOException
	{
		config.setBrowserNames(Arrays.asList("ALL"));
		List<String> browsers = config.getBrowsers();
		
		assertEquals("1a", "os1/path1/browser1.exe", browsers.get(1));
		assertEquals("1b", "os1/path2/browser2.exe", browsers.get(0));
		assertEquals("1c", 2, browsers.size());
	}
	
	@Test
	public void theDefaultBrowserMustBeAvailableOnAllPlatforms()
	{
		// TODO...
	}
	
	@Test
	public void ifARequestedBrowserIsntAvailableOnTheCurrentPlatformItIsSkipped()
	{
		// TODO...
	}
	
	@Test
	public void ifARequestedBrowserIsntAvailableOnAnyPlatformAnExceptionIsThrown()
	{
		// TODO...
	}
}
