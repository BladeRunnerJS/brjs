package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.LinkedFileAsset;

public class XMLAsset extends LinkedFileAsset {

	
	public XMLAsset(File assetFile, AssetLocation assetLocation) {
		super(assetFile, assetLocation);
	}
	
	@Override
	public List<String> getProvidedRequirePaths() {
		
		List<String> result = new ArrayList<String>();
		
		try {
			Reader reader = getReader();
			XMLIdExtractor extractor = new XMLIdExtractor();
			result = extractor.getXMLIds(reader);
			
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return result;
	}
	
}
