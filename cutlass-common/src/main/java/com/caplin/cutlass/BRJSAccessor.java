package com.caplin.cutlass;

import org.bladerunnerjs.model.BRJS;

public class BRJSAccessor {
	public static BRJS root;
	
	public static BRJS initialize(BRJS brjs) {
		root = brjs;
		return brjs;
	}
}
