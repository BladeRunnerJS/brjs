package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetFilter;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.plugin.base.AbstractAssetPlugin;

public class XMLAssetPlugin extends AbstractAssetPlugin {
	private AssetFilter xmlFilesFilter = new XMLAssetFilter();
	
	@Override
	public void setBRJS(BRJS brjs) {
	}
	
	@Override
	public List<Asset> getAssets(AssetLocation assetLocation) {
		List<Asset> assets;
		try {
			assets = assetLocation.obtainMatchingAssets(xmlFilesFilter, Asset.class, FullyQualifiedLinkedAsset.class);
		}
		catch (AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return assets;
	}
	
	private class XMLAssetFilter implements AssetFilter {
		@Override
		public boolean accept(String assetName) {
			return (assetName.endsWith(".xml") && !assetName.equals("aliases.xml")  && !assetName.equals("aliasDefinitions.xml"));
		}
	}
}
