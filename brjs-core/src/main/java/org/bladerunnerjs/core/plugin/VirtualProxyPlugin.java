package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.model.BRJS;

public class VirtualProxyPlugin {
	private VirtualProxyState proxyState = VirtualProxyState.Uninitialized;
	private Plugin plugin;
	protected BRJS brjs;
	
	public VirtualProxyPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	
	protected void initializePlugin() {
		if(proxyState == VirtualProxyState.Uninitialized) {
			proxyState = VirtualProxyState.Initlializing;
			plugin.setBRJS(brjs);
			proxyState = VirtualProxyState.Initialized;
		}														
		else if(proxyState == VirtualProxyState.Initlializing) {
			throw new CircularPluginDependencyException(plugin.getClass());
		}
	}
}
