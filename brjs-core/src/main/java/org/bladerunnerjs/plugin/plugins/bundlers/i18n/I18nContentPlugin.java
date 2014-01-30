package org.bladerunnerjs.plugin.plugins.bundlers.i18n;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class I18nContentPlugin extends AbstractContentPlugin
{
	private ContentPathParser contentPathParser;
	private List<String> requestPaths = new ArrayList<>();

	{
		try
		{
    		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
    		contentPathParserBuilder
    			.accepts("i18n/bundle.json").as("bundle-request")
    			.where("module").hasForm(".+");
    		
    		contentPathParser = contentPathParserBuilder.build();
    		requestPaths.add(contentPathParser.createRequest("bundle-request"));
		}
		catch (MalformedTokenException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		// TODO Auto-generated method stub

	}

	@Override
	public void close()
	{
		// TODO Auto-generated method stub

	}
	
	@Override
	public String getRequestPrefix()
	{
		return "i18n";
	}

	@Override
	public String getGroupName()
	{
		return null;
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		// TODO Auto-generated method stub

	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException
	{
		return requestPaths;
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, List<String> locales) throws BundlerProcessingException
	{
		return requestPaths;
	}

}
