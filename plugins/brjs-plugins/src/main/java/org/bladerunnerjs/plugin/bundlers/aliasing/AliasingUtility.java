package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
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

	public static List<AliasDefinition> aliases(BundlableNode bundlableNode)
	{
		try {
			AliasesFile aliasesFile = aliasesFile(bundlableNode);
			if (!aliasesFile.getUnderlyingFile().isFile()) {
				return Collections.emptyList();
			}
			List<AliasDefinition> aliasDefinitions = new ArrayList<>();
			for (AliasOverride aliasOverride : aliasesFile.aliasOverrides()) {
				aliasDefinitions.add( aliasesFile.getAlias(aliasOverride.getName()) );
			}
			return aliasDefinitions;
		}
		catch (ContentFileProcessingException | AliasException ex) {
			throw new RuntimeException(ex);
		}
	}

	
	public static List<AliasDefinition> aliases(AssetContainer assetContainer, MemoizedFile childDir)
	{
		return aliases(assetContainer, assetContainer.dir().getRelativePath(childDir));
	}
	
	
	public static List<AliasDefinition> aliases(AssetContainer assetContainer, String path)
	{
		try {
			AliasDefinitionsFile aliasDefinitionsFile = aliasDefinitionsFile(assetContainer, path);
			if (!aliasDefinitionsFile.getUnderlyingFile().isFile()) {
				return Collections.emptyList();
			}
			return aliasDefinitionsFile.aliases();
		}
		catch (ContentFileProcessingException ex) {
			throw new RuntimeException(ex);
		}
	}
	
}
