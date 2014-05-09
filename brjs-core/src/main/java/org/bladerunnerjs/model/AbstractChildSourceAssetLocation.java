package org.bladerunnerjs.model;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.RelativePathUtility;

public abstract class AbstractChildSourceAssetLocation extends AbstractShallowAssetLocation {
	private final MemoizedValue<String> requirePrefix = new MemoizedValue<>("AssetLocation.requirePrefix", root(), assetContainer.dir(), root().libsDir(), assetContainer.app().file("app.conf"), root().conf().file("bladerunner.conf"));
	
	public AbstractChildSourceAssetLocation(RootNode rootNode, Node parent, File dir, AssetLocation... dependentAssetLocations) {
		super(rootNode, parent, dir, dependentAssetLocations);
	}
	
	@Override
	public String requirePrefix() {
		return requirePrefix.value(() -> {
			// take the relative path from the asset container and then strip off the first dir - do it this way so it isn't tied to specific subdir of asset container (e.g. the src dir)
			String locationRequirePrefix = RelativePathUtility.get(assetContainer.dir(), dir()).replaceAll("/$", "");
			locationRequirePrefix = StringUtils.substringAfter(locationRequirePrefix, "/");
			
			return locationRequirePrefix;
		});
	}
}
