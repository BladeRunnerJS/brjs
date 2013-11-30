package org.bladerunnerjs.core.plugin.utilClasses;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.InstanceOfShouldntBeInvokedException;

public class ClassImplementingNestedInterface implements InterfaceExtendingAnotherTestInterface {  public ClassImplementingNestedInterface() { }
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
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