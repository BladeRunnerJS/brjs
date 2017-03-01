package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.TestAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;


public class TestCommonJsSourceModule extends DefaultCommonJsSourceModule implements TestAsset
{

	public TestCommonJsSourceModule(AssetContainer assetContainer, String requirePrefix, MemoizedFile assetFile, List<Asset> implicitDependencies)
	{
		super(assetContainer, requirePrefix, assetFile, implicitDependencies);
	}

}
