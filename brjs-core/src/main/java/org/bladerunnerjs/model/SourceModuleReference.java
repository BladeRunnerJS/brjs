package org.bladerunnerjs.model;

public class SourceModuleReference {
	private final String requirePath;
	private final String assetPath;
	
	public SourceModuleReference(SourceModule sourceModule) {
		requirePath = sourceModule.getRequirePath();
		assetPath = sourceModule.getAssetPath();
	}
	
	public String getRequirePath() {
		return requirePath;
	}
	
	public Object getAssetPath() {
		return assetPath;
	}
}
