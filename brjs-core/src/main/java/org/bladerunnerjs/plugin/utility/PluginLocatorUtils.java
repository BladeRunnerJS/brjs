package org.bladerunnerjs.plugin.utility;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.Plugin;


public class PluginLocatorUtils
{

	public class Messages {
		public static final String INIT_PLUGIN_ERROR_MSG = "error initializing the plugin %s, the error was: '%s'";
	}
	
	public static List<? extends Plugin> setBRJSForPlugins(BRJS brjs, Plugin... plugins)
	{
		return setBRJSForPlugins(brjs, Arrays.asList(plugins));
	}
	
	public static List<? extends Plugin> setBRJSForPlugins(BRJS brjs, List<? extends Plugin> plugins)
	{
		for (Plugin p : plugins)
		{
			try 
			{
				p.setBRJS(brjs);
			} 
			catch (Throwable ex)
			{
				brjs.logger(LoggerType.UTIL, PluginLocatorUtils.class).error(Messages.INIT_PLUGIN_ERROR_MSG, p.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(ex));
			}
		}
		return plugins;
	}
	
}
