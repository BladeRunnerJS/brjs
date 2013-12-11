package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;
import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;

public class AssetContainerVerifier {
	private AssetContainer assetContainer;
	
	public AssetContainerVerifier(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
	}
	
	public void hasSourceModules(SourceModuleDescriptor[] expectedSourceModules) throws Exception {
		List<SourceModule> actualSourceModules = assetContainer.sourceModules();
		
		assertEquals("Expected " + expectedSourceModules.length + " source modules, but there were actually " + actualSourceModules.size() + ".", expectedSourceModules.length, actualSourceModules.size());
		
		int i = 0;
		for(SourceModule actualSourceModule : actualSourceModules) {
			SourceModuleDescriptor expectedSourceModule = expectedSourceModules[i++];
			StringWriter sourceModuleContents = new StringWriter();
			
			assertEquals("Source module " + i + " differs from what's expected.", expectedSourceModule.requirePath, actualSourceModule.getRequirePath());
			IOUtils.copy(actualSourceModule.getReader(), sourceModuleContents);
			
			for(String expectedFilePath : expectedSourceModule.filePaths) {
				assertContains(expectedFilePath, sourceModuleContents.toString());
			}
		}
	}
	
	public void hasAssetLocations(String[] expectedAssetLocations) {
		List<AssetLocation> actualAssetLocations = assetContainer.assetLocations();
		
		assertEquals("Expected " + expectedAssetLocations.length + " asset locations, but there were actually " + actualAssetLocations.size() + ".", expectedAssetLocations.length, actualAssetLocations.size());
		
		int i = 0;
		for(AssetLocation actualAssetLocation : actualAssetLocations) {
			String expectedAssetLocation = expectedAssetLocations[i++];
			String actualDependentAssetLocationPath =  assetContainer.dir().toURI().relativize(actualAssetLocation.dir().toURI()).getPath();
			
			assertEquals("Asset location " + i + " differs from what's expected.", expectedAssetLocation, actualDependentAssetLocationPath);
		}
	}
	
	public void assetLocationHasNoDependencies(String assetLocation) {
		List<AssetLocation> dependentAssetLocations = assetContainer.assetLocation(assetLocation).getDependentAssetLocations();
		
		assertEquals("Asset location '" + assetLocation + "' was not expected to have any dependent asset locations.", 0, dependentAssetLocations.size());
	}
	
	public void assetLocationHasDependencies(String assetLocationPath, String[] expectedAssetLocationDependencies) {
		List<AssetLocation> actualDependentAssetLocations = assetContainer.assetLocation(assetLocationPath).getDependentAssetLocations();
		
		assertEquals("Asset location '" + assetLocationPath + "' was expected to have " + expectedAssetLocationDependencies.length + " dependent asset locations.",
			expectedAssetLocationDependencies.length, actualDependentAssetLocations.size());
		
		int i = 0;
		for(AssetLocation actualDependentAssetLocation : actualDependentAssetLocations) {
			String expectedAssetLocationDependency = expectedAssetLocationDependencies[i++];
			String actualDependentAssetLocationPath =  assetContainer.dir().toURI().relativize(actualDependentAssetLocation.dir().toURI()).getPath();
			
			assertEquals(expectedAssetLocationDependency, actualDependentAssetLocationPath);
		}
	}
}
