package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractChildSourceAssetLocation extends AbstractShallowAssetLocation {
	public AbstractChildSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, File dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
		super(rootNode, assetContainer, dir, parentAssetLocation, dependentAssetLocations);
	}
	
	@Override 
	public String requirePrefix() {
		String containerRequirePrefix = getAssumedRequirePath().substring(0, Math.min(getAssumedRequirePath().length(), assetContainer().requirePrefix().length()));
		boolean usesRepeatedDirectoryStructure = assetContainer().requirePrefix().startsWith(containerRequirePrefix);
		
		boolean isInsideJsLibrary = root().locateAncestorNodeOfClass(this, JsLib.class) != null;
		boolean isNamespaceDisabledLibrary = isInsideJsLibrary && !assetContainer().isNamespaceEnforced();
		
		return (!usesRepeatedDirectoryStructure && !isNamespaceDisabledLibrary) ? assetContainer().requirePrefix() + "/" + getAssumedRequirePath() : getAssumedRequirePath();
	}
	
	protected String getAssumedRequirePath() {
		return (!(parentAssetLocation() instanceof AbstractChildSourceAssetLocation)) ? dir.getName() :
			((AbstractChildSourceAssetLocation) parentAssetLocation()).getAssumedRequirePath() + "/" + dir().getName();
	}
}
