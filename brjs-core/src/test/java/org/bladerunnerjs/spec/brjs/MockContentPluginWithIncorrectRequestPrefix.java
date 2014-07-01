package org.bladerunnerjs.spec.brjs;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginUtility;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class MockContentPluginWithIncorrectRequestPrefix extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private List<String> requestPaths = new ArrayList<>();
	
	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("some/url/path").as("request");
			
			contentPathParser = contentPathParserBuilder.build();
			requestPaths.add(contentPathParser.createRequest("request"));
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
	public String getCompositeGroupName() {
		return null;
	}
	
	@Override
	public List<String> getPluginsThatMustAppearBeforeThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public List<String> getPluginsThatMustAppearAfterThisPlugin() {
		return new ArrayList<>();
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public Reader writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentPluginUtility os, String version) throws ContentProcessingException
	{
		return new StringReader( this.getClass().getCanonicalName() );
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return requestPaths;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return requestPaths;
	}

}
