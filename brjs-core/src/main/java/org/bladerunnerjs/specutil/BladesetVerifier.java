package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladesetVerifier extends NodeVerifier<Bladeset> {
	public BladesetVerifier(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
	}
}
