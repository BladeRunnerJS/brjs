package org.bladerunnerjs.plugin.bundlers.appmeta;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.RequirePathException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class AppMetadataContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin
{
	private static final String APP_META_REQUEST = "app-meta-request";
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("app-meta/version.js").as(APP_META_REQUEST);
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "app-meta";
	}

	@Override
	public String getCompositeGroupName()
	{
		return "text/javascript";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException
	{
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
		
		if (parsedContentPath.formName.equals(APP_META_REQUEST))
		{
			try
			{
				SourceModule metaDataSourceModule = (SourceModule) bundleSet.bundlableNode().getLinkedAsset(AppMetadataSourceModule.APP_META_DATA);
				return new CharResponseContent(brjs, metaDataSourceModule.getReader());
			}
			catch (IOException | RequirePathException ex)
			{
				throw new ContentProcessingException(ex);
			}
		}
		else 
		{
			throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
		}
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		try
		{
			return Arrays.asList( contentPathParser.createRequest(APP_META_REQUEST) );
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
}
