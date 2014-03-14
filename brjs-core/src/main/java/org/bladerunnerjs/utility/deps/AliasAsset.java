package org.bladerunnerjs.utility.deps;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.model.AssetFileInstantationException;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ModelOperationException;

public class AliasAsset implements LinkedAsset {
	private final AliasDefinition alias;
	
	public AliasAsset(AliasDefinition alias) {
		this.alias = alias;
	}
	
	@Override
	public void initialize(AssetLocation assetLocation, File dir, String assetName) throws AssetFileInstantationException {
		// do nothing
	}
	
	@Override
	public Reader getReader() throws IOException {
		return null;
	}
	
	@Override
	public AssetLocation getAssetLocation() {
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
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return null;
	}
	
	@Override
	public List<String> getAliasNames() throws ModelOperationException {
		return null;
	}
}
