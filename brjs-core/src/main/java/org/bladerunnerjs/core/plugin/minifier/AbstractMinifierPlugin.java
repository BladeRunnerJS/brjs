package org.bladerunnerjs.core.plugin.minifier;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.InstanceOfShouldntBeInvokedException;


public abstract class AbstractMinifierPlugin implements MinifierPlugin
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
