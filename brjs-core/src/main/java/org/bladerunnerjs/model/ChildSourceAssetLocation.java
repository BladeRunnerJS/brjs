package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.model.exception.RequirePathException;

public class ChildSourceAssetLocation extends ShallowAssetLocation {
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	
	public ChildSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation parentAssetLocation) {
		super(rootNode, parent, dir);
		dependentAssetLocations.add(parentAssetLocation);
	}
	
	@Override
	public String requirePrefix() throws RequirePathException {
		String containerRequirePrefix = assetContainer.requirePrefix();
		String locationRequirePrefix = assetContainer.file("src").toURI().relativize(dir().toURI()).getPath().replaceAll("/$", "");
		
		if(!locationRequirePrefix.startsWith(containerRequirePrefix)) {
			throw new InvalidRequirePathException("Source module containing directory '" + locationRequirePrefix + "' does not start with correct require prefix '" + containerRequirePrefix + "'.");
		}
		
		return assetContainer.requirePrefix() + "/" + locationRequirePrefix;
	}
	
	@Override
	public List<AssetLocation> getDependentAssetLocations() {
		return dependentAssetLocations;
	}
}
