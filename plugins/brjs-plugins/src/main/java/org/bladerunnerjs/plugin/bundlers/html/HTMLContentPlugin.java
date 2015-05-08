package org.bladerunnerjs.plugin.bundlers.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;


public class HTMLContentPlugin extends AbstractContentPlugin
{
	private final List<String> requestPaths = new ArrayList<>();
	
	private BRJS brjs;
	
	{
		requestPaths.add("html/bundle.html");
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix() {
		return "html";
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		return (bundleSet.assets("html!").isEmpty()) ? Collections.emptyList() : requestPaths;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
	{
		if(!contentPath.equals(requestPaths.get(0))) {
			throw new MalformedRequestException(contentPath, "Requests must be for exactly '" + requestPaths.get(0) + "'.");
		}
		
		return new CharResponseContent( brjs, HTMLTemplateUtility.getReaders(bundleSet, version) );
	}
}
