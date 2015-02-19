package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import java.util.List;

import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.RequestMode;

import com.google.common.base.Joiner;

public class BundlableNodeVerifier<T extends BundlableNode> extends NodeVerifier<T> {
	private T bundlableNode;
	
	public BundlableNodeVerifier(SpecTest specTest, T bundlableNode) {
		super(specTest, bundlableNode);
		this.bundlableNode = bundlableNode;
	}
	
	public VerifierChainer prodAndDevRequestsForContentPluginsAreEmpty(String contentPluginPrefix) throws Exception {
		verifyProdAndDevRequestsForContentPluginsAre(contentPluginPrefix);
		
		return verifierChainer;
	}
	
	public VerifierChainer prodAndDevRequestsForContentPluginsAre(String contentPluginPrefix, String... expectedRequests) throws Exception {
		if(expectedRequests.length == 0) throw new RuntimeException("Use prodAndDevRequestsForContentPluginsAreEmpty() if there are no expected requests");
		
		verifyProdAndDevRequestsForContentPluginsAre(contentPluginPrefix, expectedRequests);
		
		return verifierChainer;
	}
	
	private VerifierChainer verifyProdAndDevRequestsForContentPluginsAre(String contentPluginPrefix, String... expectedRequests) throws Exception {
		ContentPlugin contentPlugin = bundlableNode.root().plugins().contentPlugin(contentPluginPrefix);
		List<String> actualDevRequests = contentPlugin.getValidContentPaths(bundlableNode.getBundleSet(), RequestMode.Dev, bundlableNode.app().appConf().getLocales());
		List<String> actualProdRequests = contentPlugin.getValidContentPaths(bundlableNode.getBundleSet(), RequestMode.Prod, bundlableNode.app().appConf().getLocales());
		
		assertEquals("dev requests didn't match", Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualDevRequests));
		assertEquals("prod requests didn't match", Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualProdRequests));
		
		return verifierChainer;
	}
	
	public VerifierChainer devRequestsForContentPluginsAre(String contentPluginPrefix, String... expectedRequests) throws Exception {
		ContentPlugin contentPlugin = bundlableNode.root().plugins().contentPlugin(contentPluginPrefix);
		List<String> actualRequests = contentPlugin.getValidContentPaths(bundlableNode.getBundleSet(), RequestMode.Dev, bundlableNode.app().appConf().getLocales());
		
		assertEquals("dev requests didn't match", Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualRequests));
		
		return verifierChainer;
	}
	
	public VerifierChainer usedDevContentPathsForPluginsAre(String contentPluginPrefix, String... expectedRequests) throws Exception {
		ContentPlugin contentPlugin = bundlableNode.root().plugins().contentPlugin(contentPluginPrefix);
		List<String> actualRequests = contentPlugin.getUsedContentPaths(bundlableNode.getBundleSet(), RequestMode.Dev, bundlableNode.app().appConf().getLocales());
		
		assertEquals("dev requests didn't match", Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualRequests));
		
		return verifierChainer;
	}
	
	public VerifierChainer prodRequestsForContentPluginsAre(String contentPluginPrefix, String... expectedRequests) throws Exception {
		ContentPlugin contentPlugin = bundlableNode.root().plugins().contentPlugin(contentPluginPrefix);
		List<String> actualRequests = contentPlugin.getValidContentPaths(bundlableNode.getBundleSet(), RequestMode.Prod, bundlableNode.app().appConf().getLocales());
		
		assertEquals("prod requests didn't match", Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualRequests));
		
		return verifierChainer;
	}
	
	public VerifierChainer usedProdContentPathsForPluginsAre(String contentPluginPrefix, String... expectedRequests) throws Exception {
		ContentPlugin contentPlugin = bundlableNode.root().plugins().contentPlugin(contentPluginPrefix);
		List<String> actualRequests = contentPlugin.getUsedContentPaths(bundlableNode.getBundleSet(), RequestMode.Prod, bundlableNode.app().appConf().getLocales());
		
		assertEquals("prod requests didn't match", Joiner.on(", ").join(expectedRequests), Joiner.on(", ").join(actualRequests));
		
		return verifierChainer;
	}
}
