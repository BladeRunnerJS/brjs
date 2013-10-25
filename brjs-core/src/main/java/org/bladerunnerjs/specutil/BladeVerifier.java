package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladeVerifier extends NodeVerifier<Blade> {
	public BladeVerifier(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
	}
}
