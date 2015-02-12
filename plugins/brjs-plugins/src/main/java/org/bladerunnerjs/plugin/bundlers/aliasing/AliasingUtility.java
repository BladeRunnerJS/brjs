package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BundlableNode;

public class AliasingUtility
{
	
	private static Map<BundlableNode,AliasesFile> aliasFiles = new HashMap<>();
	private static Map<AssetContainer,Map<String,AliasDefinitionsFile>> aliasDefinitionFiles = new HashMap<>();
	
	public static AliasesFile aliasesFile(BundlableNode bundlableNode) {
		AliasesFile aliasesFile = aliasFiles.get(bundlableNode);
		if(aliasesFile == null) {
			aliasesFile = new AliasesFile(bundlableNode.file("resources/aliases.xml"), bundlableNode);
			aliasFiles.put(bundlableNode, aliasesFile);
		}
		return aliasesFile;
	}
	
	public static AliasDefinitionsFile aliasDefinitionsFile(AssetContainer assetContainer, String path) {
		if (!aliasDefinitionFiles.containsKey(assetContainer)) {
			aliasDefinitionFiles.put(assetContainer, new HashMap<String,AliasDefinitionsFile>());
		}
		Map<String,AliasDefinitionsFile> assetContainerAliasDefinitionFiles = aliasDefinitionFiles.get(assetContainer);
		
		AliasDefinitionsFile aliasDefinitionsFile = assetContainerAliasDefinitionFiles.get(path);
		if(aliasDefinitionsFile == null) {
			aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, assetContainer.file(path+"/resources/aliases.xml"));
			assetContainerAliasDefinitionFiles.put(path, aliasDefinitionsFile);
		}
		return aliasDefinitionsFile;
	}
	
}
