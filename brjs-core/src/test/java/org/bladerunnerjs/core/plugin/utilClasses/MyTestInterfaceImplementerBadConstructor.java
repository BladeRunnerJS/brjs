package org.bladerunnerjs.core.plugin.utilClasses;

import org.bladerunnerjs.model.BRJS;


public class MyTestInterfaceImplementerBadConstructor implements MyTestInterface
{
	public MyTestInterfaceImplementerBadConstructor(String s) {
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}
