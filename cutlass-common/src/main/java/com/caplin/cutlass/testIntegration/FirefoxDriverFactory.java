package com.caplin.cutlass.testIntegration;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;

public class FirefoxDriverFactory implements DriverFactory
{
	@Override
	public boolean canHandleBrowser(String browserName)
	{
		return browserName.startsWith("firefox");
	}
	
	@Override
	public WebDriver createDriver(File browserPath, Map<String, String> driverOptions)
	{
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("capability.policy.default.Window.QueryInterface", "allAccess");
		profile.setPreference("capability.policy.default.Window.frameElement.get","allAccess");
		profile.setPreference("capability.policy.default.HTMLDocument.compatMode.get", "allAccess");
        profile.setPreference("capability.policy.default.Location.href","allAccess");
        profile.setPreference("capability.policy.default.Window.pageXOffset","allAccess");
        profile.setPreference("capability.policy.default.Window.pageYOffset","allAccess");
        profile.setPreference("capability.policy.default.Window.frameElement","allAccess");
        profile.setPreference("capability.policy.default.Window.frameElement.get","allAccess");
        profile.setPreference("capability.policy.default.Window.QueryInterface","allAccess");
        profile.setPreference("capability.policy.default.Window.mozInnerScreenY","allAccess");
        profile.setPreference("capability.policy.default.Window.mozInnerScreenX","allAccess");
		
		System.setProperty("webdriver.firefox.bin", browserPath.getPath());
		
		for(String optionKey : driverOptions.keySet())
		{
			String optionValue = driverOptions.get(optionKey);
			profile.setPreference(optionKey, optionValue);
		}
		
		return new FirefoxDriver(profile);
	}
}
