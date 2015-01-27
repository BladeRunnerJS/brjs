package org.bladerunnerjs.plugin.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.ChildSourceAssetLocation;
import org.bladerunnerjs.model.ChildTestSourceAssetLocation;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestSourceAssetLocation;

public class BRJSConformantTestPackAssetLocationPlugin extends AbstractAssetLocationPlugin {
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(BRJSConformantAssetLocationPlugin.class.getCanonicalName());
	}
	
	@Override
	public List<String> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<String> assetLocationDirectories = new ArrayList<>();
		
		if(assetContainer instanceof TestPack) {
			assetLocationDirectories.add("resources");
			assetLocationDirectories.add("src-test");
			assetLocationDirectories.add("tests");
			
			File sourceTestDir = assetContainer.file("src-test");
			if(sourceTestDir.exists()) {
				for(MemoizedFile dir : brjs.getMemoizedFile(sourceTestDir).nestedDirs()) {
					assetLocationDirectories.add(assetContainer.dir().getRelativePath(dir));
				}
			}
			
			File testsDir = assetContainer.file("tests");
			if(testsDir.exists()) {
				for(MemoizedFile dir : brjs.getMemoizedFile(testsDir).nestedDirs()) {
					assetLocationDirectories.add(assetContainer.dir().getRelativePath(dir));
				}
			}
		}
		
		return assetLocationDirectories;
	}
	
	@Override
	public List<String> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		List<String> seedAssetLocationDirectories = new ArrayList<>();
		
		seedAssetLocationDirectories.add("resources");
		seedAssetLocationDirectories.add("tests");
		
		File testsDir = assetContainer.file("tests");
		if(testsDir.exists()) {
			for(MemoizedFile dir : brjs.getMemoizedFile(testsDir).nestedDirs()) {
				seedAssetLocationDirectories.add(assetContainer.dir().getRelativePath(dir));
			}
		}
		
		return seedAssetLocationDirectories;
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap) {
		AssetLocation assetLocation;
		MemoizedFile dir = assetContainer.file(dirPath);
		
		switch(dirPath) {
			case "resources":
				assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."));
				break;
			
			case "tests":
				assetLocation = new TestSourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."), assetLocationsMap.get("resources"), assetLocationsMap.get("src-test"));
				break;
			
			case "src-test":
				assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."));
				break;
			
			default:
				String parentLocationPath = assetContainer.dir().getRelativePath(dir.getParentFile());
				AssetLocation parentAssetLocation = assetLocationsMap.get(parentLocationPath);
				
				if((parentAssetLocation instanceof ChildSourceAssetLocation) || (parentAssetLocation instanceof SourceAssetLocation)) {
					assetLocation = new ChildSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation, parentAssetLocation);
				}
				else {
					assetLocation = new ChildTestSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
				}
				break;
		}
		
		return assetLocation;
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
}
