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
	public <AT extends Asset> List<AT> getAssets(List<String> prefixes, Class<? extends AT> assetType)
	{
		return bundleSet.getAssets(prefixes, assetType);
	}
	
	@Override
	public List<Asset> getAssets(List<String> prefixes, List<Class<? extends Asset>> assetTypes)
	{
		return bundleSet.getAssets(prefixes, assetTypes);
	}
	
	@Override
	public List<SourceModule> getSourceModules() {
		return bundleSet.getSourceModules();
	}
	
	@Override
	public <AT extends SourceModule> List<AT> getSourceModules(Class<? extends AT> assetType) {
		return bundleSet.getSourceModules(assetType);
	}
	
	@Override
	public List<SourceModule> getSourceModules(List<Class<? extends SourceModule>> assetTypes) {
		return bundleSet.getSourceModules(assetTypes);
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
