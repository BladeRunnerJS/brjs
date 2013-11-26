package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.specutil.engine.AssetContainerBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladeBuilder extends AssetContainerBuilder<Blade> {
	public Blade blade;
	
	public BladeBuilder(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		this.blade = blade;
	}
}
