package com.caplin.cutlass.structure;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.conf.AppConf;
import com.caplin.cutlass.exception.NamespaceException;


public class NamespaceCalculator
{

	static HashMap<String, String> applicationToNamespace = new HashMap<String, String>();

	public static void purgeCachedApplicationNamespaces()
	{
		applicationToNamespace = new HashMap<String, String>();
	}
	
	public static String getPackageNamespaceForBladeLevelResources(File location) throws NamespaceException
	{
		ScopeLevel scopeLevel = CutlassDirectoryLocator.getScope(location);
		
		if(scopeLevel == ScopeLevel.BLADE_SCOPE)
		{
			return NamespaceCalculator.getAppNamespace(location) + "." + NamespaceCalculator.getBladesetNamespace(location) + "." + NamespaceCalculator.getBladeNamespace(location) + ".";
		}
		else if(scopeLevel == ScopeLevel.BLADESET_SCOPE)
		{
			return NamespaceCalculator.getAppNamespace(location) + "." + NamespaceCalculator.getBladesetNamespace(location) + ".";
		}
		
		return "";
	}

	public static String getBladesetNamespace(File location)
	{
		File parentBladeset = CutlassDirectoryLocator.getParentBladeset(location);
		
		if (parentBladeset == null)
		{
			return "";
		}
		return StringUtils.substringBeforeLast(parentBladeset.getName(), CutlassConfig.BLADESET_SUFFIX);
	}

	public static String getBladeNamespace(File location)
	{
		File parentBlade = CutlassDirectoryLocator.getParentBlade(location);
		if (parentBlade == null)
		{
			return "";
		}
		return parentBlade.getName();
	}
	
	public static String getAppNamespace(File applicationDirectory) throws NamespaceException
	{
		if (applicationDirectory == null)
		{
			return "";
		}
		
		String applicationPath = applicationDirectory.getAbsolutePath();
		
		if (!applicationToNamespace.containsKey(applicationPath))
		{
			AppConf appConf;
			
			try
			{
				appConf = AppConf.getConf(applicationDirectory);
			}
			catch(Exception e)
			{
				throw new NamespaceException("Unable to parse the app configuration file", e);
			}
			
			String namespace = appConf.appNamespace;
			applicationToNamespace.put(applicationPath, namespace);
		}
		
		String appNamespace = applicationToNamespace.get(applicationPath);
		
		return appNamespace;
	}

}
