package org.bladerunnerjs.plugin.utility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.plugin.OrderedPlugin;

public class PluginSorter {
	public static <P extends OrderedPlugin> List<P> sort(List<P> plugins) throws PluginOrderingException, NonExistentPluginException {
		Map<P, Set<P>> unmetDependencies = getPluginDependencies(plugins);
		List<P> unorderedPlugins = new ArrayList<>(plugins);
		List<P> orderedPlugins = new ArrayList<>();
		
		while(!unorderedPlugins.isEmpty()) {
			int startSize = unorderedPlugins.size();
			
			for(P plugin : unorderedPlugins) {
				Set<P> unmetPluginDependencies = unmetDependencies.get(plugin);
				
				if(unmetPluginDependencies.isEmpty()) {
					orderedPlugins.add(plugin);
					unorderedPlugins.remove(plugin);
					
					for(Set<P> nextUnmetPluginDependencies : unmetDependencies.values()) {
						nextUnmetPluginDependencies.remove(plugin);
					}
					break;
				}
			}
			
			if(unorderedPlugins.size() == startSize) {
				throw new PluginOrderingException(unorderedPlugins);
			}
		}
		
		return orderedPlugins;
	}
	
	private static <P extends OrderedPlugin> Map<P, Set<P>> getPluginDependencies(List<P> plugins) throws NonExistentPluginException {
		Map<String, P> pluginMap = new HashMap<>();
		Map<P, Set<P>> pluginDependencies = new HashMap<>();
		
		for(P plugin : plugins) {
			pluginMap.put(plugin.getPluginClass().getCanonicalName(), plugin);
			pluginDependencies.put(plugin, new HashSet<P>());
		}
		
		for(P plugin : plugins) {
			for(String pluginName : plugin.getPluginsThatMustAppearAfterThisPlugin()) {
				pluginDependencies.get(getPlugin(pluginName, pluginMap, plugin)).add(plugin);
			}
			
			Set<P> dependentPlugins = pluginDependencies.get(plugin);
			for(String pluginName : plugin.getPluginsThatMustAppearBeforeThisPlugin()) {
				dependentPlugins.add(getPlugin(pluginName, pluginMap, plugin));
			}
		}
		
		return pluginDependencies;
	}
	
	private static <P extends OrderedPlugin> P getPlugin(String pluginName, Map<String, P> pluginMap, P plugin) throws NonExistentPluginException {
		if(!pluginMap.containsKey(pluginName)) {
			throw new NonExistentPluginException(plugin.getPluginClass().getSimpleName(), pluginName);
		}
		
		return pluginMap.get(pluginName);
	}
}
