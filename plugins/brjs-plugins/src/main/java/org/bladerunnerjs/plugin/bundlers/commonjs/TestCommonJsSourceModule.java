package org.bladerunnerjs.plugin.bundlers.commonjs;

import org.bladerunnerjs.api.TestAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public class TestCommonJsSourceModule extends DefaultCommonJsSourceModule implements TestAsset
{

	public TestCommonJsSourceModule(AssetContainer assetContainer, String requirePrefix, MemoizedFile assetFile)
	{
		super(assetContainer, requirePrefix, assetFile);
	}

}
