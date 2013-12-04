package org.bladerunnerjs.core.plugin.utilClasses;

import org.bladerunnerjs.core.plugin.AbstractPlugin;
import org.bladerunnerjs.model.BRJS;


public class MyTestInterfaceImplementerBadConstructor extends AbstractPlugin implements MyTestInterface
{
	public MyTestInterfaceImplementerBadConstructor(String s) {
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
}
