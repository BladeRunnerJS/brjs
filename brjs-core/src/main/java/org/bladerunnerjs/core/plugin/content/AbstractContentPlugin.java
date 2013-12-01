package org.bladerunnerjs.core.plugin.content;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.InstanceOfShouldntBeInvokedException;


public abstract class AbstractContentPlugin implements ContentPlugin
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
