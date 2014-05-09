package org.bladerunnerjs.plugin;

import java.io.File;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;

/**
 * Asset plug-ins allow new implementations of {@link SourceModule}, {@link LinkedAsset} &amp; {@link Asset} to be supported within the model.
 */
public interface AssetPlugin extends OrderedPlugin {
	boolean canHandleAsset(File assetFile, AssetLocation assetLocation);
	Asset createAsset(File assetFile, AssetLocation assetLocation) throws AssetFileInstantationException;
}
