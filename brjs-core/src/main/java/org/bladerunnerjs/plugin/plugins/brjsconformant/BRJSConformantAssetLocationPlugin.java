package org.bladerunnerjs.plugin.plugins.brjsconformant;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ChildSourceAssetLocation;
import org.bladerunnerjs.model.ChildTestSourceAssetLocation;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.SourceAssetLocation;
import org.bladerunnerjs.model.TestSourceAssetLocation;
import org.bladerunnerjs.model.ThemesAssetLocation;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.WorkbenchResourcesAssetLocation;
import org.bladerunnerjs.plugin.base.AbstractAssetLocationPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

public class BRJSConformantAssetLocationPlugin extends AbstractAssetLocationPlugin {
	
	private BRJS brjs;

	public static List<String> getBundlableNodeThemes(BundlableNode bundlableNode) {
		Set<String> themeNames = new HashSet<>();
		
		for(AssetContainer assetContainer : bundlableNode.assetContainers()) {
			ResourcesAssetLocation resourceAssetLocation = (ResourcesAssetLocation) assetContainer.assetLocation("resources");
			
			if(resourceAssetLocation != null) {
				for(ThemesAssetLocation themeAssetLocation : resourceAssetLocation.themes()) {
					String themeName = themeAssetLocation.getThemeName();
					
					if(!themeName.equals("common")) {
						themeNames.add(themeName);
					}
					
				}
			}
		}
		
		List<String> themeNamesList = new ArrayList<>();
		themeNamesList.add("common");
		themeNamesList.addAll(themeNames);
		
		return themeNamesList;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return new ArrayList<>();
	}
	
	public List<File> getAssetLocationDirectories(AssetContainer assetContainer) {
		List<File> assetLocationDirectories = new ArrayList<>();
		File sourceDir = assetContainer.file("src");
		File sourceTestDir = assetContainer.file("src-test");
		
		assetLocationDirectories.add(assetContainer.dir());
		assetLocationDirectories.add(assetContainer.file("resources"));
		assetLocationDirectories.add(sourceDir);
		assetLocationDirectories.add(sourceTestDir);
		
		if(sourceDir.exists()) {
			assetLocationDirectories.addAll(brjs.getFileInfo(sourceDir).nestedDirs());
		}
		
		if(sourceTestDir.exists()) {
			assetLocationDirectories.addAll(brjs.getFileInfo(sourceTestDir).nestedDirs());
		}
		
		return assetLocationDirectories;
	}
	
	public AssetLocation createAssetLocation(AssetContainer assetContainer, File dir, Map<String, AssetLocation> assetLocationsMap) {
		AssetLocation assetLocation;
		String dirPath = dir.getPath();
		
		if(dirPath.equals(assetContainer.dir().getPath())) {
			assetLocation = new BRJSConformantRootAssetLocation(assetContainer.root(), assetContainer, dir);
		}
		else if(dirPath.equals(assetContainer.file("resources").getPath())) {
			if (assetContainer instanceof Workbench) {
				assetLocation = new WorkbenchResourcesAssetLocation(assetContainer.root(), assetContainer, dir);
			}
			else {
				assetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, dir);
			}
		}
		else if(dirPath.equals(assetContainer.file("src").getPath())) {
			assetLocation = new SourceAssetLocation(assetContainer.root(), assetContainer, dir, assetLocationsMap.get("resources"));
		}
		else if(dirPath.equals(assetContainer.file("src-test").getPath())) {
			assetLocation = new TestSourceAssetLocation(assetContainer.root(), assetContainer, dir);
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
	public boolean canHandleAssetContainer(AssetContainer assetContainer) {
		return true;
	}
	
	@Override
	public List<AssetLocation> getAssetLocations(AssetContainer assetContainer, Map<String, AssetLocation> assetLocationCache)  
	{
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		if (!assetLocationCache.containsKey("root")) {
			assetLocationCache.put("root", new BRJSConformantRootAssetLocation(assetContainer.root(), assetContainer, assetContainer.dir()));
			
			AssetLocation resourcesAssetLocation;
			if (assetContainer instanceof Workbench)
			{
				resourcesAssetLocation = new WorkbenchResourcesAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources"));
			}
			else
			{
				resourcesAssetLocation = new ResourcesAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("resources"));				
			}
			assetLocationCache.put( "resources",  resourcesAssetLocation);
			assetLocationCache.put( "src", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src"), assetLocationCache.get("resources")) );
			assetLocationCache.put( "src-test", new SourceAssetLocation(assetContainer.root(), assetContainer, assetContainer.file("src-test")) );
		}
		
		assetLocations.add(assetLocationCache.get("root"));
		assetLocations.add(assetLocationCache.get("resources"));
		SourceAssetLocation srcAssetLocation = (SourceAssetLocation) assetLocationCache.get("src");
		assetLocations.add(srcAssetLocation);
		assetLocations.addAll(srcAssetLocation.getChildAssetLocations());
		SourceAssetLocation srcTestAssetLocation = (SourceAssetLocation) assetLocationCache.get("src-test");
		assetLocations.add(srcTestAssetLocation);
		assetLocations.addAll( srcTestAssetLocation.getChildAssetLocations() ) ;
    	
		return assetLocations;
	}
	
	@Override
	public boolean allowFurtherProcessing() {
		return false;
	}
	
	// TODO: do we still need this?
	protected String normalizePath(String path) {
		return path.replaceAll("/$", "");
	}
}
