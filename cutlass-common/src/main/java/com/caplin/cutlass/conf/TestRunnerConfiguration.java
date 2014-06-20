package com.caplin.cutlass.conf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.BRJSModelAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.exception.test.NoBrowsersDefinedException;

import com.caplin.cutlass.CutlassConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class TestRunnerConfiguration {
	private Logger logger = BRJSModelAccessor.root.logger(TestRunnerConfiguration.class);
	private File relativeDir;
	private String operatingSystem;
	private String defaultBrowser;
	private List<String> browserNames;
	private Map<String, Map<String, String>> browserPaths;
	private int portNumber;
	private String jsTestDriverJar;
	
	public static TestRunnerConfiguration getConfiguration(File configFile, List<String> browserNames) throws FileNotFoundException, YamlException, IOException {
		YamlReader reader = null;
		TestRunnerConfiguration configuration = null;
		try {
			reader = new YamlReader(new FileReader(configFile));
			configuration = reader.read(TestRunnerConfiguration.class);
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
		
		configuration.setRelativeDir(configFile.getParentFile());
		configuration.setOperatingSystem(CutlassConfig.OS);
		configuration.setBrowserNames(browserNames);
		
		return configuration;
	}
	
	public List<String> getBrowsers() throws NoBrowsersDefinedException, IOException 
	{
		List<String> browsers = new ArrayList<String>();
		Map<String, String> osBrowserPaths = getBrowserPathsForOS();
		
		if((browserNames.size() == 1) && (browserNames.get(0).equals("ALL"))) {
			addAllBrowsers(browsers, osBrowserPaths);
		}
		else {
			addBrowsersFromArguments(browsers, osBrowserPaths);
			addDefaultBrowserIfNoBrowserDefinedInArgumentsAndArgumentDoesntAskForNone(browsers, osBrowserPaths);
		}
		
		return browsers;
	}
	
	public Map<String, String> getBrowserPathsForOS() throws NoBrowsersDefinedException, IOException
	{
		Map<String, String> paths = null;
		try {
			paths = browserPaths.get(getOperatingSystem());
		}
		catch (ClassCastException e) 
		{
			throw new NoBrowsersDefinedException(getRelativeDir().getCanonicalPath());
		}
		return paths;
	}
	
	public File getJsTestDriverJarFile() {
		try {
			return new File(relativeDir, jsTestDriverJar).getCanonicalFile();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int getPortNumber() {
		return portNumber;
	}
	
	public void setPortNumber(int portNumber) {
		this.portNumber = portNumber;
	}
	
	public String getJsTestDriverJar() {
		return jsTestDriverJar;
	}
	
	public void setJsTestDriverJar(String jsTestDriverJar) {
		this.jsTestDriverJar = jsTestDriverJar;
	}
	
	public String getDefaultBrowser() {
		return defaultBrowser;
	}

	public void setDefaultBrowser(String defaultBrowser) {
		this.defaultBrowser = defaultBrowser;
	}
	
	public List<String> getBrowserNames() {
		return browserNames;
	}
	
	public void setBrowserNames(List<String> browserNames) {
		this.browserNames = browserNames;
	}
	
	public Map<String, Map<String, String>> getBrowserPaths() {
		return browserPaths;
	}
	
	public void setBrowserPaths(Map<String, Map<String, String>> browserPaths) {
		this.browserPaths = browserPaths;
	}
	
	public File getRelativeDir() {
		return relativeDir;
	}
	
	public void setRelativeDir(File relativeDir) {
		this.relativeDir = relativeDir;
	}
	
	public String getOperatingSystem() {
		return operatingSystem;
	}
	
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	
	private void addAllBrowsers(List<String> browsers, Map<String, String> osBrowserPaths) {
		for(String browserName : osBrowserPaths.keySet()) {
			browsers.add(osBrowserPaths.get(browserName));
		}
	}
	
	private void addBrowsersFromArguments(List<String> browsers, Map<String, String> osBrowserPaths) {
		for(String browserName : browserNames) {
			browserName = browserName.trim();
			
			if(!osBrowserPaths.containsKey(browserName) && !browserName.equals("none")) {
				logger.warn("no browser '" + browserName + "' specified within the configuration file, skipping...");
			}
			else if(!browserName.equals("none")) {
				browsers.add(osBrowserPaths.get(browserName));
			}
		}
	}
	
	private void addDefaultBrowserIfNoBrowserDefinedInArgumentsAndArgumentDoesntAskForNone(List<String> browsers, Map<String, String> osBrowserPaths) {
		if(browsers.size() == 0 && (browserNames.size() == 0 || !browserNames.get(0).equals("none"))) {
			browsers.add(osBrowserPaths.get(defaultBrowser));
		}
	}
}
