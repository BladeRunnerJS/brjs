package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasException;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSetCreator;
import org.bladerunnerjs.model.BundleSetCreator.Messages;
import org.bladerunnerjs.model.StandardBundleSet;

import com.google.common.base.Joiner;

public class BundleSetBuilder {
	
	public static final String BOOTSTRAP_LIB_NAME = "br-bootstrap";
	
	private final List<Asset> assets = new ArrayList<>();
	private final Set<SourceModule> sourceModules = new LinkedHashSet<>();
	private final Map<String,AliasDefinition> activeAliases = new LinkedHashMap<>();
	private final Set<LinkedAsset> linkedAssets = new HashSet<LinkedAsset>();
	private final Set<AssetLocation> assetLocations = new LinkedHashSet<>();
	private final BundlableNode bundlableNode;
	private final Logger logger;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		logger = bundlableNode.root().logger(BundleSetCreator.class);
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourceLocationList = new ArrayList<>();
		
		if (bundlableNode instanceof Workbench) {
			// TODO: this should be done via the API and not guessed from the outside
			AssetLocation defaultAspectResourcesAssetLocation = bundlableNode.app().aspect("default").assetLocation("resources");
			if (defaultAspectResourcesAssetLocation != null) {
				addUnscopedAssetLocation(defaultAspectResourcesAssetLocation);
			}
		}
		
		List<SourceModule> bootstrappingSourceModules = new ArrayList<SourceModule>();
		if (!sourceModules.isEmpty())
		{
			addBootstrapAndDependencies(bootstrappingSourceModules);
		}
		
		try {
			activeAliasList.addAll(activeAliases.values());
			resourceLocationList.addAll(assetLocations);
			orderAssetLocations(bundlableNode, resourceLocationList);
			
			for(AliasDefinition aliasDefinition : new ArrayList<>(activeAliases.values())) {
				addSourceModule((SourceModule)bundlableNode.getLinkedAsset(aliasDefinition.getRequirePath()));
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		List<SourceModule> orderedSourceModules = SourceModuleDependencyOrderCalculator.getOrderedSourceModules(bundlableNode, bootstrappingSourceModules, sourceModules);
		
		return new StandardBundleSet(bundlableNode, assets, orderedSourceModules, activeAliasList, resourceLocationList);
	}

	public void addSeedFiles(List<LinkedAsset> seedFiles) throws ModelOperationException {
		for(LinkedAsset seedFile : seedFiles) {
			addLinkedAsset(seedFile);
		}
	}
	
	private void addSourceModule(SourceModule sourceModule) throws ModelOperationException {
		if (sourceModules.add(sourceModule)) {
			addAliases( getAliases(sourceModule.getAliasNames()) );
			addLinkedAsset(sourceModule);
		}
	}

	private void addLinkedAsset(LinkedAsset linkedAsset) throws ModelOperationException {
		
		if(linkedAssets.add(linkedAsset)) {
			assets.add(linkedAsset);
			List<Asset> moduleDependencies = new ArrayList<>(linkedAsset.getDependentAssets(bundlableNode));
			
			addAliases( getAliases(linkedAsset.getAliasNames()) );
			
			if(moduleDependencies.isEmpty()) {
				logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, linkedAsset.getAssetPath());
			}
			else {
				
				logger.debug(Messages.FILE_DEPENDENCIES_MSG, linkedAsset.getAssetPath(), assetFilePaths(moduleDependencies));
			}
			
			if (linkedAsset instanceof SourceModule) {
				addSourceModule((SourceModule) linkedAsset);
			}
			
			for(Asset asset : moduleDependencies) {
				if(asset instanceof SourceModule){
					addSourceModule((SourceModule)asset);
				}else {
					addAssetLocation(asset.assetLocation());
					if (asset instanceof LinkedAsset) {
						addLinkedAsset((LinkedAsset) asset);						
					}
				}
				assets.add(asset);
			}
			
			addAssetLocation(linkedAsset.assetLocation());
		}
		
	}
	
	private void addAssetLocation(AssetLocation assetLocation) throws ModelOperationException {
		if (assetLocation == null) {
			return;
		}
		if (assetLocations.add(assetLocation)) {
			for(LinkedAsset resourceSeedFile : assetLocation.linkedAssets()) {
				addLinkedAsset(resourceSeedFile);
			}
			
			for(AssetLocation dependentAssetLocation : assetLocation.dependentAssetLocations()) {
				addAssetLocation(dependentAssetLocation);
			}
		}
	}
	
	private void addUnscopedAssetLocation(AssetLocation assetLocation) throws ModelOperationException {
		if (assetLocation == null) { return; }
		if (assetLocations.add(assetLocation)) {			
			for(AssetLocation dependentAssetLocation : assetLocation.dependentAssetLocations()) {
				addAssetLocation(dependentAssetLocation);
			}
		}
	}

	private List<AliasDefinition> getAliases(List<String> aliasNames) throws ModelOperationException {
		List<AliasDefinition> aliases = new ArrayList<>();
		
		try {
			for(String aliasName : aliasNames) {
				AliasDefinition alias = bundlableNode.getAlias(aliasName);
				SourceModule sourceModule =  (SourceModule)bundlableNode.getLinkedAsset(alias.getRequirePath());
				addSourceModule(sourceModule);
				
				if(alias.getInterfaceName() != null) {
					SourceModule aliasInterface = (SourceModule) bundlableNode.getLinkedAsset(alias.getInterfaceRequirePath());
					
					if(sourceModule != aliasInterface) {
						addSourceModule(aliasInterface);
					}
				}
				
				aliases.add(alias);
			}
		}
		catch(AliasException | ContentFileProcessingException | RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return aliases;
	}
	
	private String assetFilePaths(List<Asset> assets) {
		List<String> sourceFilePaths = new ArrayList<>();
		
		for(Asset asset : assets) {
			sourceFilePaths.add(asset.getAssetPath());
		}
		
		return "'" + Joiner.on("', '").join(sourceFilePaths) + "'";
	}
	
	
	private void addAllSourceModuleDependencies(SourceModule sourceModule, List<SourceModule> sourceModules) throws ModelOperationException
	{
		addAllSourceModuleDependencies(sourceModule, sourceModules, new ArrayList<SourceModule>());
	}
	
	private void addAllSourceModuleDependencies(SourceModule sourceModule, List<SourceModule> sourceModules, List<SourceModule> processedModules) throws ModelOperationException
	{
		if (processedModules.contains(sourceModule))
		{
			return;
		}
		processedModules.add(sourceModule);
		
		for (Asset asset : sourceModule.getDependentAssets(bundlableNode))
		{
			if (!sourceModules.contains(asset)) {
				if(asset instanceof SourceModule){
					addAllSourceModuleDependencies((SourceModule)asset, sourceModules, processedModules);
				}
			}
		}
		sourceModules.add(sourceModule);
	}
	
	
	private void orderAssetLocations(BundlableNode bundlableNode, List<AssetLocation> unorderedAssetLocations)
	{
		for (AssetContainer assetContainer : bundlableNode.scopeAssetContainers())
		{
			List<AssetLocation> assetLocationsForThisContainer = new ArrayList<>();
			for (AssetLocation assetLocation : unorderedAssetLocations)
			{
				if (assetLocation.assetContainer() == assetContainer)
				{
					assetLocationsForThisContainer.add(assetLocation);
				}
			}
			unorderedAssetLocations.removeAll(assetLocationsForThisContainer);
			unorderedAssetLocations.addAll(assetLocationsForThisContainer);
		}
	}
	
	private void addBootstrapAndDependencies(List<SourceModule> bootstrappingSourceModules) throws ModelOperationException
	{
		JsLib boostrapLib = bundlableNode.app().jsLib(BOOTSTRAP_LIB_NAME);
		for (Asset asset : boostrapLib.assets()) {
			if (asset instanceof SourceModule) {
				addSourceModule( (SourceModule) asset );
				addAllSourceModuleDependencies( (SourceModule) asset, bootstrappingSourceModules );						
			}
		}
		for (AssetLocation assetLocation : boostrapLib.assetLocations()) {
			addUnscopedAssetLocation(assetLocation);					
		}
	}
	
	private void addAliases(List<AliasDefinition> aliases)
	{
		for (AliasDefinition alias : aliases) {
			if (!activeAliases.containsKey(alias.getName())) {
				activeAliases.put(alias.getName(), alias);
			}
		}
	}
	
	
}
