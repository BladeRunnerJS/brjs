package org.bladerunnerjs.plugin.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BladeResourcesAssetLocation;
import org.bladerunnerjs.model.ChildSourceAssetLocation;
import org.bladerunnerjs.model.ChildTestSourceAssetLocation;
import org.bladerunnerjs.model.DefaultBladeset;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;

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
		
		MemoizedFile sourceDir = assetContainer.file("src");
		if(sourceDir.exists()) {
			for(MemoizedFile dir : brjs.getMemoizedFile(sourceDir).nestedDirs()) {
				assetLocationDirectories.add(assetContainer.dir().getRelativePath(dir));
			}
		}
		
		File sourceTestDir = assetContainer.file("src-test");
		if(sourceTestDir.exists()) {
			for(MemoizedFile dir : brjs.getMemoizedFile(sourceTestDir).nestedDirs()) {
				assetLocationDirectories.add(assetContainer.dir().getRelativePath(dir));
			}
		}
		return assetLocationDirectories;
	}
	
	private void addThemeDirectories(List<String> assetLocationDirectories, AssetContainer assetContainer) {
		MemoizedFile themesDir = brjs.getMemoizedFile( assetContainer.file("themes") );
		
		if(!themesDir.exists()){
			return;
		}
		
		for(MemoizedFile themeDir : themesDir.dirs()) {
			String relativePath = assetContainer.dir().getRelativePath(themeDir);
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
		MemoizedFile dir = assetContainer.file(dirPath);
		
		
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
