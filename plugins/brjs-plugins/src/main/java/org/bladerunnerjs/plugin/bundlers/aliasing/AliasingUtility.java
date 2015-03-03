package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;

public class AliasingUtility
{
	
	private static Map<BundlableNode,AliasesFile> aliasFiles = new LinkedHashMap<>();
	private static Map<AssetContainer,Map<String,AliasDefinitionsFile>> aliasDefinitionFiles = new LinkedHashMap<>();
	
	public static AliasesFile aliasesFile(BundlableNode bundlableNode) {
		AliasesFile aliasesFile = aliasFiles.get(bundlableNode);
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(bundlableNode);
			aliasFiles.put(bundlableNode, aliasesFile);
		}
		return aliasesFile;
	}
	
	public static AliasDefinitionsFile aliasDefinitionsFile(AssetContainer assetContainer, String path) {
		if (!aliasDefinitionFiles.containsKey(assetContainer)) {
			aliasDefinitionFiles.put(assetContainer, new LinkedHashMap<String,AliasDefinitionsFile>());
		}
		Map<String,AliasDefinitionsFile> assetContainerAliasDefinitionFiles = aliasDefinitionFiles.get(assetContainer);
		
		AliasDefinitionsFile aliasDefinitionsFile = assetContainerAliasDefinitionFiles.get(path);
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, assetContainer.file(path));
			assetContainerAliasDefinitionFiles.put(path, aliasDefinitionsFile);
		}
		return aliasDefinitionsFile;
	}
	
	public static List<AliasDefinitionsFile> aliasDefinitionFiles(AssetContainer assetContainer) {
		List<AliasDefinitionsFile> aliasDefinitionFiles = new ArrayList<>();
		
		for ( MemoizedFile assetContainerAliasDir : Arrays.asList(assetContainer.file("src"), assetContainer.file("resources")) ) {
			List<MemoizedFile> aliasDirs = new ArrayList<>();
			aliasDirs.add(assetContainerAliasDir);
			aliasDirs.addAll(assetContainerAliasDir.nestedDirs());
			
			for (MemoizedFile aliasDefinitionsDir : aliasDirs) {
				AliasDefinitionsFile aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, aliasDefinitionsDir);
				if (aliasDefinitionsFile.getUnderlyingFile().isFile()) {
					aliasDefinitionFiles.add(aliasDefinitionsFile);
				}
			}
		}
		
		return aliasDefinitionFiles;
	}
	
}
