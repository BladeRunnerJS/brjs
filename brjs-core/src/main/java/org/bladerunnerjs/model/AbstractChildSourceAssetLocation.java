package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractChildSourceAssetLocation extends AbstractShallowAssetLocation {
	public AbstractChildSourceAssetLocation(RootNode rootNode, AssetContainer assetContainer, MemoizedFile dir, AssetLocation parentAssetLocation, AssetLocation... dependentAssetLocations) {
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
		File dir = rootNode.getMemoizedFile(dir());
		return (!(parentAssetLocation() instanceof AbstractChildSourceAssetLocation)) ? dir.getName() :
			((AbstractChildSourceAssetLocation) parentAssetLocation()).getAssumedRequirePath() + "/" + dir().getName();
	}
}
