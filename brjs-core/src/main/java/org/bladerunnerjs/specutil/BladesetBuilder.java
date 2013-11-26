package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.specutil.engine.AssetContainerBuilder;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class BladesetBuilder extends AssetContainerBuilder<Bladeset> {
	public Bladeset bladeset;
	
	public BladesetBuilder(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
		this.bladeset = bladeset;
	}
}
