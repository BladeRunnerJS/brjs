package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BRJSVerifier extends NodeVerifier<BRJS> {
	public BRJSVerifier(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
	}
}
