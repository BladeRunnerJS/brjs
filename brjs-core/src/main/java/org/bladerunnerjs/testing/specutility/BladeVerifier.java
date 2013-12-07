package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BladeVerifier extends NodeVerifier<Blade> {
	public BladeVerifier(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
	}
}
