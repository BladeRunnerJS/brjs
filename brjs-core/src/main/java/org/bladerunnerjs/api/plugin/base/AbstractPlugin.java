package org.bladerunnerjs.api.plugin.base;

import org.bladerunnerjs.api.plugin.Plugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyPlugin;


/**
 * The <code>AbstractPlugin</code> class is used to provide common implementations of the plug-in methods needed to overcome the issues caused by the use
 * of <a href="http://en.wikipedia.org/wiki/Lazy_loading#Virtual_proxy">virtual proxy</a> wrappers (see {@link org.bladerunnerjs.api.plugin.Plugin} for more
 * details). Since this class is abstract, developers will need to extend one of this classes sub-classes to make use of this functionality.
 */
public abstract class AbstractPlugin implements Plugin {
	@Override
	public boolean equals(Object otherPlugin) {
		return (otherPlugin instanceof VirtualProxyPlugin) ? otherPlugin.equals(this) : (this == otherPlugin);
	}
	
	@Override
	public void close() {
		// do nothing -- concrete implementations may choose to override this method, but very few will ever need to
	}
	
	@Override
	public <P extends Plugin> boolean instanceOf(Class<P> pluginInterface) {
		return pluginInterface.isAssignableFrom(getClass());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <P extends Plugin> P castTo(Class<P> pluginInterface) {
		return (P) this;
	}
	
	@Override
	public Class<?> getPluginClass() {
		return getClass();
	}
}
