package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.TestAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public class TestNamespacedJsSourceModule extends NamespacedJsSourceModule implements TestAsset
{

	public TestNamespacedJsSourceModule(AssetContainer assetContainer, String requirePrefix, MemoizedFile jsFile, List<Asset> implicitDependencies)
	{
		super(assetContainer, requirePrefix, jsFile, implicitDependencies);
	}

}
