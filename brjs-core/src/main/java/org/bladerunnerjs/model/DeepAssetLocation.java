package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class DeepAssetLocation extends ShallowAssetLocation {
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public <A extends Asset> A obtainAsset(File assetFileOrDir, Class<? extends A> assetClass) throws AssetFileInstantationException {
		// TODO: we will need some different logic once we start taking a logical assetPath, since we won't be preventing deep file paths
		return super.obtainAsset(assetFileOrDir, assetClass);
	}
	
	@Override
	public <A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetListClass, Class<? extends A> assetClass) throws AssetFileInstantationException {
		List<A> assets = new ArrayList<>();
		
		if (dir.isDirectory()) {
			addAllMatchingAssets(dir, assetFilter, assetClass, assets);
		}
		
		return assets;
	}
	
	private <A extends Asset> void addAllMatchingAssets(File dir, AssetFilter assetFilter, Class<? extends A> assetClass, List<A> assets) throws AssetFileInstantationException {
		addMatchingAssets(dir, assetFilter, assetClass, assets);
		
		for(File file : root().getFileIterator(dir).dirs()) {
			addAllMatchingAssets(file, assetFilter, assetClass, assets);
		}
	}
}