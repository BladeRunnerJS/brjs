package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestSourceAssetLocation;
import org.bladerunnerjs.model.TestPack;

public class BRJSConformantTestPackAssetLocationPlugin extends BRJSConformantAssetLocationPlugin {
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return (assetContainer instanceof TestPack);
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(BRJSConformantAssetLocationPlugin.class.getCanonicalName());
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache)  
	{	
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		TestPack testPack = (TestPack) assetContainer;
		
		if(!assetLocationCache.containsKey("resources")) {
			assetLocationCache.put( "resources", new ResourcesAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources")) );
			assetLocationCache.put( "src-test", new SourceAssetLocation(assetContainer.root(), assetContainer, testPack.testSource().dir()) );
			assetLocationCache.put( "tests", new TestSourceAssetLocation(assetContainer.root(), assetContainer, testPack.tests().dir(), assetLocationCache.get("resources"), assetLocationCache.get("src-test")) );
		}
		
		TestSourceAssetLocation tests = (TestSourceAssetLocation) assetLocationCache.get("tests");
		SourceAssetLocation testSource = (SourceAssetLocation) assetLocationCache.get("src-test");
		
		assetLocations.add(assetLocationCache.get("resources"));
		assetLocations.add(tests);
		assetLocations.addAll(tests.getChildAssetLocations());
		assetLocations.add(testSource);
		assetLocations.addAll(testSource.getChildAssetLocations());
    		
		return assetLocations;
	}

}
