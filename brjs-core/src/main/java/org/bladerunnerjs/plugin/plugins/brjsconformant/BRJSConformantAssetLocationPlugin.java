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
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestSourceAssetLocation;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin {
	private final List<String> seedAssetLocationDirectories = new ArrayList<>();
	private BRJS brjs;
	
	{
		seedAssetLocationDirectories.add("resources");
	}
	
//JT:TODO
//	public static List<String> getBundlableNodeThemes(BundlableNode bundlableNode) {
//		Set<String> themeNames = new HashSet<>();
//		
//		List<AssetContainer> scopeAssetContainers = bundlableNode.scopeAssetContainers();
//		AssetLocation themedNodeResources;
//		if (bundlableNode instanceof Workbench) {
//			Blade blade = bundlableNode.root().locateAncestorNodeOfClass(bundlableNode, Blade.class);
//			themedNodeResources = blade.assetLocation("resources");
//			scopeAssetContainers.add(blade);
//		} else {
//			themedNodeResources = bundlableNode.assetLocation("resources");
//		}
//		
//		List<String> validThemeNames = new ArrayList<>();
//		if (themedNodeResources != null) {
//			for ( ThemesAssetLocation theme : ((ResourcesAssetLocation) themedNodeResources).themes() ) {
//				validThemeNames.add(theme.getThemeName());
//			}
//		}
//		
//		for(AssetContainer assetContainer : scopeAssetContainers) {
//			ResourcesAssetLocation resourceAssetLocation = (ResourcesAssetLocation) assetContainer.assetLocation("resources");
//			
//			if(resourceAssetLocation != null) {
//				for(ThemesAssetLocation themeAssetLocation : resourceAssetLocation.themes()) {
//					String themeName = themeAssetLocation.getThemeName();
//					
//					if (!themeName.equals("common") && validThemeNames.contains(themeName) ) {
//						themeNames.add(themeName);
//					}
//					
//				}
//			}
//		}
//		
//		List<String> themeNamesList = new ArrayList<>();
//		themeNamesList.add("common");
//		themeNamesList.addAll(themeNames);
//		
//		return themeNamesList;
//	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public List<String> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<String> assetLocationDirectories = new ArrayList<>();
		
		assetLocationDirectories.add(".");
		assetLocationDirectories.add("resources");
		assetLocationDirectories.add("src");
		assetLocationDirectories.add("src-test");
		addThemeDirectories(assetLocationDirectories, assetContainer);
		
		File sourceDir = assetContainer.file("src");
		if(sourceDir.exists()) {
			for(File dir : brjs.getFileInfo(sourceDir).nestedDirs()) {
				assetLocationDirectories.add(RelativePathUtility.get(assetContainer.dir(), dir));
			}
		}
		
		File sourceTestDir = assetContainer.file("src-test");
		if(sourceTestDir.exists()) {
			for(File dir : brjs.getFileInfo(sourceTestDir).nestedDirs()) {
				assetLocationDirectories.add(RelativePathUtility.get(assetContainer.dir(), dir));
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
			String relativePath = RelativePathUtility.get(assetContainer.dir(), themeDir);
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
				assetLocation = new BRJSConformantRootAssetLocation(assetContainer.root(), assetContainer, dir);
				break;
			
			case "resources":
				if (assetContainer instanceof Blade) {
					assetLocation = new BladeResourcesAssetLocation(assetContainer.root(), assetContainer, dir);
				} else {
					assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir);
				}
				break;
			
			case "src":
				assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("resources"));
				break;
			
			case "src-test":
				assetLocation = new TestSourceAssetLocation(assetContainer.root(), assetContainer, dir);
				break;
			
			default:
				if(dirPath.startsWith("themes")){
					
					String themeName = dirPath.substring(7);
					assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir, themeName);
					break;
				}
				
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
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
}
