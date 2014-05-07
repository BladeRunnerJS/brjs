package org.bladerunnerjs.plugin.base;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link AssetPlugin}.
 */
public abstract class AbstractAssetLocationPlugin extends AbstractPlugin implements AssetLocationPlugin {
	// TODO: remove this class
	public List<File> getSeedAssetLocationDirectories(AssetContainer assetContainer) {
		return new ArrayList<>();
	}
}
