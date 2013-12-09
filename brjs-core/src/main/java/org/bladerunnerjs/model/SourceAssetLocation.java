package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;

public class SourceAssetLocation extends ShallowAssetLocation {
	private final Map<File, AssetLocation> assetLocations = new HashMap<>();
	
	public SourceAssetLocation(RootNode rootNode, Node parent, File dir) {
		super(rootNode, parent, dir);
	}
	
	public List<AssetLocation> getChildAssetLocations() {
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		if (dir().isDirectory()) {
			Iterator<File> fileIterator = FileUtils.iterateFilesAndDirs(dir(), FalseFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
			
			while (fileIterator.hasNext()) {
				File nextDir = fileIterator.next();
				
				if (!nextDir.equals(dir())) {
					assetLocations.add(getChildAssetLocation(nextDir));
				}
			}
		}
		
		return assetLocations;
	}
	
	private AssetLocation getChildAssetLocation(File dir) {
		AssetLocation assetLocation = assetLocations.get(dir);
		
		if (assetLocation == null) {
			assetLocation = new ShallowAssetLocation(assetContainer.root(), assetContainer, dir);
			assetLocations.put(dir, assetLocation);
		}
		
		return assetLocation;
	}
}
