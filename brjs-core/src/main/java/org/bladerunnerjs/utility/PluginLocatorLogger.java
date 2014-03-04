package org.bladerunnerjs.utility;

import java.util.List;

import org.bladerunnerjs.model.BRJS.Messages;
import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.PluginLocator;

public class PluginLocatorLogger {
	public static void logPlugins(org.bladerunnerjs.logging.Logger logger, PluginLocator pluginLocator) {
		listFoundPlugins(logger, pluginLocator.getModelObserverPlugins());
		listFoundPlugins(logger, pluginLocator.getCommandPlugins());
		listFoundPlugins(logger, pluginLocator.getTagHandlerPlugins()); // TODO: add to spec tests
		listFoundPlugins(logger, pluginLocator.getContentPlugins()); // TODO: add to spec tests
		listFoundPlugins(logger, pluginLocator.getMinifierPlugins()); // TODO: add to spec tests
	}
	
	private static void listFoundPlugins(org.bladerunnerjs.logging.Logger logger, List<? extends Plugin> plugins) {
		for (Plugin plugin : plugins) {
			logger.debug(Messages.PLUGIN_FOUND_MSG, plugin.getPluginClass().getCanonicalName());
		}
	}
}
