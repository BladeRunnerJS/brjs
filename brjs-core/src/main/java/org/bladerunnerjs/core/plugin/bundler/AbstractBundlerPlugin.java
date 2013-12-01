package org.bladerunnerjs.core.plugin.bundler;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.InstanceOfShouldntBeInvokedException;

public abstract class AbstractBundlerPlugin implements BundlerPlugin
{
	@Override
	public boolean instanceOf(Class<? extends Plugin> otherPluginCLass)
	{
		throw new InstanceOfShouldntBeInvokedException();
	}
	
	@Override
	public Class<?> getPluginClass() {
		return this.getClass();
	}
}
