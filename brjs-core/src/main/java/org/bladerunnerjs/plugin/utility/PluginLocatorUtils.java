package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.plugin.Plugin;


public class PluginLocatorUtils
{

	public class Messages {
		public static final String INIT_PLUGIN_ERROR_MSG = "error initializing the plugin %s, the error was: '%s'";
		public static final String NO_MATCHING_PLUGIN = "No plugin found which matches '%s'.";
	}
	
	public static <P extends Plugin> List<P> filterAndOrderPlugins(BRJS brjs, Class<? extends Plugin> pluginInterface, List<P> plugins) throws ConfigException {
		Logger logger = brjs.logger(PluginLocatorUtils.class);
		String pluginInterfaceName = pluginInterface.getSimpleName();
		List<String> activePluginsConf = brjs.bladerunnerConf().getActivePlugins().get(pluginInterfaceName);
		if (activePluginsConf == null || activePluginsConf.isEmpty()) {
			return plugins;
		}
		List<P> activePlugins = new ArrayList<>();
		for(String nextPluginName : activePluginsConf) {
			addMatchingActivePlugin(logger, plugins, activePlugins, nextPluginName);
		}
		return activePlugins;
	}

	private static <P extends Plugin> void addMatchingActivePlugin(Logger logger, List<P> plugins, List<P> activePlugins, String nextPluginName)
	{
		if (plugins.isEmpty()) {
			return;
		}
		boolean matchedPlugin = false;
		
		for (P plugin : plugins) {
			if (activePlugins.contains(plugin)) {
				continue;
			} else if (nextPluginName.equals("*")) {
				activePlugins.add(plugin);
				matchedPlugin = true;
			} else if ( (plugin.getPluginClass().getSimpleName().equals(nextPluginName) || plugin.getPluginClass().getName().equals(nextPluginName)) ){
				activePlugins.add(plugin);
				return;
			}
		}
		
		if (!matchedPlugin) {
			logger.warn(Messages.NO_MATCHING_PLUGIN, nextPluginName);
		}
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
