package org.bladerunnerjs.api.spec.engine;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.StaticContentAccessor;


public abstract class BundlableNodeCommander<N extends BundlableNode> extends NodeCommander<N>
{
	private BundlableNode bundlableNode;
	
	public BundlableNodeCommander(SpecTest specTest, N bundlableNode)
	{
		super(specTest, bundlableNode);
		this.bundlableNode = bundlableNode;
	}
	
	public CommanderChainer requestReceivedInDev(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				ResponseContent content = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getDevVersion());        		
				ByteArrayOutputStream pluginContent = new ByteArrayOutputStream();
        		content.write(pluginContent);
        		response.append(pluginContent.toString(BladerunnerConf.OUTPUT_ENCODING));
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceivedInDev(final String requestPath, final OutputStream response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				ResponseContent content = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getDevVersion());
        		content.write(response);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceivedInProd(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				ResponseContent content = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getProdVersion());
        		ByteArrayOutputStream pluginContent = new ByteArrayOutputStream();
        		content.write(pluginContent);
        		response.append(pluginContent);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceivedInProd(final String requestPath, final OutputStream response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				ResponseContent content = bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getProdVersion());
        		content.write(response);
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
