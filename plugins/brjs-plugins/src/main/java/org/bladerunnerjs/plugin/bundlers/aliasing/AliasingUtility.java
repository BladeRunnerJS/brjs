package org.bladerunnerjs.plugin.bundlers.aliasing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.bladerunnerjs.api.memoization.Getter;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.utility.UnicodeReader;
import org.bladerunnerjs.api.BundlableNode;

public class AliasingUtility
{
	
	public static AliasesFile aliasesFile(BundlableNode bundlableNode) {
		return getNodeProperty(bundlableNode, AliasesFile.class.getSimpleName(), AliasesFile.class, 
				() -> { return new AliasesFile(bundlableNode); });
	}
	
	public static AliasDefinitionsFile aliasDefinitionsFile(AssetContainer assetContainer, String path) {
		return getNodeProperty(assetContainer, AliasDefinitionsFile.class.getSimpleName()+"_"+path, AliasDefinitionsFile.class, 
				() -> { return new AliasDefinitionsFile(assetContainer, assetContainer.file(path)); });
	}
	
	@SuppressWarnings("unchecked")
	public static List<AliasDefinitionsFile> aliasDefinitionFiles(AssetContainer assetContainer) {
		MemoizedValue<List<AliasDefinitionsFile>> aliasDefinitionFilesValue = getNodeProperty(assetContainer, "memoizedAliasDefinitionFiles", MemoizedValue.class, 
				() -> { return new MemoizedValue<List<AliasDefinitionsFile>>(assetContainer.requirePrefix()+".aliasDefinitionFiles", assetContainer); });
				
		return aliasDefinitionFilesValue.value(() -> {
			List<AliasDefinitionsFile> aliasDefinitionFiles = new ArrayList<>();
			
			for ( MemoizedFile assetContainerAliasDir : Arrays.asList(assetContainer.file("src"), assetContainer.file("resources")) ) {
				List<MemoizedFile> aliasDirs = new ArrayList<>();
				aliasDirs.add(assetContainerAliasDir);
				aliasDirs.addAll(assetContainerAliasDir.nestedDirs());
				
				for (MemoizedFile aliasDefinitionsDir : aliasDirs) {
					AliasDefinitionsFile aliasDefinitionsFile = aliasDefinitionsFile(assetContainer, assetContainer.dir().getRelativePath(aliasDefinitionsDir));
					if (aliasDefinitionsFile.getUnderlyingFile().isFile()) {
						aliasDefinitionFiles.add(aliasDefinitionsFile);
					}
				} 
			}
			
			return aliasDefinitionFiles;
		});
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
	
	@SuppressWarnings("unchecked")
	static <OT extends Object> OT getNodeProperty(Node node, String propertyKey, Class<? extends OT> valueType, Getter<Exception> valueGetter) {
		NodeProperties nodeProperties = node.nodeProperties(AliasingUtility.class.getSimpleName());
		Object nodeProperty = nodeProperties.getTransientProperty(propertyKey);
		if (nodeProperty != null && nodeProperty.getClass().isAssignableFrom(valueType)) {
			return (OT) nodeProperty;
		} else {
			try {
				nodeProperty = valueGetter.get();
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
			nodeProperties.setTransientProperty(propertyKey, nodeProperty);
			return (OT) nodeProperty;
		}
	}
	
	
	public static boolean useLegacySchema(MemoizedFile aliaseFile, String defaultCharEncoding) throws IOException {
		LineIterator it = IOUtils.lineIterator( new UnicodeReader(aliaseFile, defaultCharEncoding) );
		for (int lineNumber = 0; it.hasNext() && lineNumber < 3; lineNumber++) {
			if (it.nextLine().contains("schema.caplin.com")) {
				return true;
			}
		}
		return false;
	}

}
