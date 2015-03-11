package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.api.BundlableNode;

public class AliasingUtility
{
	
	public static AliasesFile aliasesFile(BundlableNode bundlableNode) {
		NodeProperties nodeProperties = bundlableNode.nodeProperties(AliasingUtility.class.getSimpleName());
		String aliasesPropertyName = AliasesFile.class.getSimpleName();
		Object aliasesProperty = nodeProperties.getTransientProperty(aliasesPropertyName);
		if (aliasesProperty instanceof AliasesFile) {
			return (AliasesFile) aliasesProperty;
		} else {
			AliasesFile aliasesFile = new AliasesFile(bundlableNode);
			nodeProperties.setTransientProperty(aliasesPropertyName, aliasesFile);
			return aliasesFile;
		}
	}
	
	public static AliasDefinitionsFile aliasDefinitionsFile(AssetContainer assetContainer, String path) {
		NodeProperties nodeProperties = assetContainer.nodeProperties(AliasingUtility.class.getSimpleName());
		String aliasDefintionsPropertyName = AliasDefinitionsFile.class.getSimpleName()+"_"+path;
		Object aliasDefinitionsProperty = nodeProperties.getTransientProperty(aliasDefintionsPropertyName);
		if (aliasDefinitionsProperty instanceof AliasDefinitionsFile) {
			return (AliasDefinitionsFile) aliasDefinitionsProperty;
		} else {
			AliasDefinitionsFile aliasDefinitionsFile = new AliasDefinitionsFile(assetContainer, assetContainer.file(path));
			nodeProperties.setTransientProperty(aliasDefintionsPropertyName, aliasDefinitionsFile);
			return aliasDefinitionsFile;
		}
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
	
	public static List<AliasDefinitionsFile> scopeAliasDefinitionFiles(BundlableNode bundlableNode) {
		List<AliasDefinitionsFile> scopeAliasDefinitions = new ArrayList<>();
		for (AssetContainer scopeAssetContainer : bundlableNode.scopeAssetContainers()) {
			scopeAliasDefinitions.addAll( aliasDefinitionFiles(scopeAssetContainer) );
		}
		return scopeAliasDefinitions;
	}
	
}
