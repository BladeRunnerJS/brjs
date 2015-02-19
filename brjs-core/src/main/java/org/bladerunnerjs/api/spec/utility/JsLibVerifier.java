package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;


public class JsLibVerifier extends NodeVerifier<JsLib> {
	private final JsLib jsLib;
	private final AssetContainerVerifier assetContainerVerifier;
	private final VerifierChainer verifierChainer;
	
	public JsLibVerifier(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
		assetContainerVerifier = new AssetContainerVerifier(jsLib);
		verifierChainer = new VerifierChainer(modelTest);
	}
	
	public void nameIsValid() {
		assertTrue(jsLib.getName() + " is not a valid name" , jsLib.isValidName());
	}
	
	public void nameIsInvalid() {
		assertFalse(jsLib.getName() + " is a valid name, but shouldn't be" , jsLib.isValidName());
	}

	public VerifierChainer getAppReturns(App app)
	{
		assertSame(app, jsLib.app());
		
		return verifierChainer;
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
