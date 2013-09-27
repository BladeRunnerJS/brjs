package com.caplin.cutlass.testIntegration;

import java.io.File;
import java.util.Map;

import org.openqa.selenium.WebDriver;

public interface DriverFactory
{
	boolean canHandleBrowser(String browserName);
	
	WebDriver createDriver(File browserPath, Map<String, String> driverOptions);
}
