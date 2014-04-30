package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetFilter;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class XMLAssetPlugin extends AbstractAssetPlugin {
	private AssetFilter xmlFilesFilter = new XMLAssetFilter();
	private final List<SourceModule> emptySourceModules = new ArrayList<>();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<SourceModule> getSourceModules(AssetLocation assetLocation) {
		return emptySourceModules;
	}
	
	@Override
	public List<LinkedAsset> getLinkedAssets(AssetLocation assetLocation) {
		List<LinkedAsset> assets;
		try {
			assets = assetLocation.obtainMatchingAssets(xmlFilesFilter, LinkedAsset.class, FullyQualifiedLinkedAsset.class);
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> result = new ArrayList<Asset>();
		result.addAll(this.getLinkedAssets(assetLocation));
		return result;
	}
	
	private class XMLAssetFilter implements AssetFilter {
		@Override
		public boolean accept(String assetName) {
			return (assetName.endsWith(".xml") && !assetName.equals("aliases.xml")  && !assetName.equals("aliasDefinitions.xml"));
		}
	}
}
