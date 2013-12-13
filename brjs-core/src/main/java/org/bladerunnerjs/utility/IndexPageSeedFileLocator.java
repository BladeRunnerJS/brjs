package org.bladerunnerjs.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.AssetFileInstantationException;

public class IndexPageSeedFileLocator {
	public static List<LinkedAsset> getSeedFiles(BundlableNode viewableBundlableNode) {
		List<LinkedAsset> seedFiles = new ArrayList<>();
		
		try {
			File indexFile = getIndexFile(viewableBundlableNode);
			
			if(indexFile != null) {
				LinkedAsset indexFileAsset = viewableBundlableNode.root().createAssetFile(FullyQualifiedLinkedAsset.class, viewableBundlableNode.assetLocation("resources"), indexFile);
				
				seedFiles.add(indexFileAsset);
			}
		}
		catch(AssetFileInstantationException e) {
			throw new RuntimeException(e);
		}
		
		return seedFiles;
	}
	
	private static File getIndexFile(BundlableNode viewableBundlableNode) {
		File indexFile = null;
		
		if(viewableBundlableNode.file("index.html").exists()) {
			indexFile = viewableBundlableNode.file("index.html");
		}
		else if(viewableBundlableNode.file("index.jsp").exists()) {
			indexFile = viewableBundlableNode.file("index.jsp");
		}
		
		return indexFile;
	}
}
