package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;


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
		assertSame(app, jsLib.getApp());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasSourceModules(SourceModuleDescriptor... sourceModules) throws Exception {
		assetContainerVerifier.hasSourceModules(sourceModules);
		
		return verifierChainer;
	}

	public VerifierChainer hasAssetLocations(String... assetLocations) {
		assetContainerVerifier.hasAssetLocations(assetLocations);
		
		return verifierChainer;
	}
	
	public VerifierChainer assetLocationHasNoDependencies(String assetLocation) {
		assetContainerVerifier.assetLocationHasNoDependencies(assetLocation);
		
		return verifierChainer;
	}
}
