package org.bladerunnerjs.plugin;

import org.bladerunnerjs.model.BRJS;

public class VirtualProxyPlugin implements Plugin {
	// package private
	Plugin plugin;
	
	private VirtualProxyState proxyState = VirtualProxyState.Uninitialized;
	private BRJS brjs;
	
	public VirtualProxyPlugin(Plugin plugin) {
		this.plugin = plugin;
	}
	
	protected void initializePlugin() {
		if(proxyState == VirtualProxyState.Uninitialized) {
			proxyState = VirtualProxyState.Initlializing;
			if (brjs == null)
			{
				throw new RuntimeException("BRJS hasn't been set!");
			}
			PluginLocatorUtils.setBRJSForPlugins(brjs, plugin);
			proxyState = VirtualProxyState.Initialized;
		}														
		else if(proxyState == VirtualProxyState.Initlializing) {
			throw new CircularPluginDependencyException(plugin.getClass());
		}
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public boolean instanceOf(Class<? extends Plugin> otherPluginCLass)
	{
		return otherPluginCLass.isAssignableFrom(plugin.getClass());
	}
	
	@Override
	public Class<?> getPluginClass() {
		return plugin.getClass();
	}
	
	@Override
	public boolean equals(Object object)
	{
		Plugin otherPlugin = (object instanceof VirtualProxyPlugin) ? ((VirtualProxyPlugin) object).plugin : (Plugin) object;
		
		return plugin == otherPlugin;
	}
	
	public Plugin getUnderlyingPlugin() {
		initializePlugin();
		
		return plugin;
	}
}
