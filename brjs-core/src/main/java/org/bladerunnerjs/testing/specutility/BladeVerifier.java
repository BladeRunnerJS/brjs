package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;


public class BladeVerifier extends NodeVerifier<Blade> {
	private AssetContainerVerifier assetContainerVerifier;
	
	public BladeVerifier(SpecTest modelTest, Blade blade) {
		super(modelTest, blade);
		assetContainerVerifier = new AssetContainerVerifier(blade);
	}
	
	public VerifierChainer hasSourceModules(SourceModuleDescriptor... sourceModules) throws Exception {
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
