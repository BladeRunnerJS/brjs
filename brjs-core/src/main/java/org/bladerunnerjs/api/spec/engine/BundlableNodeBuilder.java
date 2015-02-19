package org.bladerunnerjs.api.spec.engine;

import java.io.IOException;

import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.StaticContentAccessor;

public class BundlableNodeBuilder<N extends BundlableNode> extends AssetContainerBuilder<N> {
	private BundlableNode bundlableNode;
	
	public BundlableNodeBuilder(SpecTest specTest, N bundlableNode) {
		super(specTest, bundlableNode);
		this.bundlableNode = bundlableNode;
	}
	
	public BuilderChainer indexPageRequires(String requirePath) throws Exception {
		writeToFile(bundlableNode.file("index.html"), "require('"+requirePath+"');");
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageRefersTo(String... classNames) throws Exception  {
		writeToFile(bundlableNode.file("index.html"), generateStringClassReferencesContent(classNames));
		
		return builderChainer;
	}
	
	public BuilderChainer indexPageHasContent(String content) throws Exception {
		writeToFile(bundlableNode.file("index.html"), content);
		
		return builderChainer;
	}
	
	public BuilderChainer hasReceivedRequest(String requestPath) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, IOException {
		bundlableNode.handleLogicalRequest(requestPath, new StaticContentAccessor(bundlableNode.app()), bundlableNode.root().getAppVersionGenerator().getDevVersion());
		
		return builderChainer;
	}
	
	private String generateStringClassReferencesContent(String... classNames) {
		String content = "";
		
		for(String className : classNames) {
			if(className.contains("/")) {
				throw new RuntimeException("The '" + className + "' class name contains a slash. Did you mean to use indexPageRequires() instead?");
			}
			
			content += className + "\n";
		}
		
		return content;
	}
}
