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
		
		if (app == null)
		{
			YamlAppConf appConf = new YamlAppConf();
			appConf.setConfFile( new File(applicationDirectory, "app.conf") );
			appConf.verify();
			
			return new AppConf(appConf.appNamespace, appConf.locales);
		}
		
		org.bladerunnerjs.model.AppConf appConf = app.appConf();
		return new AppConf(appConf.getAppNamespace(), appConf.getLocales());
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
