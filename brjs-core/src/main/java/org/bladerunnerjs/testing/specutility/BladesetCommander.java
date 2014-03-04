package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BladesetCommander extends NodeCommander<Bladeset> {
	@SuppressWarnings("unused")
	private final Bladeset bladeset;
	public BladesetCommander(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
		this.bladeset = bladeset;
	}
}
