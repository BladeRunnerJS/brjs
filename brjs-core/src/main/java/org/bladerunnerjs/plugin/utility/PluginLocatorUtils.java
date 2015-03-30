package org.bladerunnerjs.plugin.utility;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.Plugin;


public class PluginLocatorUtils
{

	public class Messages {
		public static final String INIT_PLUGIN_ERROR_MSG = "error initializing the plugin %s, the error was: '%s'";
	}
	
	public static <P extends Plugin> List<P> filterAndOrderPlugins(BRJS brjs, List<P> plugins) {
		Collections.sort(plugins, new Comparator<Plugin>()
		{
			@Override
			public int compare(Plugin p1, Plugin p2)
			{
				int priorityComparation = Integer.compare(p1.priority(), p2.priority());
				if (priorityComparation == 0) {
					return p1.getPluginClass().getCanonicalName().compareTo( p2.getPluginClass().getCanonicalName() );
				}
				return priorityComparation;
			}
		});
		return plugins;
	}
	
	public static void setBRJSForPlugins(BRJS brjs, Plugin... plugins)
	{
		setBRJSForPlugins(brjs, Arrays.asList(plugins));
	}
	
	public static void setBRJSForPlugins(BRJS brjs, List<? extends Plugin> plugins)
	{
		for (Plugin p : plugins)
		{
			try 
			{
				p.setBRJS(brjs);
			} 
			catch (Throwable ex)
			{
				brjs.logger(PluginLocatorUtils.class).error(Messages.INIT_PLUGIN_ERROR_MSG, p.getClass().getCanonicalName(), ExceptionUtils.getStackTrace(ex));
			}
		}
	}
	
}
