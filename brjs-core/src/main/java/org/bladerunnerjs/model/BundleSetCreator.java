package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.utility.BundleSetBuilder;

import com.google.common.base.Joiner;

public class BundleSetCreator {

	public class Messages {
		public static final String BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG = "%s '%s' has no seed files.";
		public static final String BUNDLABLE_NODE_SEED_FILES_MSG = "%s '%s' contains seed files %s.";
		public static final String APP_SOURCE_LOCATIONS_MSG = "App '%s' has source locations %s.";
		public static final String FILE_HAS_NO_DEPENDENCIES_MSG = "File '%s' has no dependencies.";
		public static final String FILE_DEPENDENCIES_MSG = "File '%s' depends on %s.";
	}

	private final BundlableNode bundlableNode;
	private Map<LinkedAsset, List<SourceModule>> cachedDependentSourceModules = new HashMap<LinkedAsset, List<SourceModule>>();
	private SourceModule bootstrapSourceModule;
	
	public BundleSetCreator(BundlableNode bundlableNode)
	{
		this.bundlableNode = bundlableNode;
	}
	
	public BundleSet createBundleSet() throws ModelOperationException {
		Logger logger = bundlableNode.root().logger(LoggerType.BUNDLER, BundleSetCreator.class);
		
		try
		{
			bootstrapSourceModule = bundlableNode.getSourceModule("bootstrap");
		}
		catch (RequirePathException e)
		{
			// do nothing, bootstrap is an implicit dependency if it exists
		}
		
		BundleSetBuilder bundleSetBuilder = new BundleSetBuilder(bundlableNode, bootstrapSourceModule);
		List<LinkedAsset> seedFiles = bundlableNode.seedFiles();
		
		String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
		if(seedFiles.isEmpty()) {
			logger.debug(Messages.BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, bundlableNode.getClass().getSimpleName(), name);
		}
		else {
			logger.debug(Messages.BUNDLABLE_NODE_SEED_FILES_MSG, bundlableNode.getClass().getSimpleName(), name, seedFilePaths(seedFiles));
		}
		
		logger.debug(Messages.APP_SOURCE_LOCATIONS_MSG, bundlableNode.getApp().getName(), assetContainerPaths(bundlableNode.getApp()));
		
		for(LinkedAsset seedFile : seedFiles) {
			bundleSetBuilder.addSeedFile(seedFile);
			processFile(seedFile, bundleSetBuilder, logger, new ArrayList<LinkedAsset>());
		}
		
		return bundleSetBuilder.createBundleSet();
	}
	
	private void processFile(LinkedAsset file, BundleSetBuilder bundleSetBuilder, Logger logger, List<LinkedAsset> processedFiles) throws ModelOperationException {

		if (processedFiles.contains(file))
		{
			return;
		}
		processedFiles.add(file);

		List<SourceModule> moduleDependencies = getDependentSourceModules(file);
		
		if(moduleDependencies.isEmpty()) {
			logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, getRelativePath(file.getAssetLocation().getAssetContainer().dir(), file.getUnderlyingFile()));
		}
		else {
			logger.debug(Messages.FILE_DEPENDENCIES_MSG, getRelativePath(file.getAssetLocation().getAssetContainer().dir(), file.getUnderlyingFile()), sourceFilePaths(moduleDependencies));
		}
		
		for(SourceModule sourceModule : moduleDependencies) {
			if(bundleSetBuilder.addSourceModule(sourceModule)) {
				processFile(sourceModule, bundleSetBuilder, logger, processedFiles);
				
				for(AssetLocation assetLocation : sourceModule.getAssetLocation().getAssetContainer().assetLocations()) {
					for(LinkedAsset resourceSeedFile : assetLocation.seedResources()) {
						processFile(resourceSeedFile, bundleSetBuilder, logger, processedFiles);
					}
				}
			}
		}
	}
	
	private List<SourceModule> getDependentSourceModules(LinkedAsset file) throws ModelOperationException {
		
		List<SourceModule> dependentSourceModules;
		if (cachedDependentSourceModules.containsKey(file))
		{
			dependentSourceModules = cachedDependentSourceModules.get(file);
		}
		else
		{
			dependentSourceModules = file.getDependentSourceModules(bundlableNode);
		
    		if(file instanceof SourceModule) {
    			SourceModule sourceModule = (SourceModule) file;
    			if(!sourceModule.getRequirePath().equals("bootstrap") && bootstrapSourceModule != null) 
    			{
    				dependentSourceModules.add(bootstrapSourceModule);
    			}
    		}
    		
    		cachedDependentSourceModules.put(file, dependentSourceModules);
		}
		
		return dependentSourceModules;
	}
	
	private String seedFilePaths(List<LinkedAsset> seedFiles) {
		List<String> seedFilePaths = new ArrayList<>();
		
		for(Asset seedFile : seedFiles) {
			seedFilePaths.add(getRelativePath(bundlableNode.dir(), seedFile.getUnderlyingFile()));
		}
		
		return "'" + Joiner.on("', '").join(seedFilePaths) + "'";
	}
	
	private String assetContainerPaths(App app) {
		List<String> assetContainerPaths = new ArrayList<>();
		
		for(AssetContainer assetContainer : app.getAllAssetContainers()) {
			File baseDir = assetContainer instanceof JsLibAppWrapper ? app.root().dir() : app.dir();
			assetContainerPaths.add(getRelativePath(baseDir, assetContainer.dir()));
		}
		
		return "'" + Joiner.on("', '").join(assetContainerPaths) + "'";
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
