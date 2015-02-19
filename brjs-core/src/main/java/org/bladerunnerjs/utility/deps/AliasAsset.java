package org.bladerunnerjs.utility.deps;

import java.io.IOException;
import java.io.Reader;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.utility.RequirePathUtility;

public class AliasAsset implements LinkedAsset {
	private final AliasDefinition alias;
	
	public AliasAsset(AliasDefinition alias) {
		this.alias = alias;
	}
	
	@Override
	public Reader getReader() throws IOException {
		return null;
	}
	
	@Override
	public AssetLocation assetLocation() {
		return null;
	}
	
	@Override
	public MemoizedFile dir() {
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
		return RequirePathUtility.getPrimaryRequirePath(this);
	}
}
