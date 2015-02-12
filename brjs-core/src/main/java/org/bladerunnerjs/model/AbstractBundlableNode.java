package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.RequirePlugin;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.BundleSetRequestHandler;

public abstract class AbstractBundlableNode extends AbstractAssetContainer implements BundlableNode {
	private final MemoizedValue<BundleSet> bundleSet = new MemoizedValue<>("BundlableNode.bundleSet", root(), root().dir());
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
	}
	
	@Override
	public List<LinkedAsset> seedAssets() {
		return assetDiscoveryInitiator.seedAssets();
	}
		
	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException {
		RequirePlugin requirePlugin;
		String requirePathSuffix;
		
		if(requirePath.contains("!")) {
			String[] parts = requirePath.split("!");
			String pluginName = parts[0];
			requirePathSuffix = parts[1];
			requirePlugin = root().plugins().requirePlugin(pluginName);
		}
		else {
			requirePlugin = root().plugins().requirePlugin("default");
			requirePathSuffix = requirePath;
		}
		
		return (LinkedAsset) requirePlugin.getAsset(this, requirePathSuffix);
	}
	
	@Override
	public BundleSet getBundleSet() throws ModelOperationException {
		return bundleSet.value(() -> {
			return BundleSetCreator.createBundleSet(this);
		});
	}
	
	@Override
	public ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException {
		try {
			return BundleSetRequestHandler.handle(this.getBundleSet(), logicalRequestPath, contentAccessor, version);
		}
		catch (ModelOperationException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	@Override
	public List<Asset> assets(AssetContainer assetContainer, List<String> requirePaths) throws RequirePathException {
		List<Asset> assets = new ArrayList<Asset>();
		
		for(String requirePath : requirePaths) {				
			String canonicalRequirePath = assetContainer.canonicaliseRequirePath(requirePath);
			assets.add(getLinkedAsset(canonicalRequirePath));
		}
		
		return assets;
	}
	
	
}
