package org.bladerunnerjs.testing.specutility.engine;

import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.StaticContentAccessor;
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
				Reader reader = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getDevVersion());        		
				response.append( IOUtils.toString(reader) );
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceived(final String requestPath, final OutputStream response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				Reader reader = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getDevVersion());        		
				IOUtils.copy(reader, response);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceivedInProd(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				Reader reader = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getProdVersion());
        		response.append( IOUtils.toString(reader) );
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
