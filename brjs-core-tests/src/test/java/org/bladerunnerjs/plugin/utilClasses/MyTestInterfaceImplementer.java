package org.bladerunnerjs.plugin.utilClasses;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.base.AbstractPlugin;

public class MyTestInterfaceImplementer extends AbstractPlugin implements MyTestInterface {
	public MyTestInterfaceImplementer() {
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}