package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.spec.engine.AssetContainerBuilder;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class BladesetBuilder extends AssetContainerBuilder<Bladeset> {
	public Bladeset bladeset;
	
	public BladesetBuilder(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
		this.bladeset = bladeset;
	}
}
