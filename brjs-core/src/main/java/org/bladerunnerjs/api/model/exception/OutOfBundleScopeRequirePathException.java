package org.bladerunnerjs.api.model.exception;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;

public class OutOfBundleScopeRequirePathException extends RequirePathException {

	private static final long serialVersionUID = 1L;
	private String requirePath;
	private String scopedLocations;
	private Asset asset;
	private BundlableNode bundlableNode;
	
	public OutOfBundleScopeRequirePathException(BundlableNode bundlableNode, String requirePath, Asset asset) {
		this.bundlableNode = bundlableNode;
		this.requirePath = requirePath;
		this.asset = asset;
		scopedLocations = RequirePathExceptionUtils.getScopeLocationText(bundlableNode);
		
	}

	@Override
	public String getMessage() {
		return String.format("The asset with the require path '%s' was found at '%s', but it was not in one of the valid bundler scopes."+
				" The bundlable node was '%s' and the valid locations for assets in this scope are '%s'", 
				requirePath, asset.getAssetPath(), bundlableNode.getClass().getSimpleName(), scopedLocations);
	}
	
}