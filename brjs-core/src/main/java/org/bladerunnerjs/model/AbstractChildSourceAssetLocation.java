package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.InvalidRequirePathException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.RelativePathUtility;

//TODO: why was this 'final'?
public abstract class AbstractChildSourceAssetLocation extends AbstractShallowAssetLocation {
	private List<AssetLocation> dependentAssetLocations = new ArrayList<>();
	
	private final MemoizedValue<String> requirePrefix = new MemoizedValue<>("AssetLocation.requirePrefix", root(), dir(), root().libsDir(), assetContainer.app().file("app.conf"), root().conf().file("bladerunner.conf"));
	
	public AbstractChildSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation parentAssetLocation) {
		super(rootNode, parent, dir);
		dependentAssetLocations.add(parentAssetLocation);
		
		// TODO: understand why removing this line doesn't break any tests
		registerInitializedNode();
	}
	
	@Override
	public String requirePrefix() throws RequirePathException {
		return requirePrefix.value(() -> {
			String containerRequirePrefix = assetContainer.requirePrefix();
			
			// take the relative path from the asset container and then strip off the first dir - do it this way so it isn't tied to specific subdir of asset container (e.g. the src dir)
			String locationRequirePrefix = RelativePathUtility.get(assetContainer.dir(), dir()).replaceAll("/$", "");
			locationRequirePrefix = StringUtils.substringAfter(locationRequirePrefix, "/");
			
			if (assetContainer.isNamespaceEnforced() && !locationRequirePrefix.startsWith(containerRequirePrefix)) {
				// TODO: use dir().getPath() instead of locationRequirePrefix for a clearer error message
				throw new InvalidRequirePathException("Source module containing directory '" + locationRequirePrefix + "' does not start with correct require prefix '" + containerRequirePrefix + "'.");
			}
			
			return locationRequirePrefix;
		});
	}
	
	@Override
	public List<AssetLocation> dependentAssetLocations() {
		return dependentAssetLocations;
	}
}
