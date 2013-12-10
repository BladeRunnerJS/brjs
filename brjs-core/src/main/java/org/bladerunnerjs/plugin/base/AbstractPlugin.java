package org.bladerunnerjs.plugin.base;

import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;

public abstract class AbstractPlugin implements Plugin {
	@Override
	public boolean equals(Object object) {
		Plugin plugin = (object instanceof VirtualProxyPlugin) ? ((VirtualProxyPlugin) object).getUninitializedUnderlyingPlugin() : (Plugin) object;
		
		return this == plugin;
	}
	
	@Override
	public boolean instanceOf(Class<? extends Plugin> pluginCLass) {
		return pluginCLass.isAssignableFrom(getClass());
	}
	
	@Override
	public Class<?> getPluginClass() {
		return getClass();
	}
}
