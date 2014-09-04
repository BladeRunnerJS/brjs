package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.DuplicateAssetContainerException;
import org.bladerunnerjs.model.exception.NodeAlreadyRegisteredException;
import org.bladerunnerjs.utility.RelativePathUtility;


public class AppUtility
{

	static <AC extends AssetContainer> AC getImplicitOrExplicitAssetContainer(BRJS brjs, Class<? extends AC> type, AC implicitAssetContainer, AC explicitAssetContainer, boolean preferExplicitDefault) { 
		if (implicitAssetContainer.exists() && explicitAssetContainer.exists()) {
			throw new DuplicateAssetContainerException(type.getSimpleName(), 
							RelativePathUtility.get(brjs, brjs.dir(), implicitAssetContainer.dir()), 
							RelativePathUtility.get(brjs, brjs.dir(), explicitAssetContainer.dir())
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
