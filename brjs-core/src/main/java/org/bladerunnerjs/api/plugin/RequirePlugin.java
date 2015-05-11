package org.bladerunnerjs.api.plugin;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.model.exception.RequirePathException;

/**
 * An interface that stands as the basis of all plugins that require an entity e.g. {@link ServiceRequirePlugin}, {@link AliasDataRequirePlugin}.
 */


public interface RequirePlugin extends Plugin {
	
	/**
	 * The method returns the name of the current plugin being used e.g. 'alias'.
	 * 
	 * @return a String representing the name of the current plugin being used
	 */
	String getPluginName();
	
	/**
	 * The method returns the Asset retrieved using the selected requirePathSuffix from the bundable node.
	 * 
	 * @param bundableNode the {@link BundableNode} which hosts the sought {@link Asset}
	 * @param requirePathSuffix the suffix of the Asset's require path, used as its unique identifier within the BundableNode
	 * @return an Asset retrieved using the selected requirePathSuffix from the bundable node
	 */
	Asset getAsset(BundlableNode bundlableNode, String requirePathSuffix) throws RequirePathException;
}
