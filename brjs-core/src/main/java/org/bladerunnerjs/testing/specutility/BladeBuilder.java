package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BladeBuilder extends AssetContainerBuilder<Blade> {
	public Blade blade;
	
	public BladeBuilder(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
}
