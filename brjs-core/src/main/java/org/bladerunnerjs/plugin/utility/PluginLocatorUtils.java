package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.plugin.Plugin;


public class PluginLocatorUtils
{

	public class Messages {
		public static final String INIT_PLUGIN_ERROR_MSG = "error initializing the plugin %s, the error was: '%s'";
		public static final String NO_MATCHING_PLUGIN = "No plugin found which matches the active %s plugin configuration '%s'.";
		public static final String PLUGIN_ENABLED_MESSAGE = "The plugin '%s' will be enabled as it matched the active %s plugin configuration '%s'.";
		public static final String PLUGIN_DISABLED_EMPTY_ACTIVE_PLUGINS_MESSAGE = "The plugin '%s' has been disabled as there we no active %s plugins.";
		public static final String PLUGIN_DISABLED_MESSAGE = "The plugin '%s' has been disabled as it didn't match the active %s plugin configuration which was '%s'.";
	}
	
	public static <P extends Plugin> List<P> filterAndOrderPlugins(BRJS brjs, Class<? extends Plugin> pluginInterface, List<P> plugins) throws ConfigException {
		Logger logger = brjs.logger(PluginLocatorUtils.class);
		String pluginInterfaceName = pluginInterface.getSimpleName();
		List<String> activePluginsConf = brjs.bladerunnerConf().getOrderedPlugins().get(pluginInterfaceName);
		if (activePluginsConf == null || activePluginsConf.isEmpty()) {
			for (Plugin plugin : plugins) {
				logger.debug(Messages.PLUGIN_DISABLED_EMPTY_ACTIVE_PLUGINS_MESSAGE, plugin.getClass().getName(), pluginInterfaceName);
			}
			return Collections.emptyList();
		}
		List<P> activePlugins = new ArrayList<>();
		for(String nextPluginName : activePluginsConf) {
			addMatchingActivePlugin(logger, pluginInterfaceName, plugins, activePlugins, nextPluginName);
		}
		
		for (Plugin plugin : plugins) {
			if (!activePlugins.contains(plugin)) {
				logger.debug(Messages.PLUGIN_DISABLED_MESSAGE, plugin.getClass().getName(), pluginInterfaceName, StringUtils.join(activePluginsConf, ", "));
			}
		}
		
		return activePlugins;
	}

	private static <P extends Plugin> void addMatchingActivePlugin(Logger logger, String pluginInterfaceName, List<P> plugins, List<P> activePlugins, String nextPluginName)
	{
		if (plugins.isEmpty()) {
			return;
		}
		boolean matchedPlugin = false;
		
		for (P plugin : plugins) {
			if (activePlugins.contains(plugin)) {
				continue;
			} else if (nextPluginName.equals("*")) {
				logger.debug(Messages.PLUGIN_ENABLED_MESSAGE, plugin.getClass().getName(), pluginInterfaceName, nextPluginName);
				activePlugins.add(plugin);
				matchedPlugin = true;
			} else if ( (plugin.getPluginClass().getSimpleName().equals(nextPluginName) || plugin.getPluginClass().getName().equals(nextPluginName)) ){
				logger.debug(Messages.PLUGIN_ENABLED_MESSAGE, plugin.getClass().getName(), pluginInterfaceName, nextPluginName);
				activePlugins.add(plugin);
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
