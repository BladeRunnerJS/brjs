package org.bladerunnerjs.api.model.exception;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.LinkedAsset;


public class OutOfScopeRequirePathException extends RequirePathException
{
	private static final long serialVersionUID = 4008632015949516509L;
	private LinkedAsset sourceAsset;
	private Asset dependantAsset;
	private String scopedLocations;

	public OutOfScopeRequirePathException(LinkedAsset sourceAsset, Asset dependantAsset) {
		this.sourceAsset = sourceAsset;
		this.dependantAsset = dependantAsset;
		scopedLocations = RequirePathExceptionUtils.getScopeLocationText(sourceAsset.assetContainer());
	}

	@Override
	public String getMessage()
	{
		return String.format(
				"The asset with the primary require path '%s' has a dependency on the asset with the primary require path '%s',"+
				" which is located at '%s' and is outside of the assets' scope."+
				" The source asset is contained within the '%s' scope and can only depend on the assets in the following locations: '%s'.",
				sourceAsset.getPrimaryRequirePath(), dependantAsset.getPrimaryRequirePath(), dependantAsset.getAssetPath(),
				sourceAsset.assetContainer().getClass().getSimpleName(), scopedLocations);
	}
	
}
