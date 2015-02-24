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
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.UnresolvableRelativeRequirePathException;
import org.bladerunnerjs.api.plugin.AssetContainerAssets;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.api.model.exception.NamespaceException;

public abstract class AbstractAssetContainer extends AbstractBRJSNode implements AssetContainer {
	
	protected final AssetContainerAssets assetDiscoveryInitiator = new AssetContainerAssets(this);
	
	public AbstractAssetContainer(RootNode rootNode, Node parent, MemoizedFile dir) {
		super(rootNode, parent, dir);
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
		return assetDiscoveryInitiator.assets(); 
	}
	
	@Override
	public Asset asset(String requirePath) {
		return assetDiscoveryInitiator.assetsMap().get(requirePath);
	}
	
	@Override
	public String canonicaliseRequirePath(String requirePath) throws RequirePathException
	{
		String requirePrefix = requirePrefix();
		
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
	
}
