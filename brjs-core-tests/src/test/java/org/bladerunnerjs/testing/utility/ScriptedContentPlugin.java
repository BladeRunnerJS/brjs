package org.bladerunnerjs.testing.utility;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class ScriptedContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin
{
	private ContentPathParser contentPathParser;
	private List<String> requestPaths = new ArrayList<>();
	private boolean outputAllBundles;
	
	public ScriptedContentPlugin(boolean outputAllBundles, String... urls) {
		this.outputAllBundles = outputAllBundles;
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		for (String url : urls) {
    		contentPathParserBuilder.accepts(url).as(url);
			requestPaths.add(url);
		}
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getRequestPrefix() {
		return "ScriptedContentPlugin";
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException
	{
		return new CharResponseContent( bundleSet.bundlableNode().root(), this.getClass().getCanonicalName() );
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		return requestPaths;
	}
	
	@Override
	public boolean outputAllBundles()
	{
		return outputAllBundles;
	}

}
