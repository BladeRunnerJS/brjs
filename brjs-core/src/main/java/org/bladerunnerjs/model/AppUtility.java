package org.bladerunnerjs.model;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.DuplicateAssetContainerException;
import org.bladerunnerjs.api.model.exception.NodeAlreadyRegisteredException;


public class AppUtility
{

	public static <AC extends AssetContainer> AC getImplicitOrExplicitAssetContainer(BRJS brjs, Class<? extends AC> type, AC implicitAssetContainer, AC explicitAssetContainer, boolean preferExplicitDefault) { 
		if (implicitAssetContainer.exists() && explicitAssetContainer.exists()) {
			throw new DuplicateAssetContainerException(type.getSimpleName(), 
							brjs.dir().getRelativePath(implicitAssetContainer.dir()), 
							brjs.dir().getRelativePath(explicitAssetContainer.dir())
			);
		}
		AC assetContainer;
		if (explicitAssetContainer.exists()) {
			assetContainer = explicitAssetContainer;
		} else if (implicitAssetContainer.exists()) {
			assetContainer = implicitAssetContainer;
		} else {
			assetContainer = (preferExplicitDefault) ? explicitAssetContainer : implicitAssetContainer;
		}
		
		if (!brjs.isNodeRegistered(assetContainer)) {
			try
			{
				brjs.registerNode(assetContainer);
			}
			catch (NodeAlreadyRegisteredException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		return assetContainer;
	}
	
}
