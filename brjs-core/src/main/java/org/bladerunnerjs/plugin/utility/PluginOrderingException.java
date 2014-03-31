package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.plugin.OrderedPlugin;

import com.google.common.base.Joiner;

public class PluginOrderingException extends Exception {
	private static final long serialVersionUID = 1L;
	private final List<OrderedPlugin> plugins;
	
	@SuppressWarnings("unchecked")
	public <P extends OrderedPlugin> PluginOrderingException(List<P> plugins) {
		super("");
		this.plugins = (List<OrderedPlugin>) plugins;
	}
	
	@Override
	public String getMessage() {
		return "Circular dependency involving the plug-ins " + (Joiner.on(", ").join(getPluginNames(plugins)) + ".");
	}
	
	private <P extends OrderedPlugin> List<String> getPluginNames(List<P> plugins) {
		List<String> pluginNames = new ArrayList<>();
		
		for(P plugin : plugins) {
			pluginNames.add("'" + plugin.getPluginClass().getSimpleName() + "'");
		}
		
		return pluginNames;
	}
}
