package org.bladerunnerjs.plugin.bundlers.namespacedjs;

import org.bladerunnerjs.api.TestAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public class TestNamespacedJsSourceModule extends NamespacedJsSourceModule implements TestAsset
{

	public TestNamespacedJsSourceModule(AssetContainer assetContainer, String requirePrefix, MemoizedFile jsFile)
	{
		super(assetContainer, requirePrefix, jsFile);
	}

}
