package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.model.exception.ModelOperationException;

public class DeepLinkedAsset implements LinkedAsset {
	private final LinkedAsset linkedAsset;
	private final DeepAssetLocation deepAssetLocation;
	
	public DeepLinkedAsset(LinkedAsset linkedAsset, DeepAssetLocation deepAssetLocation) {
		this.linkedAsset = linkedAsset;
		this.deepAssetLocation = deepAssetLocation;
	}
	
	public void initialize(AssetLocation assetLocation, File assetFileOrDir) throws AssetFileInstantationException {
		linkedAsset.initialize(assetLocation, assetFileOrDir);
	}
	
	public Reader getReader() throws FileNotFoundException {
		return linkedAsset.getReader();
	}
	
	public AssetLocation getAssetLocation() {
		return deepAssetLocation;
	}
	
	public String getAssetName() {
		return linkedAsset.getAssetName();
	}
	
	public String getAssetPath() {
		return linkedAsset.getAssetPath();
	}
	
	public List<SourceModule> getDependentSourceModules(BundlableNode bundlableNode) throws ModelOperationException {
		return linkedAsset.getDependentSourceModules(bundlableNode);
	}
	
	public File getUnderlyingFile() {
		return linkedAsset.getUnderlyingFile();
	}
	
	public List<String> getAliasNames() throws ModelOperationException {
		return linkedAsset.getAliasNames();
	}
}
