package org.bladerunnerjs.api.plugin.base;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BrowsableNode;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.AppRequestHandler;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link ContentPlugin}.
 */
public abstract class AbstractContentPlugin extends AbstractPlugin implements ContentPlugin {

	public boolean outputAllBundles() {
		return true;
	}
	
	@Override
	public List<String> getUsedContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
		try
		{
			return filterUnusedContentPaths( requestMode, bundleSet, getValidContentPaths(bundleSet, requestMode, locales), locales );
		}
		catch (ResourceNotFoundException | MalformedTokenException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	
	private List<String> filterUnusedContentPaths(RequestMode requestMode, BundleSet bundleSet, List<String> contentPaths, Locale... locales) throws ContentProcessingException, ResourceNotFoundException, MalformedTokenException {
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		if (outputAllBundles() || !(bundlableNode instanceof BrowsableNode) || locales.length <= 0) {			
			return contentPaths;
		}
		
		App app = bundleSet.bundlableNode().app();
		AppRequestHandler appRequestHandler = new AppRequestHandler(app);
		UrlContentAccessor urlContentAccessor = new StaticContentAccessor(app);
		Map<String,List<String>> contentPluginRequestsMap = new LinkedHashMap<>();
		for (Locale locale : locales) {
			calculateContentPathsUsedInBrowsableNode( requestMode, (BrowsableNode) bundlableNode, locale, bundleSet, appRequestHandler, urlContentAccessor, contentPluginRequestsMap);
		}
				
		List<String> usedContentPathsForThisContentPlugin = new ArrayList<>();
		String requestPrefix = getRequestPrefix();
		
		for (String contentPath : contentPaths) {
			if (!contentPluginRequestsMap.containsKey(requestPrefix) || contentPluginRequestsMap.get(requestPrefix).contains(contentPath)) {
				usedContentPathsForThisContentPlugin.add(contentPath);
			}
		}
		
		return usedContentPathsForThisContentPlugin;
	}
	
	private static void calculateContentPathsUsedInBrowsableNode(RequestMode requestMode, BrowsableNode browsableNode, Locale locale, BundleSet bundleSet, AppRequestHandler appRequestHandler, UrlContentAccessor urlContentAccessor, Map<String, List<String>> contentPluginProdRequestsMap) throws ContentProcessingException, ResourceNotFoundException, MalformedTokenException
	{
		Map<String,Map<String,String>> usedTagsAndAttributes = appRequestHandler.getTagsAndAttributesFromIndexPage(browsableNode, locale, urlContentAccessor, requestMode);		
		
		for (TagHandlerPlugin tagPlugin : browsableNode.app().root().plugins().tagHandlerPlugins()) {
			for (String contentPluginPrefix : tagPlugin.usedContentPluginRequestPrefixes()) {
				contentPluginProdRequestsMap.put(contentPluginPrefix, new ArrayList<String>());							
			}
		}
		
		for (String tag : usedTagsAndAttributes.keySet()) {
			TagHandlerPlugin tagPlugin = browsableNode.root().plugins().tagHandlerPlugin(tag);
			Map<String,String> tagAttributes = usedTagsAndAttributes.get(tag);
			List<String> generatedRequests = tagPlugin.getGeneratedContentPaths(tagAttributes, bundleSet, requestMode, locale);
			for (String contentPluginPrefix : tagPlugin.usedContentPluginRequestPrefixes()) {
				contentPluginProdRequestsMap.get(contentPluginPrefix).addAll(generatedRequests);
			}
		}
	}
	
}
