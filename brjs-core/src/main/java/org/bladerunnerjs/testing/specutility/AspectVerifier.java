package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;
import org.bladerunnerjs.testing.utility.AssetContainerLocationUtility;


public class AspectVerifier extends NodeVerifier<Aspect> {
	private Aspect aspect;
	private AssetContainerVerifier assetContainerVerifier;
	
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
		this.aspect = aspect;
		assetContainerVerifier = new AssetContainerVerifier(aspect);
	}
	
	public void hasAlias(String aliasName, String classRef, String interfaceRef) throws Exception {
		AliasDefinition alias = aspect.aliasesFile().getAlias(aliasName);
		
		assertEquals("Class not as expected for alias '" + aliasName + "'", classRef, alias.getClassName());
		assertEquals("Interface not as expected for alias '" + aliasName + "'", interfaceRef, alias.getInterfaceName());
	}
	
	public void hasAlias(String aliasName, String classRef) throws Exception {
		hasAlias(aliasName, classRef, null);
	}
	
	public VerifierChainer hasSourceModules(SourceModuleDescriptor... sourceModules) throws Exception {
		assetContainerVerifier.hasSourceModules(sourceModules);
		
		return verifierChainer;
	}
	
	public VerifierChainer hasAssetLocations(String... assetLocations) {
		assetContainerVerifier.hasAssetLocations(assetLocations);
		
		return verifierChainer;
	}
	
	public VerifierChainer sourceModuleHasAssetLocation(String sourceModulePath, String assetLocationPath) throws Exception {
		SourceModule sourceModule = aspect.getSourceModule(sourceModulePath);
		AssetLocation assetLocation = AssetContainerLocationUtility.getAssetLocation(aspect, assetLocationPath);
		
		assertSame("Source module '" + sourceModulePath + "' did not have the asset location '" + assetLocationPath + "'.", assetLocation, sourceModule.getAssetLocation());
		
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
