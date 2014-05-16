package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedValue;

public abstract class AbstractResourcesAssetLocation extends AbstractDeepAssetLocation {
	private final File themesDir;
	private final FileInfo themesDirInfo;
	private final MemoizedValue<Map<String, ThemesAssetLocation>> themesMap;
	private final MemoizedValue<List<AssetLocation>> assetLocationsList;
	private Map<String, ThemesAssetLocation> themeAssetLocations = null;
	
	public AbstractResourcesAssetLocation(BRJS root, AssetContainer assetContainer, File file) {
		super(root, assetContainer, file);
		
		themesDir = assetContainer.file("themes");
		themesDirInfo = root().getFileInfo(themesDir);
		themesMap = new MemoizedValue<>("ResourcesAssetLocation.themes", root(), themesDir);
		assetLocationsList = new MemoizedValue<>("ResourcesAssetLocation.assetLocations", root(), themesDir);
	}
	
	@Override
	public String requirePrefix() {
		return assetContainer.requirePrefix();
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations()
	{
		return assetLocationsList.value(() -> {
			return new ArrayList<>(themes());
		});
	}
	
	public List<ThemesAssetLocation> themes() {
		return new ArrayList<>(themesMap().values());
	}
	
	public ThemesAssetLocation theme(String themeName) {
		return themesMap().get(themeName);
	}
	
	private Map<String, ThemesAssetLocation> themesMap() {
		return themesMap.value(() -> {
			Map<String, ThemesAssetLocation> previousThemeAssetLocations = themeAssetLocations;
			themeAssetLocations = new LinkedHashMap<>();
			
			if(themesDirInfo.exists()) {
				for(File themeDir : themesDirInfo.dirs()) {
					String themeName = themeDir.getName();
					ThemesAssetLocation themeAssetLocation = ((previousThemeAssetLocations != null) && previousThemeAssetLocations.containsKey(themeName)) ?
						previousThemeAssetLocations.get(themeName) : new ThemesAssetLocation(root(), assetContainer, new File(themesDir, themeName));
					themeAssetLocations.put(themeName, themeAssetLocation);
				}
			}
			
			return themeAssetLocations;
		});
	}
}
