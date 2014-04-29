package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.filemodification.FileModifiedChecker;
import org.bladerunnerjs.utility.filemodification.InfoFileModifiedChecker;

public class ResourcesAssetLocation extends DeepAssetLocation {
	private final FileModifiedChecker fileModifiedChecker;
	private final File themesDir;
	private Map<String, ThemeAssetLocation> themeAssetLocations = null;
	
	public ResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file) {
		super(root, assetContainer, file);
		
		themesDir = assetContainer.file("themes");
		fileModifiedChecker = new InfoFileModifiedChecker(root().getFileInfo(themesDir));
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations()
	{
		recalcuateDependencies();
		
		List<AssetLocation> assetLocations = new ArrayList<>();
		assetLocations.addAll(themeAssetLocations.values());
		return assetLocations;
	}
	
	public List<ThemeAssetLocation> themes() {
		recalcuateDependencies();
		
		List<ThemeAssetLocation> themes = new ArrayList<>();
		themes.addAll(themeAssetLocations.values());
		return themes;
	}
	
	public ThemeAssetLocation theme(String themeName) {
		recalcuateDependencies();
		
		return themeAssetLocations.get(themeName);
	}
	
	@Override
	public String requirePrefix() throws RequirePathException {
		return assetContainer.requirePrefix();
	}
	
	// TODO: this is the first time that we have a plug-in created asset-location producing more asset-locations dynamically, and there is currently no mechanism to help
	// with the caching issues within the model
	private void recalcuateDependencies() {
		if(fileModifiedChecker.hasChangedSinceLastCheck() || (themeAssetLocations == null)) {
			Map<String, ThemeAssetLocation> previousThemeAssetLocations = themeAssetLocations;
			themeAssetLocations = new HashMap<>();
			FileInfo themeDirInfo = root().getFileInfo(themesDir);
			
			if(themeDirInfo.exists()) {
				for(File themeDir : themeDirInfo.dirs()) {
					String themeName = themeDir.getName();
					ThemeAssetLocation themeAssetLocation = ((previousThemeAssetLocations != null) && previousThemeAssetLocations.containsKey(themeName)) ?
						previousThemeAssetLocations.get(themeName) : new ThemeAssetLocation(root(), assetContainer, new File(themesDir, themeName));
					themeAssetLocations.put(themeName, themeAssetLocation);
				}
			}
		}
	}
}
