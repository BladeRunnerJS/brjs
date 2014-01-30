package org.bladerunnerjs.plugin.base;

import org.bladerunnerjs.plugin.Plugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;


/**
 * The <code>AbstractPlugin</code> class is used to provide common implementations of the plug-in methods needed to overcome the issues caused by the use
 * of <a href="http://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">virtual proxy</a> wrappers (see {@link org.bladerunnerjs.plugin.Plugin} for more
 * details). Since this class is abstract, developers will need to extend one of this classes sub-classes to make use of this functionality.
 */
public abstract class AbstractPlugin implements Plugin {
	@Override
	public boolean equals(Object object) {
		Plugin plugin = (object instanceof VirtualProxyPlugin) ? ((VirtualProxyPlugin) object).getUninitializedUnderlyingPlugin() : (Plugin) object;
		
		return this == plugin;
	}
	
	@Override
	public void close() {
		// do nothing -- concrete implementations may choose to override this method, but very few will ever need to
	}
	
	@Override
	public boolean instanceOf(Class<? extends Plugin> pluginCLass) {
		return pluginCLass.isAssignableFrom(getClass());
	}
	
	@Override
	public Class<?> getPluginClass() {
		return getClass();
	}
	
	@Override
	public int priority()
	{
		return 0;
	}
	
}
