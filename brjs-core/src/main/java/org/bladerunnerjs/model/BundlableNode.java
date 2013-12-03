package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.model.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;


public interface BundlableNode extends Node, AssetContainer {
	AliasesFile aliasesFile();
	SourceModule getSourceFile(String requirePath) throws RequirePathException;
	List<LinkedAsset> seedFiles();
	List<AssetContainer> getAssetContainers();
	BundleSet getBundleSet() throws ModelOperationException;
	AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException;
	List<AliasDefinitionsFile> getAliasDefinitionFiles();
}
