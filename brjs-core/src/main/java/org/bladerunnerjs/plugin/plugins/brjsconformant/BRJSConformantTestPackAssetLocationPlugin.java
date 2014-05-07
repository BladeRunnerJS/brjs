package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ChildSourceAssetLocation;
import org.bladerunnerjs.model.ChildTestSourceAssetLocation;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestSourceAssetLocation;
import org.bladerunnerjs.model.TestPack;
import org.bladerunnerjs.utility.RelativePathUtility;

public class BRJSConformantTestPackAssetLocationPlugin extends BRJSConformantAssetLocationPlugin {
	private BRJS brjs;
	
	@Override
	public void setBRJS(BRJS brjs) {
		super.setBRJS(brjs);
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
			((TestPack) assetContainer).tests(); // TODO: get rid of the need for this hack -- tests() should use the plug-in created nodes
			((TestPack) assetContainer).testSource(); // TODO: get rid of the need for this hack -- testSource() should use the plug-in created nodes
			
			assetLocationDirectories.add("resources");
			assetLocationDirectories.add("src-test");
			assetLocationDirectories.add("tests");
			
			File sourceTestDir = assetContainer.file("src-test");
			if(sourceTestDir.exists()) {
				for(File dir : brjs.getFileInfo(sourceTestDir).nestedDirs()) {
					assetLocationDirectories.add(RelativePathUtility.get(assetContainer.dir(), dir));
				}
			}
			
			File testsDir = assetContainer.file("tests");
			if(testsDir.exists()) {
				for(File dir : brjs.getFileInfo(testsDir).nestedDirs()) {
					assetLocationDirectories.add(RelativePathUtility.get(assetContainer.dir(), dir));
				}
			}
		}
		
		return assetLocationDirectories;
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap) {
		AssetLocation assetLocation;
		File dir = assetContainer.file(dirPath);
		
		switch(dirPath) {
			case "resources":
				assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir);
				break;
			
			case "tests":
				assetLocation = new TestSourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("resources"), assetLocationsMap.get("src-test"));
				break;
			
			case "src-test":
				assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir);
				break;
			
			default:
				String parentLocationPath = RelativePathUtility.get(assetContainer.dir(), dir.getParentFile());
				AssetLocation parentAssetLocation = assetLocationsMap.get(parentLocationPath);
				
				if((parentAssetLocation instanceof ChildSourceAssetLocation) || (parentAssetLocation instanceof SourceAssetLocation)) {
					assetLocation = new ChildSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
				}
				else {
					assetLocation = new ChildTestSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
				}
				break;
		}
		
		return assetLocation;
	}
}
