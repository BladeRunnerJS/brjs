package org.bladerunnerjs.plugin.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BrowsableNode;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.TagHandlerPlugin;
import org.bladerunnerjs.utility.AppRequestHandler;


/**
 * A specialization of {@link AbstractPlugin} for developers that need to implement {@link ContentPlugin}.
 */
public abstract class AbstractContentPlugin extends AbstractPlugin implements ContentPlugin {
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return Collections.emptyList();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return Collections.emptyList();
	}
	
	@Override
	public boolean outputAllBundles() {
		return true;
	}
	
	@Override
	public List<String> getDevContentPathsUsedFromBrowsableNode(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		try
		{
			return filterUnusedContentPaths( true, bundleSet, getValidDevContentPaths(bundleSet, locales), locales );
		}
		catch (ResourceNotFoundException | MalformedTokenException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	@Override
	public List<String> getProdContentPathsUsedFromBrowsableNode(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		try
		{
			return filterUnusedContentPaths( false, bundleSet, getValidProdContentPaths(bundleSet, locales), locales );
		}
		catch (ResourceNotFoundException | MalformedTokenException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	
	private List<String> filterUnusedContentPaths(boolean isDev, BundleSet bundleSet, List<String> contentPaths, Locale... locales) throws ContentProcessingException, ResourceNotFoundException, MalformedTokenException {
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		if (outputAllBundles() || !(bundlableNode instanceof BrowsableNode) || locales.length <= 0) {			
			return contentPaths;
		}
		
		App app = bundleSet.getBundlableNode().app();
		AppRequestHandler appRequestHandler = new AppRequestHandler(app);
		UrlContentAccessor urlContentAccessor = new StaticContentAccessor(app);
		Map<String,List<String>> contentPluginRequestsMap = new LinkedHashMap<>();
		for (Locale locale : locales) {
			calculateContentPathsUsedInBrowsableNode( isDev, (BrowsableNode) bundlableNode, locale, bundleSet, appRequestHandler, urlContentAccessor, contentPluginRequestsMap);
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
	
	private static void calculateContentPathsUsedInBrowsableNode(boolean isDev, BrowsableNode browsableNode, Locale locale, BundleSet bundleSet, AppRequestHandler appRequestHandler, UrlContentAccessor urlContentAccessor, Map<String, List<String>> contentPluginProdRequestsMap) throws ContentProcessingException, ResourceNotFoundException, MalformedTokenException
	{
		RequestMode requestMode = (isDev) ? RequestMode.Dev : RequestMode.Prod;
		Map<String,Map<String,String>> usedTagsAndAttributes = appRequestHandler.getTagsAndAttributesFromIndexPage(browsableNode, locale, urlContentAccessor, requestMode);		
		
		for (TagHandlerPlugin tagPlugin : browsableNode.app().root().plugins().tagHandlerPlugins()) {
			for (String contentPluginPrefix : tagPlugin.getDependentContentPluginRequestPrefixes()) {
				contentPluginProdRequestsMap.put(contentPluginPrefix, new ArrayList<String>());							
			}
		}
		
		for (String tag : usedTagsAndAttributes.keySet()) {
			TagHandlerPlugin tagPlugin = browsableNode.root().plugins().tagHandlerPlugin(tag);
			Map<String,String> tagAttributes = usedTagsAndAttributes.get(tag);
			List<String> generatedRequests = tagPlugin.getGeneratedProdContentPaths(tagAttributes, bundleSet, locale);
			for (String contentPluginPrefix : tagPlugin.getDependentContentPluginRequestPrefixes()) {
				contentPluginProdRequestsMap.get(contentPluginPrefix).addAll(generatedRequests);
			}
		}
	}
	
}
