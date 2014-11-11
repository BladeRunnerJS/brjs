package org.bladerunnerjs.plugin;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;

/**
 * Asset plug-ins allow new implementations of {@link SourceModule}, {@link LinkedAsset} &amp; {@link Asset} to be supported within the model.
 */
public interface AssetPlugin extends OrderedPlugin {
	boolean canHandleAsset(MemoizedFile assetFile, AssetLocation assetLocation);
	Asset createAsset(MemoizedFile assetFile, AssetLocation assetLocation) throws AssetFileInstantationException;
}
