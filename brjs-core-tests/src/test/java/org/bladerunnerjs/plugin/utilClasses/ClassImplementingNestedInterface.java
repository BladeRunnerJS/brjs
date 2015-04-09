package org.bladerunnerjs.plugin.utilClasses;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.base.AbstractPlugin;

public class ClassImplementingNestedInterface extends AbstractPlugin implements InterfaceExtendingAnotherTestInterface {  public ClassImplementingNestedInterface() { }
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}