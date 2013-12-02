package org.bladerunnerjs.core.plugin;

import org.bladerunnerjs.core.plugin.Plugin;

public abstract class AbstractPlugin implements Plugin {
	@Override
	public boolean equals(Object object) {
		Plugin plugin = (object instanceof VirtualProxyPlugin) ? ((VirtualProxyPlugin) object).plugin : (Plugin) object;
		
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
