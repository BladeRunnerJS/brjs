package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class BladesetVerifier extends NodeVerifier<Bladeset> {
	public BladesetVerifier(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
	}
}
