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

/**
 * Any BRJS-specific entity that will be bundled and which may handle logical requests.
 */

public interface BundlableNode extends Node, AssetContainer {
	
	/**
	 * The method retrieves the resource specified by the require path.
	 * 
	 * @param requirePath the require path from which the resource will be retrieved
	 * @return the LinkedAsset object retrieved
	 * @throws RequirePathException if the require path is invalid or the resource is out of scope
	 */
	LinkedAsset getLinkedAsset(String requirePath) throws RequirePathException;
	
	/**
	 * The method retrieves the a List of {@link LinkedAsset} that are located within the current BundableNode.
	 * 
	 * @return the list of LinkedAssets objects retrieved
	 */
	List<LinkedAsset> seedAssets();
	
	/**
	 * The method retrieves the {@link BundleSet} corresponding to the current BundableNode for when it is necessary to deploy it. 
	 * 
	 * @return the BundleSet corresponding to the current BundableNode
	 * @throws ModelOperationException if any exceptions are thrown when retrieving the BundleSet
	 */
	BundleSet getBundleSet() throws ModelOperationException;
	
	/**
	 * The method represents a bundle request and will deliver the response in the form of bundled resources.
	 * 
	 * @param logicalRequestPath the path the request is being made for
	 * @param contentAccessor the output stream the content will be written to
	 * @param version the version of the bundle being retrieved
	 * @return the ResponseContent corresponding to the bundled resources
	 * @throws MalformedRequestException if the request does not adhere to the expected format
	 * @throws ResourceNotFoundException if the request may not be handled due to the indicated resource not being found
	 * @throws ContentProcessingException for non-BRJS-specific issues
	 */
	ResponseContent handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException;
	
	/**
	 * Handle a given request using the {@link BundleSet} provided.
	 * See #handleLogicalRequest(String logicalRequestPath, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException;
	 * 
	 * @param logicalRequestPath the path the request is being made for
	 * @param bundleSet handle the request using the provided BundleSet
	 * @param contentAccessor the output stream the content will be written to
	 * @param version the version of the bundle being retrieved
	 * @return the ResponseContent corresponding to the bundled resources
	 * @throws MalformedRequestException if the request does not adhere to the expected format
	 * @throws ResourceNotFoundException if the request may not be handled due to the indicated resource not being found
	 * @throws ContentProcessingException for non-BRJS-specific issues
	 */
	ResponseContent handleLogicalRequest(String logicalRequestPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException;
	
	/**
	 * The method returns all the {@link Asset}s that are required by the parameter {@link Asset} through the contained
	 * require paths.
	 * 
	 * @param asset the Asset object whose required Assets will be retrieved
	 * @param requirePaths the require paths to be resolved
	 * @return a list of Assets that are required by the parameter Asset
	 * @throws RequirePathException if the require path is invalid or ambiguous, out of scope, or may otherwise not be resolvable
	 */
	List<Asset> assets(Asset asset, List<String> requirePaths) throws RequirePathException;
}
