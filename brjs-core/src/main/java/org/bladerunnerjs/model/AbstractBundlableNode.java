package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.Workbench;
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
	private RequirePlugin defaultRequirePlugin;
	
	public AbstractBundlableNode(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
		defaultRequirePlugin = root().plugins().requirePlugin("default");
	}
	
	@Override
	public List<LinkedAsset> seedAssets() {
		List<LinkedAsset> seedAssets = new ArrayList<>();
		for (AssetContainer scopeAssetContainer : scopeAssetContainers()) {
			if (scopeAssetContainer instanceof Aspect || scopeAssetContainer instanceof Bladeset 
						|| scopeAssetContainer instanceof Blade || scopeAssetContainer instanceof Workbench<?>) {
				Asset assetContainerRootAsset = scopeAssetContainer.asset(scopeAssetContainer.requirePrefix());
				if (assetContainerRootAsset instanceof LinkedAsset) {
					seedAssets.add( (LinkedAsset) assetContainerRootAsset );
				}
			}
		}
		seedAssets.addAll( assetDiscoveryInitiator.seedAssets() );
		return seedAssets;
	}
		
	@Override
	public LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException {
		LinkedAsset linkedAsset;
		RuntimeException noLinkedAssetException = null;
		RequirePlugin requirePlugin;
		String pluginName;
		String requirePathSuffix;
		
		if(requirePath.contains("!")) {
			pluginName = StringUtils.substringBefore(requirePath, "!");
			requirePathSuffix = StringUtils.substringAfter(requirePath, "!");
			requirePlugin = root().plugins().requirePlugin(pluginName);
		} else {
			requirePlugin = defaultRequirePlugin;
			pluginName = "default";
			requirePathSuffix = requirePath;
		}
		
		if (requirePlugin == null) {
			linkedAsset = (LinkedAsset) defaultRequirePlugin.getAsset(this, requirePath);
			noLinkedAssetException = new RuntimeException("Unable to find a require plugin for the prefix '"+pluginName+"' and there is no asset registered for the require path '"+requirePath+"'.");
		} else {
			linkedAsset = (LinkedAsset) requirePlugin.getAsset(this, requirePathSuffix);
			noLinkedAssetException = new RuntimeException("There is no asset registered for the require path '"+requirePathSuffix+"'.");
		}
		
		if (linkedAsset == null) {
			throw noLinkedAssetException;
		}
		
		return linkedAsset;
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
