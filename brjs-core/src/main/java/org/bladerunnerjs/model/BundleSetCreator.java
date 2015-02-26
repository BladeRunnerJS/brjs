package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.events.BundleSetCreatedEvent;
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
	
	public static BundleSet createBundleSet(BundlableNode bundlableNode) throws ModelOperationException {
		long creationStartTime = System.currentTimeMillis();
		
		Logger logger = bundlableNode.root().logger(BundleSetCreator.class);
		
		BundleSetBuilder bundleSetBuilder = new BundleSetBuilder(bundlableNode);
		List<LinkedAsset> seedFiles = bundlableNode.seedAssets();
		
		String name = (bundlableNode instanceof NamedNode) ? ((NamedNode) bundlableNode).getName() : "default";
		if(seedFiles.isEmpty()) {
			logger.debug(Messages.BUNDLABLE_NODE_HAS_NO_SEED_FILES_MSG, bundlableNode.getTypeName(), name);
		}
		else {
			logger.debug(Messages.BUNDLABLE_NODE_SEED_FILES_MSG, bundlableNode.getTypeName(), name, seedFilePaths(bundlableNode, seedFiles));
		}
		
		logger.debug(Messages.APP_SOURCE_LOCATIONS_MSG, bundlableNode.app().getName(), assetContainerPaths(bundlableNode.app()));
		
		bundleSetBuilder.addSeedFiles(seedFiles);
		
		BundleSet bundleSet = bundleSetBuilder.createBundleSet();
		
		long creationEndTime = System.currentTimeMillis();
		long duration = creationEndTime - creationStartTime;
		bundlableNode.notifyObservers(new BundleSetCreatedEvent(bundleSet, duration), bundlableNode);
		
		return bundleSet;
	}
	
	private static String seedFilePaths(BundlableNode bundlableNode, List<? extends LinkedAsset> seedFiles) {
		List<String> seedFilePaths = new ArrayList<>();
		
		for(Asset seedFile : seedFiles) {
			seedFilePaths.add(seedFile.getAssetPath());
		}
		
		return "'" + Joiner.on("', '").join(seedFilePaths) + "'";
	}
	
	private static String assetContainerPaths(App app) {
		List<String> assetContainerPaths = new ArrayList<>();
		
		for(AssetContainer assetContainer : app.getAllAssetContainers()) {
			MemoizedFile baseDir = assetContainer instanceof AppSdkJsLib ? app.root().dir() : app.dir();
			assetContainerPaths.add(baseDir.getRelativePath(assetContainer.dir()));
		}
		
		return "'" + Joiner.on("', '").join(assetContainerPaths) + "'";
	}
}
