package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;


public class AspectVerifier extends BundlableNodeVerifier<Aspect> {
	private Aspect aspect;
	private AssetContainerVerifier assetContainerVerifier;
	
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
		this.aspect = aspect;
		assetContainerVerifier = new AssetContainerVerifier(aspect);
	}
	
	public VerifierChainer hasAlias(String aliasName, String classRef, String interfaceRef) throws Exception {
		AliasDefinition alias = aspect.aliasesFile().getAlias(aliasName);
		
		assertEquals("Class not as expected for alias '" + aliasName + "'", classRef, alias.getClassName());
		assertEquals("Interface not as expected for alias '" + aliasName + "'", interfaceRef, alias.getInterfaceName());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasAlias(String aliasName, String classRef) throws Exception {
		hasAlias(aliasName, classRef, null);
		
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
	
	public VerifierChainer sourceModuleHasAssetLocation(String sourceModulePath, String assetLocationPath) throws Exception {
		SourceModule sourceModule = (SourceModule)aspect.getLinkedAsset(sourceModulePath);
		AssetLocation assetLocation = aspect.assetLocation(assetLocationPath);
		
		assertEquals("Source module '" + sourceModulePath + "' did not have the asset location '" + assetLocationPath + "'.", assetLocation.dir().getPath(), sourceModule.assetLocation().dir().getPath());
		
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
