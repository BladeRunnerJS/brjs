package org.bladerunnerjs.plugin.bundlers.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.LinkedFileAsset;

public class XMLAsset extends LinkedFileAsset {
	private final MemoizedValue<List<String>> requirePaths;
	
	public XMLAsset(MemoizedFile htmlFile, AssetContainer assetContainer, String requirePrefix)
	{
		super(htmlFile, assetContainer,requirePrefix);
		requirePaths = new MemoizedValue<>("XMLAsset.requirePaths", assetContainer.root(), htmlFile);
	}

	@Override
	public List<String> getRequirePaths() {
		try {
			return requirePaths.value(() -> {
				Reader reader = getReader();
				XMLIdExtractor extractor = new XMLIdExtractor();
				return extractor.getXMLIds(reader);
			});
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}
}
