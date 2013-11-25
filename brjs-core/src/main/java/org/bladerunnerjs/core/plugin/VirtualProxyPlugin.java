package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.model.BRJS;

public class VirtualProxyPlugin implements Plugin {
	private VirtualProxyState proxyState = VirtualProxyState.Uninitialized;
	private Plugin plugin;
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
	
	public Plugin getUnderlyingPlugin()
	{
		return plugin;
	}
	
	@Override
	public boolean instanceOf(Class<? extends Plugin> otherPluginCLass)
	{
		return getUnderlyingPlugin().getClass().equals(otherPluginCLass);
	}
	
	@Override
	public boolean equals(Object o)
	{
		Plugin p = (Plugin) o;
		return p == getUnderlyingPlugin();
	}
	
}
