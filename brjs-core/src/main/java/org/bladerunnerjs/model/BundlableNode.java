package org.bladerunnerjs.model;

import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AmbiguousAliasException;
import org.bladerunnerjs.aliasing.IncompleteAliasException;
import org.bladerunnerjs.aliasing.UnresolvableAliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public interface BundlableNode extends Node, AssetContainer {
	AliasesFile aliasesFile();
	SourceModule getSourceModule(String requirePath) throws RequirePathException;
	List<LinkedAsset> seedFiles();
	
	/**
	 * Returns all AssetContainers that contain resources that can potentially be bundled for this BundleableNode
	 */
	List<AssetContainer> assetContainers();
	BundleSet getBundleSet() throws ModelOperationException;
	AliasDefinition getAlias(String aliasName) throws UnresolvableAliasException, AmbiguousAliasException, IncompleteAliasException, ContentFileProcessingException;
	List<AliasDefinitionsFile> aliasDefinitionFiles();
	
	void handleLogicalRequest(String logicalRequestPath, OutputStream os) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException;
}
