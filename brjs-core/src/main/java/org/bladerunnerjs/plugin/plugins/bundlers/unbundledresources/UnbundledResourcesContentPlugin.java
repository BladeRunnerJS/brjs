package org.bladerunnerjs.plugin.plugins.bundlers.unbundledresources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.BinaryResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class UnbundledResourcesContentPlugin extends AbstractContentPlugin
{

	private static final String FILE_PATH_REQUEST_FORM = "file-path";
	public static final String VERSIONED_UNBUNDLED_RESOURCES_REQUEST = "versioned-unbundled-resources-request";
	public static final String UNBUNDLED_RESOURCES_REQUEST = "unbundled-resources-request";
	public static final String UNBUNDLED_RESOURCES_DIRNAME = "unbundled-resources";
	
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("unbundled-resources/<file-path>").as(VERSIONED_UNBUNDLED_RESOURCES_REQUEST)
				.and("/unbundled-resources/<file-path>").as(UNBUNDLED_RESOURCES_REQUEST)
			.where(FILE_PATH_REQUEST_FORM).hasForm(".*");

		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix()
	{
		return UNBUNDLED_RESOURCES_DIRNAME;
	}

	@Override
	public String getCompositeGroupName()
	{
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}

	@Override
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException
	{
		try
		{
    		if (contentPath.formName.equals(UNBUNDLED_RESOURCES_REQUEST)
    				|| contentPath.formName.equals(VERSIONED_UNBUNDLED_RESOURCES_REQUEST))
    		{
    			String relativeFilePath = contentPath.properties.get(FILE_PATH_REQUEST_FORM);
    			
    			MemoizedFile unbundledResourcesDir = bundleSet.getBundlableNode().file(UNBUNDLED_RESOURCES_DIRNAME);
    			App app = bundleSet.getBundlableNode().app();
    			MemoizedFile requestedFile = unbundledResourcesDir.file(relativeFilePath);
    			String requestedFilePathRelativeToApp = app.dir().getRelativePath(requestedFile);
    			
    			if (!requestedFile.isFile())
    			{
    				String requestedFilePathRelativeToRoot = app.dir().getParentFile().getRelativePath(requestedFile);
    				throw new ContentProcessingException("The requested unbundled resource at '"+requestedFilePathRelativeToRoot+"' does not exist or is not a file.");
    			}
				
    			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    			contentAccessor.handleRequest(requestedFilePathRelativeToApp, outputBuffer);
    			return new BinaryResponseContent( new ByteArrayInputStream(outputBuffer.toByteArray()) );
    		}
			else {
				throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
			}
		}
		catch (IOException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return calculatValidRequestPaths(bundleSet);
	}

	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException
	{
		return calculatValidRequestPaths(bundleSet);
	}

	private List<String> calculatValidRequestPaths(BundleSet bundleSet) throws ContentProcessingException
	{
		
		List<String> requestPaths = new ArrayList<String>();
		
		MemoizedFile unbundledResourcesDir = bundleSet.getBundlableNode().file(UNBUNDLED_RESOURCES_DIRNAME);
		
		if (!unbundledResourcesDir.isDirectory())
		{
			return requestPaths;
		}
		
		try
		{
			for (MemoizedFile file : brjs.getMemoizedFile(unbundledResourcesDir).nestedFiles())
			{
    			String relativePath = unbundledResourcesDir.getRelativePath(file);
    			requestPaths.add( contentPathParser.createRequest(UNBUNDLED_RESOURCES_REQUEST, relativePath) );
    			requestPaths.add( contentPathParser.createRequest(VERSIONED_UNBUNDLED_RESOURCES_REQUEST, relativePath) );
			}
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}
	
}
