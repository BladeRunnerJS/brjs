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
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BundlableNode;

public class AliasingUtility
{
	
	public static final String BR_UNKNOWN_CLASS_NAME = "br.UnknownClass";
	
	public static boolean useLegacySchema(MemoizedFile aliaseFile, String defaultCharEncoding) throws IOException {
		LineIterator it = IOUtils.lineIterator( new UnicodeReader(aliaseFile, defaultCharEncoding) );
		for (int lineNumber = 0; it.hasNext() && lineNumber < 3; lineNumber++) {
			if (it.nextLine().contains("schema.caplin.com")) {
				return true;
			}
		}
		return false;
	}
	
	/* Memoization Utilities */
	
	public static AliasesFile aliasesFile(AssetContainer assetContainer) {
		return getNodeProperty(assetContainer, AliasesFile.class.getSimpleName(), AliasesFile.class, 
				() -> { return new AliasesFile(assetContainer); });
	}
	
	public static AliasesFile aliasesFile(App app) {
		return getNodeProperty(app, AliasesFile.class.getSimpleName(), AliasesFile.class, 
				() -> { return new AliasesFile(app); });
	}
	
	public static AliasDefinitionsFile aliasDefinitionsFile(AssetContainer assetContainer, String path) {
		return getNodeProperty(assetContainer, AliasDefinitionsFile.class.getSimpleName()+"_"+path, AliasDefinitionsFile.class, 
				() -> { return new AliasDefinitionsFile(assetContainer, assetContainer.file(path)); });
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
	
	/* Aliasing Utils */
	
	public static List<AliasDefinition> aliases(BundlableNode bundlableNode) {
		List<AliasDefinition> aliasDefinitions = new ArrayList<>();
		List<AliasDefinition> aliasDefinitionsOnlyFromBundlableNode = aliases(bundlableNode, aliasesFile(bundlableNode));
		aliasDefinitions.addAll(aliasDefinitionsOnlyFromBundlableNode);
		
		List<AliasDefinition> aliasesFromAppAliases = aliases(bundlableNode, aliasesFile(bundlableNode.app()));
		
		for (AliasDefinition alias : aliasesFromAppAliases) {
			if (aliasDefinitionsOnlyFromBundlableNode.isEmpty()) {
				aliasDefinitions.add(alias);
				continue;
			}
			for (AliasDefinition aliasFromBundlableNode : aliasDefinitionsOnlyFromBundlableNode) {
				if (aliasFromBundlableNode.getName().equals(alias.getName())) {
					continue;
				}
				aliasDefinitions.add(alias);
			}
		}
		
		return aliasDefinitions;
	}
	
	public static List<AliasDefinition> aliases(AssetContainer assetContainer, MemoizedFile childDir)
	{
		try {
			String path = assetContainer.dir().getRelativePath(childDir);
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
	
	public static AliasDefinition resolveAlias(String aliasName, BundlableNode bundlableNode) throws AliasException, ContentFileProcessingException {
		AliasesFile bundlableNodeAliasesFile = aliasesFile(bundlableNode);
		AliasesFile appAliasesFile = aliasesFile(bundlableNode.app());
		
		AliasDefinition aliasDefinition = getAliasDefinitionsFileAlias(aliasName, bundlableNode, bundlableNodeAliasesFile, appAliasesFile);
		AliasOverride bundlableNodeAliasOverride = getAliasFileAliasOverride(aliasName, bundlableNodeAliasesFile);
		AliasOverride bundlableNodeGroupAliasOverride = getAliasFileGroupOverride(aliasName, bundlableNode, bundlableNodeAliasesFile, appAliasesFile);
		
		AliasOverride appAliasOverride = getAliasFileAliasOverride(aliasName, appAliasesFile);
		AliasOverride appGroupAliasOverride = getAliasFileGroupOverride(aliasName, bundlableNode, bundlableNodeAliasesFile, appAliasesFile);
		
		if ((bundlableNodeAliasOverride == null) && (bundlableNodeGroupAliasOverride != null)) {
			bundlableNodeAliasOverride = bundlableNodeGroupAliasOverride;
		}
		
		AliasOverride activeAliasOverride = null;
		
		if (bundlableNodeAliasOverride != null) {
			activeAliasOverride = bundlableNodeAliasOverride;
		} else if (bundlableNodeGroupAliasOverride != null) {
			activeAliasOverride = bundlableNodeAliasOverride; 
		} else if (appAliasOverride != null) {
			activeAliasOverride = appAliasOverride;
		} else if (appGroupAliasOverride != null) {
			activeAliasOverride = appGroupAliasOverride; 
		}
		
		if (aliasDefinition == null && activeAliasOverride == null) {
			throw new UnresolvableAliasException(bundlableNodeAliasesFile, aliasName);
		}
		
		if (aliasDefinition == null) {
			aliasDefinition = new AliasDefinition(activeAliasOverride.getName(), activeAliasOverride.getClassName(), null);
		}
		else if (activeAliasOverride != null) {
			aliasDefinition = new AliasDefinition(activeAliasOverride.getName(), activeAliasOverride.getClassName(), aliasDefinition.getInterfaceName());
		}
		
		if (aliasDefinition.getClassName() == null) {
			aliasDefinition = new AliasDefinition(aliasDefinition.getName(), BR_UNKNOWN_CLASS_NAME, aliasDefinition.getInterfaceName());
		}
		
		return aliasDefinition;
	}
	
	
	/* Private Methods */
	
	@SuppressWarnings("unchecked")
	private static List<AliasDefinitionsFile> aliasDefinitionFiles(AssetContainer assetContainer) {
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
	
	private static List<AliasDefinition> aliases(BundlableNode bundlableNode, AliasesFile aliasesFile) {
		List<AliasDefinition> aliasDefinitions = new ArrayList<>();
		try {
			for (AliasOverride appAliasOverride : aliasesFile.aliasOverrides()) {
				try {
					aliasDefinitions.add(resolveAlias(appAliasOverride.getName(), bundlableNode));
				}
				catch (UnresolvableAliasException ex) {
					// if unresolved don't add to alias definitions, because it could be for another bundlable node
				}
			}
		} catch (ContentFileProcessingException | AliasException e) {
			throw new RuntimeException(e);
		}
		
		return aliasDefinitions;
	}
	
	private static AliasOverride getAliasFileAliasOverride(String aliasName, AliasesFile aliasesFile) throws ContentFileProcessingException {
		AliasOverride aliasOverride = null;
		
		for(AliasOverride nextAliasOverride : aliasesFile.aliasOverrides()) {
			if(nextAliasOverride.getName().equals(aliasName)) {
				aliasOverride = nextAliasOverride;
				break;
			}
		}
		
		return aliasOverride;
	}
	
	private static AliasOverride getAliasFileGroupOverride(String aliasName, BundlableNode bundlableNode, AliasesFile bundlableNodeAliasesFile, AliasesFile appAliasesFile) throws ContentFileProcessingException, AmbiguousAliasException {
		AliasOverride aliasOverride = null;
		AliasesFile activeAliasFile = activeAliasesFile(bundlableNodeAliasesFile, appAliasesFile);
		List<String> groupNames = activeAliasFile.groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : AliasingUtility.scopeAliasDefinitionFiles(bundlableNode)) {
			AliasOverride nextAliasOverride = aliasDefinitionsFile.getGroupOverride(aliasName, groupNames);
			if(aliasOverride != null && nextAliasOverride != null) {
				throw new AmbiguousAliasException(activeAliasFile.getUnderlyingFile(), aliasName, groupNames);
			}
			
			if (nextAliasOverride != null)
			{
				aliasOverride = nextAliasOverride;
			}
		}
		
		return aliasOverride;
	}
	
	private static AliasDefinition getAliasDefinitionsFileAlias(String aliasName, BundlableNode bundlableNode, AliasesFile bundlableNodeAliasesFile, AliasesFile appAliasesFile) throws ContentFileProcessingException, AliasException {
		AliasDefinition aliasDefinition = null;
		AliasesFile activeAliasFile = activeAliasesFile(bundlableNodeAliasesFile, appAliasesFile);
		String scenarioName = activeAliasFile.scenarioName();
		List<String> groupNames = activeAliasFile.groupNames();
		
		for(AliasDefinitionsFile aliasDefinitionsFile : scopeAliasDefinitionFiles(bundlableNode)) {
			AliasDefinition nextAliasDefinition = aliasDefinitionsFile.getAliasDefinition(aliasName, scenarioName, groupNames);
			
			if (nextAliasDefinition != null)
			{    			
    			if (aliasDefinition != null && nextAliasDefinition != null) {
    				throw new AmbiguousAliasException(activeAliasFile.getUnderlyingFile(), aliasName, scenarioName);
    			}
			
				aliasDefinition = nextAliasDefinition;
			}
		}
		
		return aliasDefinition;
	}
	
	private static List<AliasDefinitionsFile> scopeAliasDefinitionFiles(BundlableNode bundlableNode) {
		List<AliasDefinitionsFile> scopeAliasDefinitions = new ArrayList<>();
		for (AssetContainer scopeAssetContainer : bundlableNode.scopeAssetContainers()) {
			scopeAliasDefinitions.addAll( aliasDefinitionFiles(scopeAssetContainer) );
		}
		return scopeAliasDefinitions;
	}
	
	private static AliasesFile activeAliasesFile(AliasesFile bundlableNodeAliasesFile, AliasesFile appAliasesFile) throws ContentFileProcessingException {
		return (bundlableNodeAliasesFile.getUnderlyingFile().exists()) ? bundlableNodeAliasesFile : appAliasesFile;
	}
	
}
