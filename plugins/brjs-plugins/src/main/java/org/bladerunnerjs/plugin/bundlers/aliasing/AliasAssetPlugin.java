package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;
import org.bladerunnerjs.plugin.require.AliasCommonJsSourceModule;


public class AliasAssetPlugin extends AbstractAssetPlugin
{

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		List<Asset> aliasAssets = new ArrayList<>();
		
		if (assetContainer instanceof BundlableNode && assetContainer.dir() == dir) {
			BundlableNode bundlableNode = (BundlableNode) assetContainer;
			
			AliasesFile aliasesFile = new AliasesFile(bundlableNode);
			for (AliasDefinition aliasDefinition : getAliases(aliasesFile)) {
				if (!assetDiscoveryInitiator.hasRegisteredAsset(AliasCommonJsSourceModule.calculateRequirePath(aliasDefinition))) {
					Asset aliasAsset = new AliasCommonJsSourceModule(bundlableNode, aliasDefinition);
					assetDiscoveryInitiator.registerAsset(aliasAsset);
					aliasAssets.add(aliasAsset);
				}
			}
			
			Asset aliasDataAsset = new AliasDataSourceModule(bundlableNode);
			if (!assetDiscoveryInitiator.hasRegisteredAsset(aliasDataAsset.getPrimaryRequirePath())) {
				assetDiscoveryInitiator.registerAsset(aliasDataAsset);
				aliasAssets.add(aliasDataAsset);
			}
		}
		
		return aliasAssets;
	}
	
	private List<AliasDefinition> getAliases(AliasesFile aliasesFile) {
		try {
			return aliasesFile.getAliases();
		}
		catch (ContentFileProcessingException | AliasException ex) {
			throw new RuntimeException(ex);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	
	
}
