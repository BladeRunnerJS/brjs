package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.FastDirectoryFileFilter;

public class DeepAssetLocation extends ShallowAssetLocation {
	public DeepAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public <A extends Asset> A obtainAsset(Class<? extends A> assetClass, File dir, String assetName) throws AssetFileInstantationException {
		return assetLocator.obtainAsset(assetClass, dir, assetName);
	}
	
	@Override
	public <A extends Asset> List<A> obtainMatchingAssets(AssetFilter assetFilter, Class<A> assetListClass, Class<? extends A> assetClass) throws AssetFileInstantationException {
		List<A> assets = new ArrayList<>();
		
		if (FastDirectoryFileFilter.isDirectory(dir)) {
			addAllMatchingAssets(dir, assetFilter, assetClass, assets);
		}
		
		return assets;
	}
	
	private <A extends Asset> void addAllMatchingAssets(File dir, AssetFilter assetFilter, Class<? extends A> assetClass, List<A> assets) throws AssetFileInstantationException {
		addMatchingAssets(dir, assetFilter, assetClass, assets);
		
		for(File childDir : root().getFileIterator(dir).dirs()) {
			addAllMatchingAssets(childDir, assetFilter, assetClass, assets);
		}
	}
}