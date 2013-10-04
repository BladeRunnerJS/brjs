package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladeCommander extends NodeCommander<Blade> {
	@SuppressWarnings("unused")
	private final Blade blade;
	public BladeCommander(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
}
