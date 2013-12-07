package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class BladesetBuilder extends AssetContainerBuilder<Bladeset> {
	public Bladeset bladeset;
	
	public BladesetBuilder(SpecTest modelTest, Bladeset bladeset) {
		super(modelTest, bladeset);
		this.bladeset = bladeset;
	}
}
