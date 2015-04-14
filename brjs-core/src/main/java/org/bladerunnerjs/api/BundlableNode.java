package org.bladerunnerjs.api;

import java.util.List;

import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.engine.Node;


public interface BundlableNode extends Node, AssetContainer {
	LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException;
	List<LinkedAsset> seedAssets();
	
	BundleSet getBundleSet() throws ModelOperationException;
	
	ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException;
	List<Asset> assets(Asset asset, List<String> requirePaths) throws RequirePathException;
}
