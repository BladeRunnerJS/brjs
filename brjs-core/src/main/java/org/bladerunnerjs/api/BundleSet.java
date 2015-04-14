package org.bladerunnerjs.api;

import java.util.List;

public interface BundleSet {
	public BundlableNode getBundlableNode();
	public List<LinkedAsset> seedAssets();
	public List<LinkedAsset> getLinkedAssets();
	public List<Asset> getAssets(String... prefixes);
	public <AT extends Asset> List<AT> getAssets(List<String> prefixes, List<Class<? extends AT>> assetTypes);
	public List<SourceModule> getSourceModules();
	public <AT extends SourceModule> List<AT> getSourceModules(List<Class<? extends AT>> assetTypes);
}
