package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.spec.engine.AssetContainerBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class BladeBuilder extends AssetContainerBuilder<Blade> {
	public Blade blade;
	
	public BladeBuilder(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
}
