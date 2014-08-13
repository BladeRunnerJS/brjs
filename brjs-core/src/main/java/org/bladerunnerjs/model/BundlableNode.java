package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasException;
import org.bladerunnerjs.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ResponseContent;


public interface BundlableNode extends Node, AssetContainer {
	AliasesFile aliasesFile();
	LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException;
	List<AssetLocation> seedAssetLocations();
	List<LinkedAsset> seedAssets();
	
	BundleSet getBundleSet() throws ModelOperationException;
	AliasDefinition getAlias(String aliasName) throws AliasException, ContentFileProcessingException;
	List<AliasDefinitionsFile> aliasDefinitionFiles();
	
	ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException;
	List<Asset> getLinkedAssets(AssetLocation assetLocation, List<String> requirePaths) throws RequirePathException;
}
