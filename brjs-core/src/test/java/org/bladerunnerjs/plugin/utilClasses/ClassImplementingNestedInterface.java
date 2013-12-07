package org.bladerunnerjs.plugin.utilClasses;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AbstractPlugin;

public class ClassImplementingNestedInterface extends AbstractPlugin implements InterfaceExtendingAnotherTestInterface {  public ClassImplementingNestedInterface() { }
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}