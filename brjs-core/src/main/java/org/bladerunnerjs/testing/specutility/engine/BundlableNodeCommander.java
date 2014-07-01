package org.bladerunnerjs.testing.specutility.engine;

import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.StaticContentOutputStream;
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
				ContentPluginOutput contentOutputStream = new StaticContentOutputStream(bundlableNode.app(), responseOutput);
        		bundlableNode.handleLogicalRequest(requestPath, contentOutputStream, bundlableNode.root().getAppVersionGenerator().getDevVersion());
        		
        		Reader reader = contentOutputStream.getReader();
				if (reader != null){
					StringWriter writer = new StringWriter();
					IOUtils.copy(reader, writer);
					response.append(writer.toString());
				} else {
					response.append(responseOutput.toString(specTest.getActiveClientCharacterEncoding()));
				}
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer requestReceivedInProd(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
 				ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
				ContentPluginOutput contentOutputStream = new StaticContentOutputStream(bundlableNode.app(), responseOutput);
        		bundlableNode.handleLogicalRequest(requestPath, contentOutputStream, bundlableNode.root().getAppVersionGenerator().getProdVersion());
        		Reader reader = contentOutputStream.getReader();
        		if( reader == null){
        			response.append(responseOutput.toString(specTest.getActiveClientCharacterEncoding()));
        		}else{
        			Writer writer = new StringWriter();
        			IOUtils.copy(reader, writer);
        			response.append(writer.toString());
        		}
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
