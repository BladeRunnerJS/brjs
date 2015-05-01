package org.bladerunnerjs.utility.trie;

import org.bladerunnerjs.api.Asset;

public class LinkedAssetReference implements AssetReference {
	
	private final Asset asset;
	
	public LinkedAssetReference(Asset asset) {
		this.asset = asset;
	}
	
	public String getAssetPath() {
		return asset.getAssetPath();
	}
	
	public String getRequirePath() {
		return asset.getPrimaryRequirePath();
	}
	
	public Class<? extends Asset> getAssetClass() {
		return asset.getClass();
	}
}
