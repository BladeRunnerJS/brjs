package org.bladerunnerjs.plugin.bundlers.favicon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
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
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class FavIconContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin {

	public static final String FAVICON_FILE = "favicon.ico";
	public static final String VERSIONED_FAVICON_REQUEST = "versioned-favicon-request";
	public static final String FAVICON_REQUEST = "favicon-request";
	
	private ContentPathParser contentPathParser;
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("favicon.ico").as(VERSIONED_FAVICON_REQUEST)
				.and("/favicon.ico").as(FAVICON_REQUEST);

		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public String getRequestPrefix() {
		return FAVICON_FILE;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor,
			String version) throws MalformedRequestException, ContentProcessingException {
		try {
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			
    		if (parsedContentPath.formName.equals(FAVICON_REQUEST) || parsedContentPath.formName.equals(VERSIONED_FAVICON_REQUEST)) {
    			return getFileContents(bundleSet, parsedContentPath, contentAccessor, bundleSet.bundlableNode());
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

	private ResponseContent getFileContents(BundleSet bundleSet, ParsedContentPath contentPath,	UrlContentAccessor contentAccessor, 
			AssetContainer assetContainer) throws ContentProcessingException, IOException {
		MemoizedFile faviconFile = assetContainer.file(FAVICON_FILE);
		App app = bundleSet.bundlableNode().app();
		String requestedFilePathRelativeToApp = app.dir().getRelativePath(faviconFile);
		if (!faviconFile.isFile())
		{
			String requestedFilePathRelativeToRoot = app.dir().getParentFile().getRelativePath(faviconFile);
			throw new ContentProcessingException("The requested 'favicon.ico' at '"+requestedFilePathRelativeToRoot+"' does not exist or is not a file.");
		}	
		ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
		contentAccessor.handleRequest(requestedFilePathRelativeToApp, outputBuffer);
		return new BinaryResponseContent( new ByteArrayInputStream(outputBuffer.toByteArray()) );
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
		List<String> requestPaths = new ArrayList<String>();
		for(AssetContainer assetContainer : bundleSet.bundlableNode().scopeAssetContainers()) {
			requestPaths.addAll(createRequest(assetContainer));
		}
		return requestPaths;
	}
	
	private List<String> createRequest(AssetContainer assetContainer) throws ContentProcessingException {
		List<String> requestPaths = new ArrayList<String>();
		MemoizedFile faviconFile = assetContainer.file(FAVICON_FILE);
		if (!faviconFile.isFile()) {
			return requestPaths;
		}
		try	{
			String relativePath = faviconFile.getRelativePath(faviconFile);
			if (assetContainer instanceof Aspect) {
	    		requestPaths.add( contentPathParser.createRequest(FAVICON_REQUEST, relativePath) );
	    		requestPaths.add( contentPathParser.createRequest(VERSIONED_FAVICON_REQUEST, relativePath) );
			}
		}
		catch (MalformedTokenException e)
		{
			throw new ContentProcessingException(e);
		}
		return requestPaths;
	}

	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}

	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}

}
