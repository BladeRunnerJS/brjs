package org.bladerunnerjs.plugin.bundlers.xml;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.LinkedFileAsset;

public class XMLAsset extends LinkedFileAsset {
	private final MemoizedValue<List<String>> requirePaths;
	private AssetContainer assetContainer;
	
	public XMLAsset(MemoizedFile htmlFile, AssetContainer assetContainer, String requirePrefix, List<Asset> implicitDependencies)
	{
		super(htmlFile, assetContainer,requirePrefix, implicitDependencies);
		this.assetContainer = assetContainer;
		requirePaths = new MemoizedValue<>("XMLAsset.requirePaths", assetContainer.root(), htmlFile);
	}

	@Override
	public List<String> getRequirePaths() {
		try {
			List<String> calculatedRequirePaths = requirePaths.value(() -> {
				String idRequirePrefix = (assetContainer.isNamespaceEnforced()) ? assetContainer.requirePrefix() : assetContainer.app().getRequirePrefix();
    			String idNamespace = idRequirePrefix.replace("/", ".");
				Reader reader = getReader();
				XMLIdExtractor extractor = new XMLIdExtractor();
				List<String> extractedIds = extractor.getXMLIds(reader);
				List<String> extractedRequirePaths = new ArrayList<>();
				for (String id : extractedIds) {
					if (id.startsWith(idNamespace) || id.startsWith(idRequirePrefix)) {
						extractedRequirePaths.add(id);
					}
				}
				return extractedRequirePaths;
			});
			calculatedRequirePaths.add(getPrimaryRequirePath());
			return calculatedRequirePaths;
		}
		catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static String calculateRequirePath(String requirePrefix, MemoizedFile xmlFile)
	{
		return LinkedFileAsset.calculateRequirePath(requirePrefix, xmlFile);
	}
}
