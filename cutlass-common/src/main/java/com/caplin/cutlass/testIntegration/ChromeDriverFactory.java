package com.caplin.cutlass.testIntegration;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

public class ChromeDriverFactory implements DriverFactory
{
	@Override
	public boolean canHandleBrowser(String browserName)
	{
		return browserName.startsWith("chrome");
	}
	
	@SuppressWarnings("deprecation")
	@Override
	public WebDriver createDriver(File browserPath, Map<String, String> driverOptions)
	{
		DesiredCapabilities capabilities = DesiredCapabilities.chrome();
		capabilities.setCapability("chrome.binary", browserPath.getPath());
		
		for(String optionKey : driverOptions.keySet())
		{
			String optionValue = driverOptions.get(optionKey);
			capabilities.setCapability(optionKey, optionValue);
		}
		
		// TODO: we should switch over to using chrome options instead
		return new ChromeDriver(capabilities);
	}
}
