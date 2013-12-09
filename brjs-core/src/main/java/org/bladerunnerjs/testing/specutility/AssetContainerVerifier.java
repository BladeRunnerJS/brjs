package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;
import static org.bladerunnerjs.testing.utility.BRJSAssertions.*;

import java.io.StringWriter;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.SourceModule;

public class AssetContainerVerifier {
	private AssetContainer assetContainer;
	
	public AssetContainerVerifier(AssetContainer assetContainer) {
		this.assetContainer = assetContainer;
	}
	
	public void hasSourceModules(SourceModuleDescriptor[] expectedSourceModules) throws Exception {
		List<SourceModule> actualSourceModules = assetContainer.sourceModules();
		
		assertEquals("Expected " + expectedSourceModules.length + " source modules, but there were only " + actualSourceModules.size() + ".", expectedSourceModules.length, actualSourceModules.size());
		
		int i = 0;
		for(SourceModule actualSourceModule : actualSourceModules) {
			SourceModuleDescriptor expectedSourceModule = expectedSourceModules[i++];
			StringWriter sourceModuleContents = new StringWriter();
			
			assertEquals("Source module " + (i + 1) + " differs from what's expected.", expectedSourceModule.requirePath, actualSourceModule.getRequirePath());
			IOUtils.copy(actualSourceModule.getReader(), sourceModuleContents);
			
			for(String expectedFilePath : expectedSourceModule.filePaths) {
				assertContains(expectedFilePath, sourceModuleContents.toString());
			}
		}
	}
	
	public void hasAssetLocations(String[] assetLocations) {
		// TODO Auto-generated method stub
	}
	
	public void assetLocationHasNoDependencies(String assetLocation) {
		// TODO Auto-generated method stub
	}
	
	public void assetLocationHasDependencies(String assetLocation, String[] assetLocationDependencies) {
		// TODO Auto-generated method stub
	}
}
