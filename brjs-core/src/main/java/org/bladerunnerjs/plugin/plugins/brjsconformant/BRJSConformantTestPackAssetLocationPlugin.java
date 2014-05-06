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
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return (assetContainer instanceof TestPack);
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Arrays.asList(BRJSConformantAssetLocationPlugin.class.getCanonicalName());
	}
	
	public List<File> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<File> assetLocationDirectories = new ArrayList<>();
		
		if(assetContainer instanceof TestPack) {
			((TestPack) assetContainer).tests(); // TODO: get rid of the need for this hack -- tests() should use the plug-in created nodes
			((TestPack) assetContainer).testSource(); // TODO: get rid of the need for this hack -- testSource() should use the plug-in created nodes
			
			File sourceTestDir = assetContainer.file("src-test");
			File testsDir = assetContainer.file("tests");
			
			assetLocationDirectories.add(assetContainer.file("resources"));
			
			assetLocationDirectories.add(sourceTestDir);
			if(sourceTestDir.exists()) {
				assetLocationDirectories.addAll(brjs.getFileInfo(sourceTestDir).nestedDirs());
			}
			
			assetLocationDirectories.add(testsDir);
			if(testsDir.exists()) {
				assetLocationDirectories.addAll(brjs.getFileInfo(testsDir).nestedDirs());
			}
		}
		
		return assetLocationDirectories;
	}
	
	public AssetLocation createAssetLocation(AssetContainer assetContainer, File dir, Map<String, AssetLocation> assetLocationsMap) {
		AssetLocation assetLocation;
		String dirPath = dir.getPath();
		
		if(dirPath.equals(assetContainer.file("resources").getPath())) {
			assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir);
		}
		else if(dirPath.equals(assetContainer.file("tests").getPath())) {
			assetLocation = new TestSourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("resources"), assetLocationsMap.get("src-test"));
		}
		else if(dirPath.equals(assetContainer.file("src-test").getPath())) {
			assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir);
		}
		else {
			String parentLocationPath = normalizePath(RelativePathUtility.get(assetContainer.dir(), dir.getParentFile()));
			AssetLocation parentAssetLocation = assetLocationsMap.get(parentLocationPath);
			
			if((parentAssetLocation instanceof ChildSourceAssetLocation) || (parentAssetLocation instanceof SourceAssetLocation)) {
				assetLocation = new ChildSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
			}
			else {
				assetLocation = new ChildTestSourceAssetLocation(assetContainer.root(), assetContainer, dir, parentAssetLocation);
			}
		}
		
		return assetLocation;
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
