package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.assertEquals;

import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.utility.RelativePathUtility;

import com.google.common.base.Joiner;

public class AssetContainerVerifier {
	private AssetContainer assetContainer;
	
	public AssetContainerVerifier(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
	}
	
	public void hasSourceModules(String... expectedSourceModules) throws Exception {
		
		Set<LinkedAsset> assets = assetContainer.linkedAssets();
		Set<SourceModule> actualSourceModules = new LinkedHashSet<SourceModule>();
		for(LinkedAsset asset : assets){
			if(asset instanceof SourceModule){
				actualSourceModules.add((SourceModule)asset);
			}
		}
		assertEquals("Source modules [" + renderSourceModules(actualSourceModules) + "] was expected to contain " + expectedSourceModules.length + " item(s).", expectedSourceModules.length, actualSourceModules.size());
		
		int i = 0;
		for(SourceModule actualSourceModule : actualSourceModules) {
			String expectedSourceModule = expectedSourceModules[i++];
			StringWriter sourceModuleContents = new StringWriter();
			
			assertEquals("Source module " + i + " differs from what's expected.", expectedSourceModule, actualSourceModule.getPrimaryRequirePath());
			try (Reader reader = actualSourceModule.getReader()) { IOUtils.copy(reader, sourceModuleContents); }
		}
	}
	
	public void hasAssetLocations(String[] expectedAssetLocations) throws Exception {
		List<AssetLocation> actualAssetLocations = assetContainer.assetLocations();
		
		assertEquals("Asset locations [" + renderAssetLocations(actualAssetLocations) + "] was expected to contain " + expectedAssetLocations.length + " item(s).", expectedAssetLocations.length, actualAssetLocations.size());
		
		int i = 0;
		for(AssetLocation actualAssetLocation : actualAssetLocations) {
			String expectedAssetLocation = expectedAssetLocations[i++];
			String actualDependentAssetLocationPath = RelativePathUtility.get(assetContainer.root(), assetContainer.dir(), actualAssetLocation.dir());
			
			if(actualDependentAssetLocationPath.equals("")) {
				actualDependentAssetLocationPath = ".";
			}
			
			assertEquals("Asset location " + i + " differs from what's expected.", expectedAssetLocation, actualDependentAssetLocationPath);
		}
	}

	public void assetLocationHasNoDependencies(String assetLocation) {
		List<AssetLocation> dependentAssetLocations = assetContainer.assetLocation(assetLocation).dependentAssetLocations();
		
		assertEquals("Asset location '" + assetLocation + "' was not expected to have any dependent asset locations.", 0, dependentAssetLocations.size());
	}
	
	public void assetLocationHasDependencies(String assetLocationPath, String[] expectedAssetLocationDependencies) {
		AssetLocation assetLocation = assetContainer.assetLocation(assetLocationPath);
		if(assetLocation == null) {
			throw new RuntimeException("asset location '" + assetLocationPath + "' does not exist.");
		}
		
		List<AssetLocation> actualDependentAssetLocations = assetLocation.dependentAssetLocations();
		
		assertEquals("Asset location '" + assetLocationPath + "' was expected to have " + expectedAssetLocationDependencies.length + " dependent asset locations.",
			expectedAssetLocationDependencies.length, actualDependentAssetLocations.size());
		
		int i = 0;
		for(AssetLocation actualDependentAssetLocation : actualDependentAssetLocations) {
			String expectedAssetLocationDependency = expectedAssetLocationDependencies[i++];
			String actualDependentAssetLocationPath =  RelativePathUtility.get(assetContainer.root(), assetContainer.dir(), actualDependentAssetLocation.dir());
			
			assertEquals(expectedAssetLocationDependency, actualDependentAssetLocationPath);
		}
	}
	
	private String renderSourceModules(Set<SourceModule> sourceModules) {
		List<String> sourceModulePaths = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			sourceModulePaths.add(sourceModule.getPrimaryRequirePath());
		}
		
		return Joiner.on(", ").join(sourceModulePaths);
	}
	
	private String renderAssetLocations(List<AssetLocation> assetLocations) throws Exception {
		List<String> assetLocationPaths = new ArrayList<>();
		
		for(AssetLocation assetLocation : assetLocations) {
			assetLocationPaths.add(RelativePathUtility.get(assetContainer.root(), assetLocation.assetContainer().dir(), assetLocation.dir()));
		}
		
		return Joiner.on(", ").join(assetLocationPaths);
	}
}
