package org.bladerunnerjs.model;

import java.util.List;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.LinkedAsset;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasException;
import org.bladerunnerjs.api.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.engine.Node;


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
