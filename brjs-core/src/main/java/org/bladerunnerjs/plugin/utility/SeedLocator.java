package org.bladerunnerjs.plugin.utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.FullyQualifiedLinkedAsset;
import org.bladerunnerjs.model.LinkedAsset;

public class SeedLocator {
	private final Map<String, LinkedAsset> cachedAssets = new HashMap<>();
	private final MemoizedValue<List<LinkedAsset>> seedAssetsList;
	
	public SeedLocator(BRJS brjs) {
		seedAssetsList = new MemoizedValue<>("AssetLocation.seedAssets", brjs, brjs.dir());
	}
	
	public List<LinkedAsset> seedAssets(BundlableNode viewableBundlableNode) {
		return seedAssetsList.value(() -> {
			List<LinkedAsset> seedFiles = new ArrayList<>();
			File indexFile = getIndexFile(viewableBundlableNode);
			
			if (indexFile != null) {
				String indexFilePath = indexFile.getAbsolutePath();
				
				if(!cachedAssets.containsKey(indexFilePath)) {
					// BM: seed files
					cachedAssets.put(indexFilePath, new FullyQualifiedLinkedAsset(viewableBundlableNode.assetLocation("resources"), indexFile.getParentFile(), indexFile.getName()));
				}
				
				seedFiles.add(cachedAssets.get(indexFilePath));
			}
			
			return seedFiles;
		});
	}
	
	private File getIndexFile(BundlableNode viewableBundlableNode) {
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
