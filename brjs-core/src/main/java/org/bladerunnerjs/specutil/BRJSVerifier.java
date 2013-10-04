package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BRJSVerifier extends NodeVerifier<BRJS> {
	public BRJSVerifier(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
	}
}
