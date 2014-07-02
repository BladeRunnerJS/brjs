package org.bladerunnerjs.plugin.plugins.bundlers.unbundledresources;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.RelativePathUtility;


public class UnbundledResourcesContentPlugin extends AbstractContentPlugin
{

	private static final String FILE_PATH_REQUEST_FORM = "file-path";
	public static final String UNBUNDLED_RESOURCES_REQUEST = "unbundled-resources-request";
	public static final String UNBUNDLED_RESOURCES_DIRNAME = "unbundled-resources";
	
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("unbundled-resources/<file-path>").as(UNBUNDLED_RESOURCES_REQUEST)
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
    		if (contentPath.formName.equals(UNBUNDLED_RESOURCES_REQUEST))
    		{
    			String relativeFilePath = contentPath.properties.get(FILE_PATH_REQUEST_FORM);
    			
    			File unbundledResourcesDir = bundleSet.getBundlableNode().file(UNBUNDLED_RESOURCES_DIRNAME);
    			App app = bundleSet.getBundlableNode().app();
    			File requestedFile = new File(unbundledResourcesDir, relativeFilePath);
    			String requestedFilePathRelativeToApp = RelativePathUtility.get(app.root(), app.dir(), requestedFile);
    			
    			if (!requestedFile.isFile())
    			{
    				String requestedFilePathRelativeToRoot = RelativePathUtility.get(brjs, app.dir().getParentFile(), requestedFile);
    				throw new ContentProcessingException("The requested unbundled resource at '"+requestedFilePathRelativeToRoot+"' does not exist or is not a file.");
    			}
				
    			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
    			contentAccessor.handleRequest(requestedFilePathRelativeToApp, outputBuffer);
    			return new CharResponseContent( brjs, outputBuffer.toString() );
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
		
		File unbundledResourcesDir = bundleSet.getBundlableNode().file(UNBUNDLED_RESOURCES_DIRNAME);
		
		if (!unbundledResourcesDir.isDirectory())
		{
			return requestPaths;
		}
		
		try
		{
			for (File file : brjs.getFileInfo(unbundledResourcesDir).nestedFiles())
			{
    			String relativePath = RelativePathUtility.get(brjs, unbundledResourcesDir, file);
    			String calculatedPath = contentPathParser.createRequest(UNBUNDLED_RESOURCES_REQUEST, relativePath);
    			requestPaths.add(calculatedPath);
			}
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		
		return requestPaths;
	}
	
}
