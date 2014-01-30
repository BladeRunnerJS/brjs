package org.bladerunnerjs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.BundleSetCreator;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.BundleSetCreator.Messages;
import org.bladerunnerjs.model.exception.CircularDependencyException;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;

import com.google.common.base.Joiner;


public class BundleSetBuilder {
	private static final String BOOTSTRAP_LIB_NAME = "br-bootstrap";
	private final Set<SourceModule> sourceModules = new LinkedHashSet<>();
	private final Set<AliasDefinition> activeAliases = new HashSet<>();
	private final Set<LinkedAsset> linkedAssets = new HashSet<LinkedAsset>();
	private final Set<AssetLocation> assetLocations = new HashSet<>();
	private final BundlableNode bundlableNode;
	private final Logger logger;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		logger = bundlableNode.root().logger(LoggerType.BUNDLER, BundleSetCreator.class);
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourceLocationList = new ArrayList<>();
		
		try {
			activeAliasList.addAll(activeAliases);
			resourceLocationList.addAll(assetLocations);
			
			for(AliasDefinition aliasDefinition : activeAliases) {
				addSourceModule(bundlableNode.getSourceModule(aliasDefinition.getRequirePath()));
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		if (sourceModules.size() > 0)
		{
    		try {
    			
    			addSourceModule(bundlableNode.getSourceModule(BOOTSTRAP_LIB_NAME));
    		}
    		catch(RequirePathException e) {
    			// do nothing: 'bootstrap' is only an implicit dependency if it exists 
    		}
		}
		
		return new BundleSet(bundlableNode, orderSourceModules(sourceModules), activeAliasList, resourceLocationList);
	}
	
	public void addSeedFiles(List<LinkedAsset> seedFiles) throws ModelOperationException {
		for(LinkedAsset seedFile : seedFiles) {
			addLinkedAsset(seedFile);
		}
	}
	
	private void addSourceModule(SourceModule sourceModule) throws ModelOperationException {
		if(sourceModules.add(sourceModule)) {
			activeAliases.addAll(getAliases(sourceModule.getAliasNames()));
			addLinkedAsset(sourceModule);
		}
	}
	
	private void addLinkedAsset(LinkedAsset linkedAsset) throws ModelOperationException {
		
		if(linkedAssets.add(linkedAsset)) {
			List<SourceModule> moduleDependencies = linkedAsset.getDependentSourceModules(bundlableNode);
			
			
			activeAliases.addAll(getAliases(linkedAsset.getAliasNames()));
			 
			File linkedAssetFile = linkedAsset.getUnderlyingFile();
			File assetLocationDir = linkedAsset.getAssetLocation().getAssetContainer().dir();
			String linkedAssetRelativePath = "";
			if (linkedAssetFile.getAbsolutePath().equals( assetLocationDir.getAbsolutePath()))
			{
				linkedAssetRelativePath = RelativePathUtility.get(linkedAsset.getAssetLocation().root().dir(), linkedAssetFile);
			}
			else
			{
				linkedAssetRelativePath = RelativePathUtility.get(assetLocationDir, linkedAssetFile);				
			}
			
			if(moduleDependencies.isEmpty()) {
				logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, linkedAssetRelativePath);
			}
			else {
				logger.debug(Messages.FILE_DEPENDENCIES_MSG, linkedAssetRelativePath, sourceFilePaths(moduleDependencies));
			}
			
			for(SourceModule sourceModule : moduleDependencies) {
				addSourceModule(sourceModule);
			}
			
			addAssetLocation(linkedAsset.getAssetLocation());
		}
		
	}
	
	private void addAssetLocation(AssetLocation assetLocation) throws ModelOperationException {
		
		
		if(assetLocations.add(assetLocation)) {
			for(LinkedAsset resourceSeedFile : assetLocation.seedResources()) {
				addLinkedAsset(resourceSeedFile);
			}
			
			for(AssetLocation dependentAssetLocation : assetLocation.getDependentAssetLocations()) {
				addAssetLocation(dependentAssetLocation);
			}
		}
	}

	private List<AliasDefinition> getAliases(List<String> aliasNames) throws ModelOperationException {
		List<AliasDefinition> aliases = new ArrayList<>();
		
		try {
			for(String aliasName : aliasNames) {
				aliases.add(bundlableNode.getAlias(aliasName));
			}
		}
		catch(AliasException | BundlerFileProcessingException e) {
			throw new ModelOperationException(e);
		}
		
		return aliases;
	}

	private List<SourceModule> orderSourceModules(Set<SourceModule> sourceModules) throws ModelOperationException {
		List<SourceModule> sourceModulesList = new ArrayList<>();
		Set<LinkedAsset> metDependencies = new HashSet<>();		
		
		while(!sourceModules.isEmpty()) {
			Set<SourceModule> unprocessedSourceModules = new HashSet<>();
			boolean progressMade = false;
			
			for(SourceModule sourceModule : sourceModules) {
				if(dependenciesHaveBeenMet(sourceModule, metDependencies)) {
					progressMade = true;
					sourceModulesList.add(sourceModule);
					metDependencies.add(sourceModule);
				}
				else {
					unprocessedSourceModules.add(sourceModule);
				}
			}
			
			if (!progressMade)
			{
				throw new CircularDependencyException(unprocessedSourceModules);
			}
			
			sourceModules = unprocessedSourceModules;
		}
		
		return sourceModulesList;
	}
	
	private boolean dependenciesHaveBeenMet(SourceModule sourceModule, Set<LinkedAsset> metDependencies) throws ModelOperationException {
		for(LinkedAsset dependentSourceModule : sourceModule.getOrderDependentSourceModules(bundlableNode)) {
			if(!metDependencies.contains(dependentSourceModule)) {
				return false;
			}
		}
		
		return true;
	}
	
	private String sourceFilePaths(List<SourceModule> sourceModules) {
		List<String> sourceFilePaths = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			File assetContainerDir = sourceModule.getAssetLocation().getAssetContainer().dir();
			File sourceModuleFile = sourceModule.getUnderlyingFile();
			if (assetContainerDir.getAbsolutePath().equals( sourceModuleFile.getAbsolutePath() ))
			{
				sourceFilePaths.add( RelativePathUtility.get(sourceModule.getAssetLocation().root().dir(), sourceModuleFile) );
			}
			else
			{
				sourceFilePaths.add( RelativePathUtility.get(assetContainerDir, sourceModuleFile) );
			}
		}
		
		return "'" + Joiner.on("', '").join(sourceFilePaths) + "'";
	}
	
}