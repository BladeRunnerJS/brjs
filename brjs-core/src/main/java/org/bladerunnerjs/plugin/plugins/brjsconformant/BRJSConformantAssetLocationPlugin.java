package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.DeepAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.model.ThemeAssetLocation;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;

public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return true;
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache) 
	{
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		if (assetContainer instanceof TestPack)
		{
			TestPack testPack = (TestPack) assetContainer;
			
			if(!assetLocationCache.containsKey("tests")) {
				assetLocationCache.put("tests", new DeepAssetLocation(assetContainer.root(), assetContainer, testPack.tests().dir()));
				assetLocationCache.put("src-test", new SourceAssetLocation(assetContainer.root(), assetContainer, testPack.testSource().dir()));
			}
			
			DeepAssetLocation tests = (DeepAssetLocation) assetLocationCache.get("tests");
			SourceAssetLocation testSource = (SourceAssetLocation) assetLocationCache.get("src-test");
			
			assetLocations.add(tests);
			assetLocations.add(testSource);
			assetLocations.addAll(testSource.getChildAssetLocations());
		}
		else
		{
			if(!assetLocationCache.containsKey("resources")) {
				assetLocationCache.put("resources", new DeepAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources")));
				assetLocationCache.put("themes", new ThemeAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("themes")));
				assetLocationCache.put("src", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src"), assetLocationCache.get("resources")));
				assetLocationCache.put("src-test", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src-test")));
			}
			
			assetLocations.add(assetLocationCache.get("resources"));
			assetLocations.add(assetLocationCache.get("themes"));
			SourceAssetLocation srcAssetLocation = (SourceAssetLocation) assetLocationCache.get("src");
			assetLocations.add(srcAssetLocation);
			assetLocations.addAll(srcAssetLocation.getChildAssetLocations());
			SourceAssetLocation srcTestAssetLocation = (SourceAssetLocation) assetLocationCache.get("src-test");
			assetLocations.add(srcTestAssetLocation);
			assetLocations.addAll( srcTestAssetLocation.getChildAssetLocations() ) ;
		}
		
		return assetLocations;
	}
}
