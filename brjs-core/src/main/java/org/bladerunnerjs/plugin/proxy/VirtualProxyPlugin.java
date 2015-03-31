package org.bladerunnerjs.plugin.proxy;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.api.plugin.exception.CircularPluginDependencyException;
import org.bladerunnerjs.plugin.utility.PluginLocatorUtils;

public class VirtualProxyPlugin implements Plugin {
	private Plugin plugin;
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
	public void close() {
		if(proxyState == VirtualProxyState.Initialized) {
			plugin.close();
		}
	}
	
	@Override
	public <P extends Plugin> boolean instanceOf(Class<P> otherPluginCLass)
	{
		return otherPluginCLass.isAssignableFrom(plugin.getClass());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <P extends Plugin> P castTo(Class<P> pluginInterface) {
		initializePlugin();
		return (P) plugin;
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
	
	@Override
	public int hashCode()
	{
		return plugin.hashCode();
	}
}
