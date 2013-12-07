package org.bladerunnerjs.plugin.utilClasses;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.AbstractPlugin;

public class MyTestInterfaceImplementer extends AbstractPlugin implements MyTestInterface {
	public MyTestInterfaceImplementer() {
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}