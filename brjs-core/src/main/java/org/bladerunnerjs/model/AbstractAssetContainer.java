package org.bladerunnerjs.model;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRelativeRequirePathException;
import org.bladerunnerjs.api.plugin.AssetRegistry;
import org.bladerunnerjs.api.plugin.DefaultAssetRegistry;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	
	private final MemoizedValue<AssetRegistry> assetDiscoveryResult;
	
	public AbstractAssetContainer(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
		assetDiscoveryResult = new MemoizedValue<>("AssetContainerAssets.assetDiscoveryResult", this);
	}
	
	@Override
	public App app() {
		Node node = this.parentNode();
		
		while(!(node instanceof App) && node != null) {
			node = node.parentNode();
		}
		
		if (node == null) {
			AssetContainer assetContainer = root().locateAncestorNodeOfClass(dir().getParentFile(), AssetContainer.class);
			if (assetContainer != null) {
				return assetContainer.app();				
			}
		}
		
		return (App) node;
	}
	
	@Override
	public Set<Asset> assets() {
		return assetDiscoveryResult().getRegisteredAssets();
	}
	
	@Override
	public Asset asset(String requirePath) {
		return assetDiscoveryResult().getRegisteredAsset(requirePath);
	}
	
	@Override
	public String canonicaliseRequirePath(Asset asset, String requirePath) throws RequirePathException
	{
		String requirePrefix = StringUtils.substringBeforeLast(asset.getPrimaryRequirePath(), "/");
		
		List<String> requirePrefixParts = new LinkedList<String>( Arrays.asList(requirePrefix.split("/")) );
		List<String> requirePathParts = new LinkedList<String>( Arrays.asList(requirePath.split("/")) );
		
		if(!requirePath.contains("../") && !requirePath.contains("./")) {
			return requirePath;
		}
		
		Iterator<String> requirePathPartsIterator = requirePathParts.iterator();
		while(requirePathPartsIterator.hasNext()) {
			String pathPart = requirePathPartsIterator.next();
			
			switch (pathPart) {
				case ".":
					requirePathPartsIterator.remove();
					break;
				
				case "..":
					requirePathPartsIterator.remove();
					if (requirePrefixParts.size() > 0)
					{
						requirePrefixParts.remove( requirePrefixParts.size()-1 );						
					}
					else
					{
						String msg = String.format("Unable to continue up to parent require path, no more parents remaining. Require path of container was '%s', relative require path was '%s'", requirePrefix, requirePath);
						throw new UnresolvableRelativeRequirePathException(msg);
					}
					break;
				
				default:
					break;
			}
		}
		
		return StringUtils.join(requirePrefixParts, "/") + "/" + StringUtils.join(requirePathParts, "/");
	}
	
	protected AssetRegistry assetDiscoveryResult() {
		return assetDiscoveryResult.value(() -> {
			return new DefaultAssetRegistry(this);
		});
	}
	
}
