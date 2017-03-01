package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.plugin.Plugin;


public class PluginLocatorUtils
{

	public class Messages {
		public static final String INIT_PLUGIN_ERROR_MSG = "error initializing the plugin %s, the error was: '%s'";
		public static final String NO_MATCHING_PLUGIN = "No plugin found which matches the ordered %s plugin configuration '%s'.";
	}
	
	public static <P extends Plugin> List<P> orderPlugins(BRJS brjs, Class<? extends Plugin> pluginInterface, List<P> plugins) throws ConfigException {
		Logger logger = brjs.logger(PluginLocatorUtils.class);
		String pluginInterfaceName = pluginInterface.getSimpleName();
		List<String> activePluginsConf = brjs.bladerunnerConf().getOrderedPlugins().get(pluginInterfaceName);
		if (activePluginsConf == null || activePluginsConf.isEmpty()) {
			return plugins;
		}
		Set<P> orderedPlugins = new LinkedHashSet<>();
		for(String nextPluginName : activePluginsConf) {
			addMatchingActivePlugin(logger, pluginInterfaceName, plugins, orderedPlugins, nextPluginName);
		}
		orderedPlugins.addAll(plugins);
		
		return new ArrayList<>(orderedPlugins);
	}

	private static <P extends Plugin> void addMatchingActivePlugin(Logger logger, String pluginInterfaceName, List<P> plugins, Set<P> orderedPlugins, String nextPluginName)
	{
		if (plugins.isEmpty()) {
			return;
		}
		boolean matchedPlugin = false;
		
		for (P plugin : plugins) {
			if (nextPluginName.equals("*")) {
				orderedPlugins.add(plugin);
				matchedPlugin = true;
			} else if ( (plugin.getPluginClass().getSimpleName().equals(nextPluginName) || plugin.getPluginClass().getName().equals(nextPluginName)) ){
				if (orderedPlugins.contains(plugin)) {
					orderedPlugins.remove(plugin);
				}
				orderedPlugins.add(plugin);
				return;
			}
		}
		
		if (!matchedPlugin) {
			logger.warn(Messages.NO_MATCHING_PLUGIN, pluginInterfaceName, nextPluginName);
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
