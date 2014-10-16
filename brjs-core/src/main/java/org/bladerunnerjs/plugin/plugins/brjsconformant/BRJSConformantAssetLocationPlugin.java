package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.BladeResourcesAssetLocation;
import org.bladerunnerjs.model.ChildSourceAssetLocation;
import org.bladerunnerjs.model.ChildTestSourceAssetLocation;
import org.bladerunnerjs.model.DefaultBladeset;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin {
	private final List<String> seedAssetLocationDirectories = new ArrayList<>();
	private BRJS brjs;
	
	{
		seedAssetLocationDirectories.add("resources");
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public List<String> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<String> assetLocationDirectories = new ArrayList<>();
		
		if (assetContainer instanceof DefaultBladeset) {
			return assetLocationDirectories;
		}
		
		assetLocationDirectories.add(".");
		assetLocationDirectories.add("resources");
		assetLocationDirectories.add("src");
		assetLocationDirectories.add("src-test");
		addThemeDirectories(assetLocationDirectories, assetContainer);
		
		File sourceDir = assetContainer.file("src");
		if(sourceDir.exists()) {
			for(File dir : brjs.getFileInfo(sourceDir).nestedDirs()) {
				assetLocationDirectories.add(RelativePathUtility.get(brjs, assetContainer.dir(), dir));
			}
		}
		
		File sourceTestDir = assetContainer.file("src-test");
		if(sourceTestDir.exists()) {
			for(File dir : brjs.getFileInfo(sourceTestDir).nestedDirs()) {
				assetLocationDirectories.add(RelativePathUtility.get(brjs, assetContainer.dir(), dir));
			}
		}
		return assetLocationDirectories;
	}
	
	private void addThemeDirectories(List<String> assetLocationDirectories, AssetContainer assetContainer) {
		File themesDir = assetContainer.file("themes");
		if(!themesDir.exists()){
			return;
		}
		
		FileInfo themesDirInfo = brjs.getFileInfo(themesDir);
		for(File themeDir : themesDirInfo.dirs()) {
			String relativePath = RelativePathUtility.get(brjs, assetContainer.dir(), themeDir);
			assetLocationDirectories.add(relativePath);
		}
	}

	@Override
	public List<String> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		return seedAssetLocationDirectories;
	}
	
	@Override
	public AssetLocation createAssetLocation(AssetContainer assetContainer, String dirPath, Map<String, AssetLocation> assetLocationsMap) {
		AssetLocation assetLocation;
		File dir = assetContainer.file(dirPath);
		
		
		switch(dirPath) {
			case ".":
				assetLocation = (assetContainer instanceof JsLib) ? new BRJSConformantJsLibRootAssetLocation(assetContainer.root(), assetContainer, dir, null) :
					new BRJSConformantRootAssetLocation(assetContainer.root(), assetContainer, dir, null);
				break;
			
			case "resources":
				if (assetContainer instanceof Blade) {
					assetLocation = new BladeResourcesAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."));
				} else {
					assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."));
				}
				break;
			
			case "src":
				assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."), assetLocationsMap.get("resources"));
				break;
			
			case "src-test":
				assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."));
				break;
			
			default:
				if(dirPath.startsWith("themes")){
					
					String themeName = dirPath.substring(7);
					assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("."), themeName);
					break;
				}
				
				String parentLocationPath = RelativePathUtility.get(brjs, assetContainer.dir(), dir.getParentFile());
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
