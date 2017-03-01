package org.bladerunnerjs.api.model.exception;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.LinkedAsset;

public class OutOfBundleScopeRequirePathException extends RequirePathException {

	private static final long serialVersionUID = 1L;
	private String requirePath;
	private String scopedLocations;
	private Asset asset;
	private BundlableNode bundlableNode;
	private Asset assetWithException = null;
	
	public OutOfBundleScopeRequirePathException(BundlableNode bundlableNode, String requirePath, Asset asset) {
		this.bundlableNode = bundlableNode;
		this.requirePath = requirePath;
		this.asset = asset;
		scopedLocations = RequirePathExceptionUtils.getScopeLocationText(bundlableNode);
	}
	
	public void setAssetWithException(LinkedAsset asset) {
		assetWithException = asset;
	}

	@Override
	public String getMessage() {
		if (assetWithException != null) {
			return String.format("There was an exception calculating dependencies for the asset at '%s'. It's dependency with the require path '%s' was found at '%s', but it was not in one of the valid bundler scopes."+
					" The bundlable node was '%s' and the valid locations for assets in this scope are '%s'", 
					assetWithException.getAssetPath(), requirePath, asset.getAssetPath(), bundlableNode.getClass().getSimpleName(), scopedLocations);			
		}
		return String.format("The asset with the require path '%s' was found at '%s', but it was not in one of the valid bundler scopes."+
				" The bundlable node was '%s' and the valid locations for assets in this scope are '%s'", 
				requirePath, asset.getAssetPath(), bundlableNode.getClass().getSimpleName(), scopedLocations);
	}
	
}