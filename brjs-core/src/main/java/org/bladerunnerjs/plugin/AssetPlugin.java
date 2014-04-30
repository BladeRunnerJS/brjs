package org.bladerunnerjs.plugin;

import java.util.List;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;

/**
 * Asset plug-ins allow new implementations of {@link SourceModule}, {@link LinkedAsset} &amp; {@link Asset} to be supported within the model.
 */
public interface AssetPlugin extends Plugin {
	/**
	 * Return a list of all assets discovered within the given {@link AssetLocation}.
	 * 
	 * @param assetLocation The asset location to search within.
	 * @return A list of all assets discovered at this location, or an empty list if none were discovered.
	 */
	List<Asset> getAssets(AssetLocation assetLocation);
}
