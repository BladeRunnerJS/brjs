package org.bladerunnerjs.plugin.utilClasses;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.plugin.base.AbstractPlugin;


public class MyTestInterfaceImplementerBadConstructor extends AbstractPlugin implements MyTestInterface
{
	public MyTestInterfaceImplementerBadConstructor(String s) {
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}
