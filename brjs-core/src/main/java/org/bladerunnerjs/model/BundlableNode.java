package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;


public interface BundlableNode extends Node, AssetContainer {
	AliasesFile aliasesFile();
	SourceModule getSourceModule(String requirePath) throws RequirePathException;
	List<LinkedAsset> seedFiles();
	
	/**
	 * Returns all AssetContainers that contain resources that can potentially be bundled for this BundleableNode
	 * @return
	 */
	List<AssetContainer> getAssetContainers();
	BundleSet getBundleSet() throws ModelOperationException;
	AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException;
	List<AliasDefinitionsFile> getAliasDefinitionFiles();
}
