package org.bladerunnerjs.testing.specutility.engine;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;


public abstract class BundlableNodeCommander<N extends BundlableNode> extends NodeCommander<N>
{
	
	
	private BundlableNode bundlableNode;


	public BundlableNodeCommander(SpecTest specTest, N bundlableNode)
	{
		super(specTest, bundlableNode);
		this.bundlableNode = bundlableNode;
	}
	
	
	public CommanderChainer requestReceived(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
        		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
        		bundlableNode.handleLogicalRequest(requestPath, responseOutput);
        		response.append(responseOutput.toString("UTF-8"));
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer bundleSetGenerated() {
		call(new Command() {
			public void call() throws Exception {
				bundlableNode.getBundleSet();
			}
		});
		
		return commanderChainer;
	}
		
}
