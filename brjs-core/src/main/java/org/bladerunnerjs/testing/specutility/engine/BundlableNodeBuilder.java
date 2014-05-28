package org.bladerunnerjs.testing.specutility.engine;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;

public class BundlableNodeBuilder<N extends BundlableNode> extends AssetContainerBuilder<N> {
	private BundlableNode bundlableNode;
	
	public BundlableNodeBuilder(SpecTest specTest, N bundlableNode) {
		super(specTest, bundlableNode);
		this.bundlableNode = bundlableNode;
	}
	
	public BuilderChainer hasReceivedRequest(String requestPath) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		bundlableNode.handleLogicalRequest(requestPath, responseOutput);
		
		return builderChainer;
	}
}
