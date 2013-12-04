package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.BundleSetBuilder;

import com.google.common.base.Joiner;

public class BundleSetCreator {

	public class Messages {
		public static final String BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG = "%s '%s' has no seed files.";
		public static final String BUNDLABLE_NODE_SEED_FILES_MSG = "%s '%s' contains seed files %s.";
		public static final String APP_SOURCE_LOCATIONS_MSG = "App '%s' has source locations %s.";
		public static final String FILE_HAS_NO_DEPENDENCIES_MSG = "File '%s' has no dependencies.";
		public static final String FILE_DEPENDENCIES_MSG = "File '%s' depends on %s.";
	}
	
	public static BundleSet createBundleSet(BundlableNode bundlableNode) throws ModelOperationException {
		Logger logger = bundlableNode.root().logger(LoggerType.BUNDLER, BundleSetCreator.class);
		
		BundleSetBuilder bundleSetBuilder = new BundleSetBuilder(bundlableNode);
		List<LinkedAsset> seedFiles = bundlableNode.seedFiles();
		
		String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
		if(seedFiles.isEmpty()) {
			logger.debug(Messages.BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, bundlableNode.getClass().getSimpleName(), name);
		}
		else {
			logger.debug(Messages.BUNDLABLE_NODE_SEED_FILES_MSG, bundlableNode.getClass().getSimpleName(), name, seedFilePaths(bundlableNode, seedFiles));
		}
		
		logger.debug(Messages.APP_SOURCE_LOCATIONS_MSG, bundlableNode.getApp().getName(), assetContainerPaths(bundlableNode.getApp()));
		
		for(LinkedAsset seedFile : seedFiles) {
			bundleSetBuilder.addSeedFile(seedFile);
			processFile(bundlableNode, seedFile, bundleSetBuilder, logger);
		}
		
		return bundleSetBuilder.createBundleSet();
	}
	
	private static void processFile(BundlableNode bundlableNode, LinkedAsset file, BundleSetBuilder bundleSetBuilder, Logger logger) throws ModelOperationException {
		List<SourceModule> moduleDependencies = file.getDependentSourceModules(bundlableNode);
		
		if(moduleDependencies.isEmpty()) {
			logger.debug(Messages.FILE_HAS_NO_DEPENDENCIES_MSG, getRelativePath(file.getAssetLocation().getAssetContainer().dir(), file.getUnderlyingFile()));
		}
		else {
			logger.debug(Messages.FILE_DEPENDENCIES_MSG, getRelativePath(file.getAssetLocation().getAssetContainer().dir(), file.getUnderlyingFile()), sourceFilePaths(moduleDependencies));
		}
		
		for(SourceModule sourceModule : moduleDependencies) {
			if(bundleSetBuilder.addSourceFile(sourceModule)) {
				processFile(bundlableNode, sourceModule, bundleSetBuilder, logger);
				
				for(AssetLocation assetLocation : sourceModule.getAssetLocation().getAssetContainer().getAllAssetLocations()) {
					for(LinkedAsset resourceSeedFile : assetLocation.seedResources()) {
						processFile(bundlableNode, resourceSeedFile, bundleSetBuilder, logger);
					}
				}
			}
		}
	}
	
	private static String seedFilePaths(BundlableNode bundlableNode, List<LinkedAsset> seedFiles) {
		List<String> seedFilePaths = new ArrayList<>();
		
		for(Asset seedFile : seedFiles) {
			seedFilePaths.add(getRelativePath(bundlableNode.dir(), seedFile.getUnderlyingFile()));
		}
		
		return "'" + Joiner.on("', '").join(seedFilePaths) + "'";
	}
	
	private static String assetContainerPaths(App app) {
		List<String> assetContainerPaths = new ArrayList<>();
		
		for(AssetContainer assetContainer : app.getAllAssetContainers()) {
			File baseDir = assetContainer instanceof JsLibAppWrapper ? app.root().dir() : app.dir();
			assetContainerPaths.add(getRelativePath(baseDir, assetContainer.dir()));
		}
		
		return "'" + Joiner.on("', '").join(assetContainerPaths) + "'";
	}
	
	private static String sourceFilePaths(List<SourceModule> sourceFiles) {
		List<String> sourceFilePaths = new ArrayList<>();
		
		for(SourceModule sourceFile : sourceFiles) {
			sourceFilePaths.add(getRelativePath(sourceFile.getAssetLocation().getAssetContainer().dir(), sourceFile.getUnderlyingFile()));
		}
		
		return "'" + Joiner.on("', '").join(sourceFilePaths) + "'";
	}
	
	private static String getRelativePath(File baseFile, File sourceFile) {
		return baseFile.toURI().relativize(sourceFile.toURI()).getPath();
	}
}
