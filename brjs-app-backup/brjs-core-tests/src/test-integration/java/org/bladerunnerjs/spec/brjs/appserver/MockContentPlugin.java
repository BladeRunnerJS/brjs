package org.bladerunnerjs.spec.brjs.appserver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class MockContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin
{
	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("mock-content-plugin/").as("request")
					.and("mock-content-plugin/some/other/path/").as("long-request")
					.and("/mock-content-plugin/unversioned/url").as("unversioned-request");
			
			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest("request"));
			prodRequestPaths.add(contentPathParser.createRequest("long-request"));
		}
		catch(MalformedTokenException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getRequestPrefix() {
		return "mock-content-plugin";
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
		return Arrays.asList();
	}

}
