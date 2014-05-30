package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public final class ThemesAssetLocation extends AbstractDeepAssetLocation {
	private final MemoizedValue<List<String>> themesList = new MemoizedValue<>("ThemeAssetLocation.themes", root(), dir());
	
	public ThemesAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	public String getThemeName() {
		return dir().getName();
	}
	
	public List<String> themes() {
		return themesList.value(() -> {
			List<String> themes = new ArrayList<>();
			
			if(dir().exists()) {
				for(File dir : root().getFileInfo(dir()).dirs()) {
					themes.add(dir.getName());
				}
			}
			
			return themes;
		});
		
	}
}
