package org.bladerunnerjs.plugin.bundlers.unbundledresources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.Workbench;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.BinaryResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.api.BladesetWorkbench;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class UnbundledResourcesContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin
{

	private static final String FILE_PATH_REQUEST_FORM = "file-path";
	public static final String VERSIONED_UNBUNDLED_RESOURCES_REQUEST = "versioned-unbundled-resources-request";
	public static final String UNBUNDLED_RESOURCES_REQUEST = "unbundled-resources-request";
	public static final String UNBUNDLED_RESOURCES_DIRNAME = "unbundled-resources";
	public static final String BLADESET_VERSIONED_UNBUNDLED_RESOURCES_REQUEST = "bladeset-versioned-unbundled-resources-request";
	public static final String BLADESET_UNBUNDLED_RESOURCES_REQUEST = "bladeset-unbundled-resources-request";
	public static final String BLADE_VERSIONED_UNBUNDLED_RESOURCES_REQUEST = "blade-versioned-unbundled-resources-request";
	public static final String BLADE_UNBUNDLED_RESOURCES_REQUEST = "blade-unbundled-resources-request";
	public static final String BLADEWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST = "bladeworkbench-versioned-unbundled-resources-request";
	public static final String BLADEWORKBENCH_UNBUNDLED_RESOURCES_REQUEST = "bladeworkbench-unbundled-resources-request";
	public static final String BLADESETWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST = "bladesetworkbench-versioned-unbundled-resources-request";
	public static final String BLADESETWORKBENCH_UNBUNDLED_RESOURCES_REQUEST = "bladesetworkbench-unbundled-resources-request";
	
	private ContentPathParser contentPathParser;
	private BRJS brjs;

	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("unbundled-resources/bladeset_<bladeset>/blade_<blade>/workbench/<file-path>").as(BLADEWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST)
				.and("/unbundled-resources/bladeset_<bladeset>/blade_<blade>/workbench/<file-path>").as(BLADEWORKBENCH_UNBUNDLED_RESOURCES_REQUEST)
				.and("unbundled-resources/bladeset_<bladeset>/workbench/<file-path>").as(BLADESETWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST)
				.and("/unbundled-resources/bladeset_<bladeset>/workbench/<file-path>").as(BLADESETWORKBENCH_UNBUNDLED_RESOURCES_REQUEST)
				.and("unbundled-resources/bladeset_<bladeset>/blade_<blade>/<file-path>").as(BLADE_VERSIONED_UNBUNDLED_RESOURCES_REQUEST)
				.and("/unbundled-resources/bladeset_<bladeset>/blade_<blade>/<file-path>").as(BLADE_UNBUNDLED_RESOURCES_REQUEST)
				.and("unbundled-resources/bladeset_<bladeset>/<file-path>").as(BLADESET_VERSIONED_UNBUNDLED_RESOURCES_REQUEST)
				.and("/unbundled-resources/bladeset_<bladeset>/<file-path>").as(BLADESET_UNBUNDLED_RESOURCES_REQUEST)
				.and("unbundled-resources/<file-path>").as(VERSIONED_UNBUNDLED_RESOURCES_REQUEST)
				.and("/unbundled-resources/<file-path>").as(UNBUNDLED_RESOURCES_REQUEST)
			.where(FILE_PATH_REQUEST_FORM).hasForm(".*")
				.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN);

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
	public ContentPathParser getContentPathParser()
	{
		return contentPathParser;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException
	{
		try
		{
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			
    		if (parsedContentPath.formName.equals(UNBUNDLED_RESOURCES_REQUEST)
    				|| parsedContentPath.formName.equals(VERSIONED_UNBUNDLED_RESOURCES_REQUEST))
    		{
    			return getFileContents(bundleSet, parsedContentPath, contentAccessor, bundleSet.bundlableNode());
    		}
    		else if (parsedContentPath.formName.equals(BLADESET_UNBUNDLED_RESOURCES_REQUEST)
    				 || parsedContentPath.formName.equals(BLADESET_VERSIONED_UNBUNDLED_RESOURCES_REQUEST))
    		{    			
    			Bladeset bladeset = bundleSet.bundlableNode().app().bladeset(parsedContentPath.properties.get("bladeset"));
    			return getFileContents(bundleSet, parsedContentPath, contentAccessor, bladeset);
    		}
    		else if (parsedContentPath.formName.equals(BLADE_UNBUNDLED_RESOURCES_REQUEST)
   				 || parsedContentPath.formName.equals(BLADE_VERSIONED_UNBUNDLED_RESOURCES_REQUEST))
	   		{    			
	   			Blade blade = bundleSet.bundlableNode().app().bladeset(parsedContentPath.properties.get("bladeset")).blade(parsedContentPath.properties.get("blade"));
	   			return getFileContents(bundleSet, parsedContentPath, contentAccessor, blade);
	   		}
    		else if (parsedContentPath.formName.equals(BLADEWORKBENCH_UNBUNDLED_RESOURCES_REQUEST)
      				 || parsedContentPath.formName.equals(BLADEWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST))
   	   		{    			
   	   			Workbench<?> workbench = bundleSet.bundlableNode().app().bladeset(parsedContentPath.properties.get("bladeset")).blade(parsedContentPath.properties.get("blade")).workbench();
   	   			return getFileContents(bundleSet, parsedContentPath, contentAccessor, workbench);
   	   		}
    		else if (parsedContentPath.formName.equals(BLADESETWORKBENCH_UNBUNDLED_RESOURCES_REQUEST)
     				 || parsedContentPath.formName.equals(BLADESETWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST))
  	   		{    			
  	   			Workbench<?> workbench = bundleSet.bundlableNode().app().bladeset(parsedContentPath.properties.get("bladeset")).workbench();
  	   			return getFileContents(bundleSet, parsedContentPath, contentAccessor, workbench);
  	   		}
			else {
				throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
			}
		}
		catch (IOException e)
		{
			throw new ContentProcessingException(e);
		}
	}

	private ResponseContent getFileContents(BundleSet bundleSet, ParsedContentPath contentPath, 
			UrlContentAccessor contentAccessor,	AssetContainer assetContainer) throws ContentProcessingException, IOException {
		String relativeFilePath = contentPath.properties.get(FILE_PATH_REQUEST_FORM);
		MemoizedFile unbundledResourcesDir = assetContainer.file(UNBUNDLED_RESOURCES_DIRNAME);
		App app = bundleSet.bundlableNode().app();
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

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		List<String> requestPaths = new ArrayList<String>();
		for(AssetContainer assetContainer : bundleSet.bundlableNode().scopeAssetContainers()) {
			requestPaths.addAll(createRequest(assetContainer));
		}
		return requestPaths;
	}
	
	private List<String> createRequest(AssetContainer assetContainer) throws ContentProcessingException 
	{
		List<String> requestPaths = new ArrayList<String>();
		MemoizedFile unbundledResourcesDir = assetContainer.file(UNBUNDLED_RESOURCES_DIRNAME);
		if (!unbundledResourcesDir.isDirectory())
			return requestPaths;
		try
		{
			for (MemoizedFile file : brjs.getMemoizedFile(unbundledResourcesDir).nestedFiles())
			{
				String relativePath = unbundledResourcesDir.getRelativePath(file);
				if (assetContainer instanceof Aspect)
				{
	    			requestPaths.add( contentPathParser.createRequest(UNBUNDLED_RESOURCES_REQUEST, relativePath) );
	    			requestPaths.add( contentPathParser.createRequest(VERSIONED_UNBUNDLED_RESOURCES_REQUEST, relativePath) );
				}
				if (assetContainer instanceof Bladeset)
				{
					Bladeset bladeset = (Bladeset) assetContainer;
	    			requestPaths.add( contentPathParser.createRequest(BLADESET_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), relativePath) );
	    			requestPaths.add( contentPathParser.createRequest(BLADESET_VERSIONED_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), relativePath) );
				}
				if (assetContainer instanceof Blade)
				{
					Blade blade = (Blade) assetContainer;
					Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
	    			requestPaths.add( contentPathParser.createRequest(BLADE_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), blade.getName(), relativePath) );
	    			requestPaths.add( contentPathParser.createRequest(BLADE_VERSIONED_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), blade.getName(), relativePath) );
				}
				if (assetContainer instanceof BladeWorkbench)
				{
					@SuppressWarnings("unchecked")
					Workbench<BladeWorkbench> workbench = (Workbench<BladeWorkbench>) assetContainer;
					Blade blade = brjs.locateAncestorNodeOfClass(workbench, Blade.class);
					Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
	    			requestPaths.add( contentPathParser.createRequest(BLADEWORKBENCH_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), blade.getName(), relativePath) );
	    			requestPaths.add( contentPathParser.createRequest(BLADEWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), blade.getName(), relativePath) );
				}
				if (assetContainer instanceof BladesetWorkbench)
				{
					@SuppressWarnings("unchecked")
					Workbench<BladesetWorkbench> workbench = (Workbench<BladesetWorkbench>) assetContainer;
					Bladeset bladeset = brjs.locateAncestorNodeOfClass(workbench, Bladeset.class);
	    			requestPaths.add( contentPathParser.createRequest(BLADESETWORKBENCH_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), relativePath) );
	    			requestPaths.add( contentPathParser.createRequest(BLADESETWORKBENCH_VERSIONED_UNBUNDLED_RESOURCES_REQUEST, bladeset.getName(), relativePath) );
				}
			}
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}
	
}
