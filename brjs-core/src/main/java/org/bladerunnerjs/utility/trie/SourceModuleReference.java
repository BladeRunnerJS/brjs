package org.bladerunnerjs.utility.trie;

import org.bladerunnerjs.model.SourceModule;

public class SourceModuleReference implements AssetReference {
	
	private final SourceModule sourceModule;
	
	public SourceModuleReference(SourceModule sourceModule) {
		this.sourceModule = sourceModule;
	}
	
	public String getAssetPath() {
		return sourceModule.getAssetPath();
	}
	
	public String getRequirePath() {
		return sourceModule.getRequirePath();
	}
}
