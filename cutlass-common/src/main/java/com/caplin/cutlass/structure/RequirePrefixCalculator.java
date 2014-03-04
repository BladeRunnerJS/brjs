package com.caplin.cutlass.structure;

import java.io.File;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.conf.AppConf;
import com.caplin.cutlass.exception.NamespaceException;


public class RequirePrefixCalculator
{

	static HashMap<String, String> applicationToRequirePrefix = new HashMap<String, String>();

	public static void purgeCachedApplicationNamespaces()
	{
		applicationToRequirePrefix = new HashMap<String, String>();
	}
	
	public static String getPackageRequirePrefixForBladeLevelResources(File location) throws NamespaceException
	{
		ScopeLevel scopeLevel = CutlassDirectoryLocator.getScope(location);
		
		if(scopeLevel == ScopeLevel.BLADE_SCOPE)
		{
			return RequirePrefixCalculator.getAppRequirePrefix(location) + "." + RequirePrefixCalculator.getBladesetRequirePrefix(location) + "." + RequirePrefixCalculator.getBladeRequirePrefix(location) + ".";
		}
		else if(scopeLevel == ScopeLevel.BLADESET_SCOPE)
		{
			return RequirePrefixCalculator.getAppRequirePrefix(location) + "." + RequirePrefixCalculator.getBladesetRequirePrefix(location) + ".";
		}
		
		return "";
	}

	public static String getBladesetRequirePrefix(File location)
	{
		File parentBladeset = CutlassDirectoryLocator.getParentBladeset(location);
		
		if (parentBladeset == null)
		{
			return "";
		}
		return StringUtils.substringBeforeLast(parentBladeset.getName(), CutlassConfig.BLADESET_SUFFIX);
	}

	public static String getBladeRequirePrefix(File location)
	{
		File parentBlade = CutlassDirectoryLocator.getParentBlade(location);
		if (parentBlade == null)
		{
			return "";
		}
		return parentBlade.getName();
	}
	
	public static String getAppRequirePrefix(File applicationDirectory) throws NamespaceException
	{
		if (applicationDirectory == null)
		{
			return "";
		}
		
		String applicationPath = applicationDirectory.getAbsolutePath();
		
		if (!applicationToRequirePrefix.containsKey(applicationPath))
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
			
			String namespace = appConf.requirePrefix;
			applicationToRequirePrefix.put(applicationPath, namespace);
		}
		
		String requirePrefix = applicationToRequirePrefix.get(applicationPath);
		
		return requirePrefix;
	}

}
