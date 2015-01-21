package org.bladerunnerjs.plugin.utilClasses;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.base.AbstractPlugin;


public class MyTestInterfaceImplementerBadConstructor extends AbstractPlugin implements MyTestInterface
{
	public MyTestInterfaceImplementerBadConstructor(String s) {
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}
