package org.bladerunnerjs.utility.trie;

import org.bladerunnerjs.model.LinkedAsset;

public class LinkedAssetReference implements AssetReference {
	
	private final LinkedAsset asset;
	
	public LinkedAssetReference(LinkedAsset asset) {
		this.asset = asset;
	}
	
	public String getAssetPath() {
		return asset.getAssetPath();
	}
	
	public String getRequirePath() {
		return asset.getPrimaryRequirePath();
	}
}
