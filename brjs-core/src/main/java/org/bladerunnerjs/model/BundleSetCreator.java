package org.bladerunnerjs.model;

import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.utility.BundleSetBuilder;

public class BundleSetCreator {
	public static BundleSet createBundleSet(BundlableNode bundlableNode) throws ModelOperationException {
		BundleSetBuilder bundleSetBuilder = new BundleSetBuilder(bundlableNode);
		
		for(LinkedAssetFile seedFile : bundlableNode.getSeedFiles()) {
			bundleSetBuilder.addSeedFile(seedFile);
			processSeedFile(seedFile, bundleSetBuilder);
		}
		
		return bundleSetBuilder.createBundleSet();
	}
	
	private static void processSeedFile(LinkedAssetFile seedFile, BundleSetBuilder bundleSetBuilder) throws ModelOperationException {
		for(SourceFile sourceFile : seedFile.getDependentSourceFiles()) {
			if(bundleSetBuilder.addSourceFile(sourceFile)) {
				processSeedFile(sourceFile, bundleSetBuilder);
				
				for(LinkedAssetFile resourceSeedFile : sourceFile.getResources().seedResources()) {
					processSeedFile(resourceSeedFile, bundleSetBuilder);
				}
			}
		}
	}
}
