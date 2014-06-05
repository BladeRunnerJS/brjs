package org.bladerunnerjs.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.BundleSetCreator;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.BundleSetCreator.Messages;
import org.bladerunnerjs.model.StandardBundleSet;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;

import com.google.common.base.Joiner;


public class BundleSetBuilder {
	
	public static final String BOOTSTRAP_LIB_NAME = "br-bootstrap";
	
	private final Set<SourceModule> sourceModules = new LinkedHashSet<>();
	private final Map<SourceModule, Set<SourceModule>> orderDependentSourceModuleDependencies = new LinkedHashMap<>();
	private final Set<AliasDefinition> activeAliases = new LinkedHashSet<>();
	private final Set<LinkedAsset> linkedAssets = new HashSet<LinkedAsset>();
	private final Set<AssetLocation> assetLocations = new LinkedHashSet<>();
	private final BundlableNode bundlableNode;
	private final Logger logger;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		logger = bundlableNode.root().logger(LoggerType.BUNDLER, BundleSetCreator.class);
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourceLocationList = new ArrayList<>();
		
		if (bundlableNode instanceof Workbench) {
			for ( AssetLocation assetLocation : bundlableNode.app().aspect("default").seedAssetLocations() ) {
				addUncopedAssetLocation( assetLocation );				
			}
		}
		
		try {
			activeAliasList.addAll(activeAliases);
			resourceLocationList.addAll(assetLocations);
			orderAssetLocations(bundlableNode, resourceLocationList);
			
			
			for(AliasDefinition aliasDefinition : new ArrayList<>(activeAliases)) {
				addSourceModule(bundlableNode.getSourceModule(aliasDefinition.getRequirePath()));
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		SourceModule bootstrapSourceModule = null;
		List<SourceModule> bootstrappingSourceModules = new ArrayList<SourceModule>();
		try {
			if (!sourceModules.isEmpty())
			{
				bootstrapSourceModule = bundlableNode.getSourceModule(BOOTSTRAP_LIB_NAME);
				addSourceModule( bootstrapSourceModule );
				addAllSourceModuleDependencies(bootstrapSourceModule, bootstrappingSourceModules);
			}
		}
		catch(RequirePathException e) {
			// do nothing: 'bootstrap' is only an implicit dependency if it exists 
		}
		
		List<SourceModule> orderedSourceModules = new SourceModuleDependencyOrderCalculator(bundlableNode, bootstrappingSourceModules, sourceModules, orderDependentSourceModuleDependencies).getOrderedSourceModules();
		
		return new StandardBundleSet(bundlableNode, orderedSourceModules, activeAliasList, resourceLocationList);
	}

	public void addSeedFiles(List<LinkedAsset> seedFiles) throws ModelOperationException {
		for(LinkedAsset seedFile : seedFiles) {
			addLinkedAsset(seedFile);
		}
	}
	
	private void addSourceModule(SourceModule sourceModule) throws ModelOperationException {
		if (sourceModules.add(sourceModule)) {
			activeAliases.addAll(getAliases(sourceModule.getAliasNames()));
			addLinkedAsset(sourceModule);
		}
	}
	
	private void addLinkedAsset(LinkedAsset linkedAsset) throws ModelOperationException {
		
		if(linkedAssets.add(linkedAsset)) {
			List<SourceModule> moduleDependencies = new ArrayList<>(linkedAsset.getDependentSourceModules(bundlableNode));
			
			if (linkedAsset instanceof SourceModule) {
				moduleDependencies.addAll( ((SourceModule) linkedAsset).getOrderDependentSourceModules(bundlableNode) );
			}
			
			activeAliases.addAll(getAliases(linkedAsset.getAliasNames()));
			
			if(moduleDependencies.isEmpty()) {
				logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, linkedAsset.getAssetPath());
			}
			else {
				logger.debug(Messages.FILE_DEPENDENCIES_MSG, linkedAsset.getAssetPath(), sourceFilePaths(moduleDependencies));
			}
			
			if (linkedAsset instanceof SourceModule) {
				addSourceModule((SourceModule) linkedAsset);
			}
			
			for(SourceModule sourceModule : moduleDependencies) {
				addSourceModule(sourceModule);
			}
			
			addAssetLocation(linkedAsset.assetLocation());
		}
		
	}
	
	private void addAssetLocation(AssetLocation assetLocation) throws ModelOperationException {
		
		if(assetLocations.add(assetLocation)) {
			for(LinkedAsset resourceSeedFile : assetLocation.linkedAssets()) {
				addLinkedAsset(resourceSeedFile);
			}
			
			for(AssetLocation dependentAssetLocation : assetLocation.dependentAssetLocations()) {
				addAssetLocation(dependentAssetLocation);
			}
		}
	}
	
	private void addUncopedAssetLocation(AssetLocation assetLocation) throws ModelOperationException {
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
				
				// TODO: get rid of this guard once we remove the 'SERVICE!' hack
				if (alias != null)
				{
					SourceModule sourceModule = bundlableNode.getSourceModule(alias.getRequirePath());
					addSourceModule(sourceModule);
					
					if(alias.getInterfaceName() != null) {
						addOrderDependentSourceModuleDependency(sourceModule, bundlableNode.getSourceModule(alias.getInterfaceRequirePath()));
					}
					
					aliases.add(alias);
				}
			}
		}
		catch(AliasException | ContentFileProcessingException | RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return aliases;
	}
	
	private void addOrderDependentSourceModuleDependency(SourceModule sourceModule, SourceModule dependency) throws ModelOperationException {
		if(sourceModule != dependency) {
			if(!orderDependentSourceModuleDependencies.containsKey(sourceModule)) {
				orderDependentSourceModuleDependencies.put(sourceModule, new LinkedHashSet<SourceModule>());
			}
			
			addSourceModule(dependency);
			orderDependentSourceModuleDependencies.get(sourceModule).add(dependency);
		}
	}
	
	private String sourceFilePaths(List<SourceModule> sourceModules) {
		List<String> sourceFilePaths = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			sourceFilePaths.add(sourceModule.getAssetPath());
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
		
		for (SourceModule dependency : sourceModule.getDependentSourceModules(bundlableNode))
		{
			if (!sourceModules.contains(dependency)) {
				addAllSourceModuleDependencies(dependency, sourceModules, processedModules);
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
			unorderedAssetLocations.addAll(0, assetLocationsForThisContainer);
		}
	}
	
}
