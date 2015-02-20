package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;

import com.google.common.base.Joiner;


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
	
	public VerifierChainer classHasPreExportDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.linkedAsset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getPreExportDefineTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasPostExportDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.linkedAsset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getPostExportDefineTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}
	
	public VerifierChainer classHasUseTimeDependencies(String requirePath, String... expectedRequirePaths) throws Exception {
		SourceModule sourceModule = (SourceModule) aspect.linkedAsset(requirePath);
		List<String> actualRequirePaths = requirePaths(sourceModule.getUseTimeDependentAssets(aspect));
		
		assertEquals(Joiner.on(", ").join(expectedRequirePaths), Joiner.on(", ").join(actualRequirePaths));
		
		return verifierChainer;
	}

	private List<String> requirePaths(List<Asset> assets) {
		List<String> requirePaths = new ArrayList<>();
		
		for(Asset asset : assets) {
			SourceModule sourceModule = (SourceModule) asset;
			requirePaths.add(sourceModule.getRequirePaths().get(0));
		}
		
		return requirePaths;
	}
}
