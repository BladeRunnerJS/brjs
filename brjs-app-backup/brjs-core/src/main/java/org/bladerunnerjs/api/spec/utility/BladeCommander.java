package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.spec.engine.NodeCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class BladeCommander extends NodeCommander<Blade> {
	@SuppressWarnings("unused")
	private final Blade blade;
	public BladeCommander(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
}
