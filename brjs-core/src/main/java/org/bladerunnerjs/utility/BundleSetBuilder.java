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
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;

import com.google.common.base.Joiner;


public class BundleSetBuilder {
	private final Set<LinkedAsset> seedFiles = new HashSet<>();
	private final Set<SourceModule> sourceModules = new LinkedHashSet<>();
	private final Set<AliasDefinition> activeAliases = new HashSet<>();
	private final Set<AssetLocation> resources = new HashSet<>();
	private final List<LinkedAsset> processedFiles = new ArrayList<LinkedAsset>();
	private final BundlableNode bundlableNode;
	private final Logger logger;
	
	public BundleSetBuilder(BundlableNode bundlableNode) {
		this.bundlableNode = bundlableNode;
		logger = bundlableNode.root().logger(LoggerType.BUNDLER, BundleSetCreator.class);
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		List<AliasDefinition> activeAliasList = new ArrayList<>();
		List<AssetLocation> resourcesList = new ArrayList<>();
		
		try {
			activeAliasList.addAll(activeAliases);
			resourcesList.addAll(resources);
			
			for(AliasDefinition aliasDefinition : activeAliases) {
				addSourceModule(bundlableNode.getSourceModule(aliasDefinition.getRequirePath()));
			}
		}
		catch(RequirePathException e) {
			throw new ModelOperationException(e);
		}
		
		return new BundleSet(bundlableNode, orderSourceModules(sourceModules), activeAliasList, resourcesList);
	}
	
	public void addSeedFile(LinkedAsset seedFile) throws ModelOperationException {
		seedFiles.add(seedFile);
		activeAliases.addAll(getAliases(seedFile.getAliasNames()));
		addLinkedAsset(seedFile);
	}
	
	public void addSourceModule(SourceModule sourceModule) throws ModelOperationException {
		if(sourceModules.add(sourceModule)) {
			activeAliases.addAll(getAliases(sourceModule.getAliasNames()));
			resources.addAll(sourceModule.getAssetLocation().getDependentAssetLocations());
			
			addLinkedAsset(sourceModule);
			
			for(AssetLocation assetLocation : sourceModule.getAssetLocation().getAssetContainer().assetLocations()) {
				for(LinkedAsset resourceSeedFile : assetLocation.seedResources()) {
					addLinkedAsset(resourceSeedFile);
				}
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
		
		
		int maxIterations = sourceModules.size() * sourceModules.size();
		int iterationCount = 0;
		
		while(!sourceModules.isEmpty()) {
			Set<SourceModule> unprocessedSourceModules = new HashSet<>();
			for(SourceModule sourceModule : sourceModules) {
				if(dependenciesHaveBeenMet(sourceModule, metDependencies)) {
					sourceModulesList.add(sourceModule);
					metDependencies.add(sourceModule);
				}
				else {
					unprocessedSourceModules.add(sourceModule);
				}
			}
			
			if (iterationCount++ > maxIterations)
			{
				throw new ModelOperationException("Error satisfying source file dependencies. unprocessedSourceFiles = "+stringifySourceModules(unprocessedSourceModules));
			}
			
			sourceModules = unprocessedSourceModules;
		}
		
		return sourceModulesList;
	}
	
	private boolean dependenciesHaveBeenMet(SourceModule sourceModule, Set<LinkedAsset> metDependencies) throws ModelOperationException {
		for(LinkedAsset dependentSourceModule : getOrderDependentSourceModules(sourceModule, bundlableNode)) {
			if(!metDependencies.contains(dependentSourceModule)) {
				return false;
			}
		}
		
		return true;
	}
	
	private List<SourceModule> getOrderDependentSourceModules(SourceModule sourceModule, BundlableNode bundlableNode) throws ModelOperationException {
		List<SourceModule> orderDependentSourceModules = sourceModule.getOrderDependentSourceModules(bundlableNode);
		
		if(!sourceModule.getRequirePath().equals("bootstrap")) {
			try {
				orderDependentSourceModules.add(bundlableNode.getSourceModule("bootstrap"));
			}
			catch(RequirePathException e) {
				// do nothing: 'bootstrap' is only an implicit dependency if it exists 
			}
		}
		
		return orderDependentSourceModules;
	}

	private String stringifySourceModules(Set<SourceModule> sourceModules)
	{
		StringBuilder builder = new StringBuilder();
		for (SourceModule sourceModule : sourceModules)
		{
			builder.append(sourceModule.getRequirePath()+", ");
		}
		builder.setLength(builder.length()-2);
		return builder.toString();
		
	}
	
	// ---
	
	private void addLinkedAsset(LinkedAsset file) throws ModelOperationException {

		if (processedFiles.contains(file))
		{
			return;
		}
		processedFiles.add(file);

		List<SourceModule> moduleDependencies = getDependentSourceModules(file, bundlableNode);
		
		if(moduleDependencies.isEmpty()) {
			logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, getRelativePath(file.getAssetLocation().getAssetContainer().dir(), file.getUnderlyingFile()));
		}
		else {
			logger.debug(Messages.FILE_DEPENDENCIES_MSG, getRelativePath(file.getAssetLocation().getAssetContainer().dir(), file.getUnderlyingFile()), sourceFilePaths(moduleDependencies));
		}
		
		for(SourceModule sourceModule : moduleDependencies) {
			addSourceModule(sourceModule);
		}
	}
	
	private List<SourceModule> getDependentSourceModules(LinkedAsset file, BundlableNode bundlableNode) throws ModelOperationException {
		List<SourceModule> dependentSourceModules = file.getDependentSourceModules(bundlableNode);
		
		if(file instanceof SourceModule) {
			SourceModule sourceModule = (SourceModule) file;
			
			if(!sourceModule.getRequirePath().equals("bootstrap")) {
				try {
					dependentSourceModules.add(bundlableNode.getSourceModule("bootstrap"));
				}
				catch(RequirePathException e) {
					// do nothing: 'bootstrap' is only an implicit dependency if it exists 
				}
			}
		}
		
		return dependentSourceModules;
	}
	
	private String sourceFilePaths(List<SourceModule> sourceModules) {
		List<String> sourceFilePaths = new ArrayList<>();
		
		for(SourceModule sourceModule : sourceModules) {
			sourceFilePaths.add(getRelativePath(sourceModule.getAssetLocation().getAssetContainer().dir(), sourceModule.getUnderlyingFile()));
		}
		
		return "'" + Joiner.on("', '").join(sourceFilePaths) + "'";
	}
	
	private String getRelativePath(File baseFile, File sourceFile) {
		return baseFile.toURI().relativize(sourceFile.toURI()).getPath();
	}
}
