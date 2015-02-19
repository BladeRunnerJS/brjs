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
	
	public VerifierChainer hasAssetLocations(String... assetLocations) throws Exception {
		assetContainerVerifier.hasAssetLocations(assetLocations);
		
		return verifierChainer;
	}
	
	public VerifierChainer assetLocationHasNoDependencies(String assetLocation) {
		assetContainerVerifier.assetLocationHasNoDependencies(assetLocation);
		
		return verifierChainer;
	}
	
	public VerifierChainer assetLocationHasDependencies(String assetLocation, String... assetLocationDependencies) {
		assetContainerVerifier.assetLocationHasDependencies(assetLocation, assetLocationDependencies);
		
		return verifierChainer;
	}
}
