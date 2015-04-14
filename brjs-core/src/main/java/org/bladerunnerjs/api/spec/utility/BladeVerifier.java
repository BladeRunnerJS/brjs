package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;


public class BladeVerifier extends NodeVerifier<Blade> {
	private AssetContainerVerifier assetContainerVerifier;
	
	public BladeVerifier(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		assetContainerVerifier = new AssetContainerVerifier(blade);
	}
	
	public VerifierChainer hasSourceModules(String... sourceModules) throws Exception {
		assetContainerVerifier.hasSourceModules(sourceModules);
		
		return verifierChainer;
	}
	
}
