package org.bladerunnerjs.testing.specutility.engine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.StaticContentOutputStream;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;

public class BundlableNodeBuilder<N extends BundlableNode> extends AssetContainerBuilder<N> {
	private BundlableNode bundlableNode;
	
	public BundlableNodeBuilder(SpecTest specTest, N bundlableNode) {
		super(specTest, bundlableNode);
		this.bundlableNode = bundlableNode;
	}
	
	public BuilderChainer hasReceivedRequest(String requestPath) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, IOException {
		ContentPluginOutput contentOutputStream = new StaticContentOutputStream(bundlableNode.app(), new ByteArrayOutputStream() );
		bundlableNode.handleLogicalRequest(requestPath, contentOutputStream, bundlableNode.root().getAppVersionGenerator().getDevVersion());
		
		return builderChainer;
	}
}
