package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BladeCommander extends NodeCommander<Blade> {
	@SuppressWarnings("unused")
	private final Blade blade;
	public BladeCommander(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
}
