package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.spec.engine.NodeCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class BladesetCommander extends NodeCommander<Bladeset> {
	@SuppressWarnings("unused")
	private final Bladeset bladeset;
	public BladesetCommander(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
		this.bladeset = bladeset;
	}
}
