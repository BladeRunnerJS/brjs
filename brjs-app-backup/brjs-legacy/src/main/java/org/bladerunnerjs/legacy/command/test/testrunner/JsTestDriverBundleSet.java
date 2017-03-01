package org.bladerunnerjs.legacy.command.test.testrunner;

import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.BundlableNode;

public class JsTestDriverBundleSet implements BundleSet {
	private BundleSet bundleSet;
	private JsTestDriverBundlableNode bundlableNode;
	
	public JsTestDriverBundleSet(JsTestDriverBundlableNode bundlableNode, BundleSet bundleSet) {
		this.bundlableNode = bundlableNode;
		this.bundleSet = bundleSet;
	}
	
	public BundlableNode bundlableNode() {
		return bundlableNode;
	}
	
	@Override
	public List<Asset> assets(String... prefixes)
	{
		return bundleSet.assets(prefixes);
	}
	
	@Override
	public <AT extends Asset> List<AT> assets(Class<? extends AT> assetType, String... prefixes)
	{
		return bundleSet.assets(assetType, prefixes);
	}
	
	@Override
	public List<Asset> assets(List<Class<? extends Asset>> assetTypes, String... prefixes)
	{
		return bundleSet.assets(assetTypes, prefixes);
	}
	
	@Override
	public List<SourceModule> sourceModules() {
		return bundleSet.sourceModules();
	}
	
	@Override
	public <AT extends SourceModule> List<AT> sourceModules(Class<? extends AT> assetType) {
		return bundleSet.sourceModules(assetType);
	}
	
	@Override
	public List<SourceModule> sourceModules(List<Class<? extends SourceModule>> assetTypes) {
		return bundleSet.sourceModules(assetTypes);
	}

	@Override
	public List<LinkedAsset> seedAssets()
	{
		return bundleSet.seedAssets();
	}

}
