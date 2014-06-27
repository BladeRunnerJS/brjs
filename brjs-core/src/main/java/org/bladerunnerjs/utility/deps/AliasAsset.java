package org.bladerunnerjs.utility.deps;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.utility.PrimaryRequirePathUtility;

public class AliasAsset implements LinkedAsset {
	private final AliasDefinition alias;
	
	private final List<String> emptyRequirePaths = new ArrayList<String>();
	
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
	public File dir() {
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
		return emptyRequirePaths;
	}
	
	@Override
	public String getPrimaryRequirePath() {
		return PrimaryRequirePathUtility.getPrimaryRequirePath(this);
	}
}
