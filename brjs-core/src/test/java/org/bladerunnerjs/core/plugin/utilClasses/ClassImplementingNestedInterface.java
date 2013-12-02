package org.bladerunnerjs.core.plugin.utilClasses;

import org.bladerunnerjs.core.plugin.AbstractPlugin;
import org.bladerunnerjs.model.BRJS;

public class ClassImplementingNestedInterface extends AbstractPlugin implements InterfaceExtendingAnotherTestInterface {  public ClassImplementingNestedInterface() { }
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}