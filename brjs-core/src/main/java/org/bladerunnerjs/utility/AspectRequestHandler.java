package org.bladerunnerjs.utility;

import java.util.Map;

import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;


public class AspectRequestHandler
{

	private Aspect aspect;
	private AppRequestHandler appRequestHandler;

	public AspectRequestHandler(Aspect aspect) {
		this.aspect = aspect;
		this.appRequestHandler = new AppRequestHandler(aspect.app());
	}
	
	public String createRelativeBundleRequest(String contentPath, String version) throws MalformedTokenException
	{
		return appRequestHandler.createRelativeBundleRequest(contentPath, version);
	}
	
	public String createBundleRequest(String contentPath, String version) throws MalformedTokenException
	{
		return appRequestHandler.createBundleRequest(aspect, contentPath, version);
	}
	
	public String createLocaleForwardingRequest() throws MalformedTokenException
	{
		return appRequestHandler.createLocaleForwardingRequest(aspect);
	}
	
	public String createIndexPageRequest(Locale locale) throws MalformedTokenException
	{
		return appRequestHandler.createIndexPageRequest(aspect, locale);
	}
	
	public Map<String, Map<String, String>> getTagsAndAttributesFromIndexPage(Locale locale, UrlContentAccessor contentAccessor, RequestMode requestMode) throws ContentProcessingException, ResourceNotFoundException
	{
		return appRequestHandler.getTagsAndAttributesFromIndexPage(aspect, locale, contentAccessor, requestMode);
	}

	public ResponseContent getIndexPageContent(Locale locale, String version, UrlContentAccessor contentAccessor, RequestMode requestMode) throws ContentProcessingException, ResourceNotFoundException
	{
		return appRequestHandler.getIndexPageContent(aspect, locale, version, contentAccessor, requestMode);
	}

	public ResponseContent getLocaleForwardingPageContent(BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException
	{
		return appRequestHandler.getLocaleForwardingPageContent(bundleSet, contentAccessor, version);
	}
	
}
