package org.bladerunnerjs.core.plugin.observer;

import org.bladerunnerjs.core.plugin.ModelObserverPlugin;
import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.InstanceOfShouldntBeInvokedException;


public abstract class AbstractModelObserverPlugin implements ModelObserverPlugin
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
