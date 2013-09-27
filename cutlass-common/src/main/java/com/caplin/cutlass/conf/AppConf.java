package com.caplin.cutlass.conf;

import org.bladerunnerjs.model.conf.YamlAppConf;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.App;
import com.caplin.cutlass.BRJSAccessor;
import com.esotericsoftware.yamlbeans.YamlException;

import java.io.*;

public class AppConf
{	
	public static final AppConf exampleConf = new AppConf("<namespace>", "<locale1>,<locale2>");
	public static final String LOCALE_SEPERATOR = ",";
	public String appNamespace = "";
	public String locales = "";
	
	public AppConf()
	{
		
	}
	
	public AppConf(String appNamespace, String locales)
	{
		this.appNamespace = appNamespace;
		this.locales = locales;
	}
	
	public static AppConf getConf(File applicationDirectory) throws FileNotFoundException, YamlException, IOException, ConfigException
	{
		App app = BRJSAccessor.root.locateAncestorNodeOfClass(applicationDirectory, App.class);
		File appConfFile = app.file("app.conf");
		
		if(!appConfFile.isFile()) {
			YamlAppConf exampleConf = new YamlAppConf();
			exampleConf.setConfFile(appConfFile);
			exampleConf.appNamespace = "<namespace>";
			exampleConf.locales = "<locale1>,<locale2>";
			
			throw new FileNotFoundException(
				"No app config file found at " + appConfFile.getAbsolutePath() + ".\n" +
				"Example content for 'app.conf':\n\n" +
				exampleConf.getRenderedConfig());
		}
		
		org.bladerunnerjs.model.AppConf newAppConf = app.appConf();
		
		return new AppConf(newAppConf.getAppNamespace(), newAppConf.getLocales());
	}
	
	public static void writeConf(File applicationDirectory, AppConf appConf) throws IOException, ConfigException
	{
		YamlAppConf newAppConf = new YamlAppConf();
		newAppConf.setConfFile(new File(applicationDirectory, "app.conf"));
		newAppConf.appNamespace = appConf.appNamespace;
		newAppConf.locales = appConf.locales;
		newAppConf.write();
	}
}
