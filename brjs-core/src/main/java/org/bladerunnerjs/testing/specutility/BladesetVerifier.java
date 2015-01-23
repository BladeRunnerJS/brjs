package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BladesetVerifier extends NodeVerifier<Bladeset> {
	public BladesetVerifier(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
	}
}
