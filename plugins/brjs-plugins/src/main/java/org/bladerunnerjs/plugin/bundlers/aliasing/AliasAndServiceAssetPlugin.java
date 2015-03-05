package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.plugin.AssetDiscoveryInitiator;
import org.bladerunnerjs.api.plugin.base.AbstractAssetPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.plugin.plugins.require.AliasDataSourceModule;
import org.bladerunnerjs.plugin.require.AliasCommonJsSourceModule;


public class AliasAndServiceAssetPlugin extends AbstractAssetPlugin
{

	@Override
	public List<Asset> discoverAssets(AssetContainer assetContainer, MemoizedFile dir, String requirePrefix, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator)
	{
		List<Asset> aliasAssets = new ArrayList<>();
		
		if (assetContainer.dir() == dir) {
			if (assetContainer instanceof BundlableNode && !(assetContainer instanceof JsLib)) {
				BundlableNode bundlableNode = (BundlableNode) assetContainer;
				createAliasDataSourceModule(assetDiscoveryInitiator, aliasAssets, bundlableNode);
				addBundlableNodeAliases(implicitDependencies, assetDiscoveryInitiator, aliasAssets, bundlableNode);
//				for (AssetContainer scopeAssetContainer : assetContainer.scopeAssetContainers()) {
//					for (MemoizedFile childDir : getAliasDefinitionsLocations(scopeAssetContainer.dir())) {
//						addScenarioAliases(implicitDependencies, assetDiscoveryInitiator, aliasAssets, bundlableNode, scopeAssetContainer, childDir);
//					}					
//				}
			}
			
			for (MemoizedFile childDir : getAliasDefinitionsLocations(dir)) {
				addAssetContainerAliases(assetContainer, implicitDependencies, assetDiscoveryInitiator, aliasAssets, childDir);
			}
		}
		
		return aliasAssets;
	}



	private void addAssetContainerAliases(AssetContainer assetContainer, List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator, List<Asset> aliasAssets, MemoizedFile childDir)
	{
		AliasDefinitionsFile aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, childDir);
		if (aliasDefinitionsFile.getUnderlyingFile().exists()) {
			for (AliasDefinition aliasDefinition : getAliases(aliasDefinitionsFile)) {
				if (!assetDiscoveryInitiator.hasRegisteredAsset(AliasCommonJsSourceModule.calculateRequirePath(aliasDefinition))) {
					Asset aliasAsset = new AliasCommonJsSourceModule(assetContainer, aliasDefinition, implicitDependencies);
					assetDiscoveryInitiator.registerAsset(aliasAsset);
					aliasAssets.add(aliasAsset);
				}
			}
		}
	}
	
	private void addBundlableNodeAliases(List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator, List<Asset> aliasAssets, BundlableNode bundlableNode)
	{
		AliasesFile aliasesFile = new AliasesFile(bundlableNode);
		for (AliasDefinition aliasDefinition : getAliases(aliasesFile)) {
			if (!scopeAssetContainersHaveAlias(bundlableNode, aliasDefinition)) {
				Asset aliasAsset = new AliasCommonJsSourceModule(bundlableNode, aliasDefinition, implicitDependencies);
				assetDiscoveryInitiator.registerAsset(aliasAsset);
				aliasAssets.add(aliasAsset);
			}
		}
	}

	private void addScenarioAliases(List<Asset> implicitDependencies, AssetDiscoveryInitiator assetDiscoveryInitiator, List<Asset> aliasAssets, BundlableNode bundlableNode, AssetContainer assetContainer, MemoizedFile childDir)
	{
		AliasesFile aliasesFile = new AliasesFile(bundlableNode);
		AliasDefinitionsFile aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, childDir);
		if (aliasDefinitionsFile.getUnderlyingFile().exists()) {
			for (AliasDefinition aliasDefinition : getScenarioAliases(aliasesFile, aliasDefinitionsFile)) {
				if (!assetDiscoveryInitiator.hasRegisteredAsset(AliasCommonJsSourceModule.calculateRequirePath(aliasDefinition))) {
					Asset aliasAsset = new AliasCommonJsSourceModule(assetContainer, aliasDefinition, implicitDependencies);
					assetDiscoveryInitiator.registerAsset(aliasAsset);
					aliasAssets.add(aliasAsset);
				}
			}
		}
	}
	


	private void createAliasDataSourceModule(AssetDiscoveryInitiator assetDiscoveryInitiator, List<Asset> aliasAssets, BundlableNode bundlableNode)
	{
		Asset aliasDataAsset = new AliasDataSourceModule(bundlableNode);
		if (!assetDiscoveryInitiator.hasRegisteredAsset(aliasDataAsset.getPrimaryRequirePath())) {
			assetDiscoveryInitiator.registerAsset(aliasDataAsset);
			aliasAssets.add(aliasDataAsset);
		}
	}
	


	private List<MemoizedFile> getAliasDefinitionsLocations(MemoizedFile dir) {
		List<MemoizedFile> aliasDefinitionsDirs = new ArrayList<>();
		for (MemoizedFile aliasDefintionsRootLocation : Arrays.asList(dir.file("src"), dir.file("resources"))) {
			aliasDefinitionsDirs.add(aliasDefintionsRootLocation);
			aliasDefinitionsDirs.addAll(aliasDefintionsRootLocation.nestedDirs());
		}
		return aliasDefinitionsDirs;
	}

	private boolean scopeAssetContainersHaveAlias(BundlableNode bundlableNode, AliasDefinition aliasDefinition)
	{
		for (AssetContainer scopeAssetContainer : bundlableNode.scopeAssetContainers()) {
			if (scopeAssetContainer == bundlableNode) {
				continue;
			}
			if (scopeAssetContainer.asset(AliasCommonJsSourceModule.calculateRequirePath(aliasDefinition)) != null) {
				return true;
			}
		}
		return false;
	}

	private List<AliasDefinition> getScenarioAliases(AliasesFile aliasesFile, AliasDefinitionsFile aliasDefinitionsFile) {
		try {
			List<AliasDefinition> aliasDefinitions = new ArrayList<>();
			
			for (AliasDefinition aliasDefinition : aliasDefinitionsFile.aliases()) {
				Map<String, AliasOverride> scenarioAliases = aliasDefinitionsFile.scenarioAliases(aliasDefinition);
				if (scenarioAliases.isEmpty()) {
					continue;
				}
				
				AliasOverride scenarioAlias = scenarioAliases.get(aliasesFile.scenarioName());
				AliasDefinition scenarioAliasDefinition;
				if (scenarioAlias == null) {
					scenarioAliasDefinition = aliasDefinition; 					
				} else {
					scenarioAliasDefinition = new AliasDefinition(scenarioAlias.getName(), scenarioAlias.getClassName(), aliasDefinition.getInterfaceName()); 
				}
				aliasDefinitions.add( scenarioAliasDefinition );
			}
			
			return aliasDefinitions;
		}
		catch (ContentFileProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private List<AliasDefinition> getNonScenarioAliases(AliasDefinitionsFile aliasDefinitionsFile)
	{
		try {
			List<AliasDefinition> aliasDefinitions = new ArrayList<>();
			
			for (AliasDefinition aliasDefinition : aliasDefinitionsFile.aliases()) {
				if (aliasDefinitionsFile.scenarioAliases(aliasDefinition).isEmpty()) {
					aliasDefinitions.add(aliasDefinition);
				}
			}
			
			return aliasDefinitions;
		}
		catch (ContentFileProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
	private List<AliasDefinition> getAliases(AliasDefinitionsFile aliasDefinitionsFile) 
	{
		try {
			return aliasDefinitionsFile.aliases();
		}
		catch (ContentFileProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}

	private List<AliasDefinition> getAliases(AliasesFile aliasesFile) {
		try {
			List<AliasDefinition> aliasDefinitions = new ArrayList<>();
			for (AliasOverride aliasOverride : aliasesFile.aliasOverrides()) {
				aliasDefinitions.add( aliasesFile.getAlias(aliasOverride.getName()) );
			}
			return aliasDefinitions;
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
