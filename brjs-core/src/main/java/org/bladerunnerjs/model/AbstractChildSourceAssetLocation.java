package org.bladerunnerjs.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.RelativePathUtility;

public abstract class AbstractChildSourceAssetLocation extends AbstractShallowAssetLocation {
	
	private String locationRequirePrefix;
	private AssetContainer assetContainer;
	
	public AbstractChildSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir, dependentAssetLocations);
		assetContainer = (AssetContainer) parent;
		// take the relative path from the asset container and then strip off the first dir - do it this way so it isn't tied to specific subdir of asset container (e.g. the src dir)
		locationRequirePrefix = RelativePathUtility.get(root(), assetContainer().dir(), dir()).replaceAll("/$", "");
		locationRequirePrefix = StringUtils.substringAfter(locationRequirePrefix, "/");
		
	}
	
	@Override 
	public String requirePrefix() {
		//TODO; parent assetLocation and assetContainer to calculate the requirePrefix
		assetContainer.requirePrefix(); // call requirePrefix() on the assetContainer so it can perform validation on it's require prefix
		
		return locationRequirePrefix;
	}
}
