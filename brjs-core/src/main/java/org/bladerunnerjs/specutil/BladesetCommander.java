package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladesetCommander extends NodeCommander<Bladeset> {
	@SuppressWarnings("unused")
	private final Bladeset bladeset;
	public BladesetCommander(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
		this.bladeset = bladeset;
	}
}
