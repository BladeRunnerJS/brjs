package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedFileAsset;

public class XMLAsset extends LinkedFileAsset {

	private List<String> requirePaths;
	
	public XMLAsset(File assetFile, AssetLocation assetLocation) {
		super(assetFile, assetLocation);
	}
	
	@Override
	public List<String> getRequirePaths() {

		if(haveFileContentsChanged() || requirePaths == null){
			try {
				Reader reader = getReader();
				XMLIdExtractor extractor = new XMLIdExtractor();
				requirePaths = extractor.getXMLIds(reader);

			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}

		return requirePaths;
	}

}
