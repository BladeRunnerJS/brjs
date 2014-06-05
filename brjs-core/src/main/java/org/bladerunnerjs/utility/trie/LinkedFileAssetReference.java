package org.bladerunnerjs.utility.trie;

import org.bladerunnerjs.model.LinkedFileAsset;

public class LinkedFileAssetReference implements AssetReference {
	
	private final LinkedFileAsset asset;
	
	public LinkedFileAssetReference(LinkedFileAsset asset) {
		this.asset = asset;
	}
	
	public String getAssetPath() {
		return asset.getAssetPath();
	}
	
	public String getRequirePath() {
		return asset.getAssetPath();
	}
}
