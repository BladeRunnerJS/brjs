package org.bladerunnerjs.api;

import java.util.List;

import org.bladerunnerjs.model.BundlableNode;

public interface BundleSet {
	public BundlableNode getBundlableNode();
	public List<Asset> getAssets();
	public List<Asset> getAssetsWithRequirePrefix(String... prefixes);
	public List<SourceModule> getSourceModules();
}
