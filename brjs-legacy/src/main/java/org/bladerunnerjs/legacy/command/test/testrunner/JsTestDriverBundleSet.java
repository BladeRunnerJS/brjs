package org.bladerunnerjs.legacy.command.test.testrunner;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.BundlableNode;

public class JsTestDriverBundleSet implements BundleSet {
	private BundleSet bundleSet;
	
	public JsTestDriverBundleSet(BundleSet bundleSet) {
		this.bundleSet = bundleSet;
	}
	
	public BundlableNode getBundlableNode() {
		return new JsTestDriverBundlableNode(bundleSet.getBundlableNode());
	}
	
	@Override
	public List<Asset> getAssets(String... prefixes)
	{
		return bundleSet.getAssets(prefixes);
	}
	
	@Override
	public List<Asset> getAssets(List<String> prefixes, List<Class<? extends Asset>> assetTypes)
	{
		return bundleSet.getAssets(prefixes, assetTypes);
	}
	
	@Override
	public List<SourceModule> getSourceModules(String... prefixes) {
		return bundleSet.getSourceModules();
	}

	@Override
	public List<LinkedAsset> seedAssets()
	{
		return bundleSet.seedAssets();
	}

	@Override
	public List<LinkedAsset> getLinkedAssets()
	{
		return bundleSet.getLinkedAssets();
	}

}
