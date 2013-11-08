package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.BundleSetBuilder;

public class BundleSetCreator {
	public static BundleSet createBundleSet(BundlableNode bundlableNode) throws ModelOperationException {
		BundleSetBuilder bundleSetBuilder = new BundleSetBuilder(bundlableNode);
		
		for(LinkedAssetFile seedFile : bundlableNode.seedFiles()) {
			bundleSetBuilder.addSeedFile(seedFile);
			processFile(seedFile, bundleSetBuilder);
		}
		
		return bundleSetBuilder.createBundleSet();
	}
	
	private static void processFile(LinkedAssetFile file, BundleSetBuilder bundleSetBuilder) throws ModelOperationException {
		for(SourceFile sourceFile : file.getDependentSourceFiles()) {
			if(bundleSetBuilder.addSourceFile(sourceFile)) {
				processFile(sourceFile, bundleSetBuilder);
				
				for(Resources resources : sourceFile.getResources()) {
					for(LinkedAssetFile resourceSeedFile : resources.seedResources()) {
						processFile(resourceSeedFile, bundleSetBuilder);
					}
				}
			}
		}
	}
}
