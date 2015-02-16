package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;

public class AliasAsset implements LinkedAsset {
	private final AliasDefinition alias;
	private AssetContainer assetContainer;
	
	public AliasAsset(AssetContainer assetContainer, AliasDefinition alias) {
		this.alias = alias;
		this.assetContainer = assetContainer;
	}
	
	@Override
	public Reader getReader() throws IOException {
		return null;
	}
	
	@Override
	public MemoizedFile file() {
		return null;
	}
	
	@Override
	public String getAssetName() {
		return alias.getName();
	}
	
	@Override
	public String getAssetPath() {
		return "alias!" + alias.getName();
	}
	
	@Override
	public List<Asset> getDependentAssets(BundlableNode bundlableNode) throws ModelOperationException {
		return null;
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return null;
	}
	
	@Override
	public List<String> getRequirePaths() {
		return Collections.emptyList();
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return null;
	}

	@Override
	public AssetContainer assetContainer()
	{
		return assetContainer;
	}
}
