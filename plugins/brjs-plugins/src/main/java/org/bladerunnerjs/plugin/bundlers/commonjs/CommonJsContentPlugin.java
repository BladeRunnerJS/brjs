package org.bladerunnerjs.plugin.bundlers.commonjs;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

public class CommonJsContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin
{
	private static final String SINGLE_MODULE_REQUEST = "single-module-request";
	private static final String BUNDLE_REQUEST = "bundle-request";
	
	private ContentPathParser contentPathParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		try
		{
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("common-js/bundle.js").as(BUNDLE_REQUEST)
					.and("common-js/module/<module>.js").as(SINGLE_MODULE_REQUEST)
				.where("module").hasForm(ContentPathParserBuilder.PATH_TOKEN);

			contentPathParser = contentPathParserBuilder.build();
			prodRequestPaths.add(contentPathParser.createRequest(BUNDLE_REQUEST));
		}
		catch (MalformedTokenException e)
		{
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}

	@Override
	public String getRequestPrefix()
	{
		return "common-js";
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
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<>();

		List<CommonJsSourceModule> commonJsSourceModules = bundleSet.getSourceModules(CommonJsSourceModule.class);
		
		if (requestMode == RequestMode.Prod) {
			return (commonJsSourceModules.isEmpty()) ? Collections.emptyList() : prodRequestPaths;
		}
		
		try
		{
			for (SourceModule sourceModule : commonJsSourceModules)
			{
				requestPaths.add(contentPathParser.createRequest(SINGLE_MODULE_REQUEST, sourceModule.getPrimaryRequirePath()));
			}
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}

		return requestPaths;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException
	{
		try
		{
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			
			if (parsedContentPath.formName.equals(SINGLE_MODULE_REQUEST))
			{
				SourceModule jsModule = (SourceModule)bundleSet.bundlableNode().getLinkedAsset(parsedContentPath.properties.get("module"));
				return new CharResponseContent(brjs, jsModule.getReader());
				
			}
			else if (parsedContentPath.formName.equals(BUNDLE_REQUEST))
			{
				List<Reader> readerList = new ArrayList<Reader>();
				for (SourceModule sourceModule : bundleSet.getSourceModules(Arrays.asList(CommonJsSourceModule.class)))
				{
					readerList.add(new StringReader("// " + sourceModule.getPrimaryRequirePath() + "\n"));
					readerList.add(sourceModule.getReader());
					readerList.add(new StringReader("\n\n"));
				}
				return new CharResponseContent( brjs, readerList );
			}
			else
			{
				throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
			}
		}
		catch (IOException | RequirePathException e)
		{
			throw new ContentProcessingException(e);
		}
	}
}
