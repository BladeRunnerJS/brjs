package org.bladerunnerjs.model;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.utility.RequirePathUtility;
import org.bladerunnerjs.utility.reader.JsCommentStrippingReader;

public class IndexPageAsset extends LinkedFileAsset {

	public IndexPageAsset(MemoizedFile assetFile, AssetContainer assetContainer, String requirePrefix, List<Asset> implicitDependencies) {
		super(assetFile, assetContainer, requirePrefix, implicitDependencies);
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {		
		List<Asset> assetList = super.getDependentAssets(bundlableNode);
		
		Set<String> dependencies = new LinkedHashSet<String>();
		List<String> aliases = new ArrayList<>();
		try {
			RequirePathUtility.addRequirePathsFromReader(getReader(), dependencies, aliases);
		} catch (IOException ex) {
			throw new RuntimeException(ex);
		}
		List<String> dependenciesList = new ArrayList<String>(dependencies);
		try {
			assetList.addAll(bundlableNode.assets(this, dependenciesList));
		} catch (RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		Asset rootAsset = assetContainer().asset(assetContainer().requirePrefix());
		if (rootAsset != null) {
			assetList.add(rootAsset);			
		}
		
		return assetList;
	}
	
	@Override
	public Reader getReader() throws IOException {
		return new JsCommentStrippingReader(assetContainer().root(), super.getReader(), false);
	}
}
