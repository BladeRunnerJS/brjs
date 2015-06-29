package org.bladerunnerjs.plugin.bundlers.favicon;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.BinaryResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class FavIconContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin {

	public static final String FAVICON_FILENAME = "favicon.ico";
	public static final String FAVICON_REQUEST = "favicon-request";
	
	private ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder.accepts("/favicon.ico").as(FAVICON_REQUEST);

		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public String getRequestPrefix() {
		return FAVICON_FILENAME;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor,
			String version) throws MalformedRequestException, ContentProcessingException, ResourceNotFoundException {
		
		try {
			ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
			
    		if (parsedContentPath.formName.equals(FAVICON_REQUEST)) {
    			return getFileContents(bundleSet.bundlableNode().app(), contentAccessor);
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

	private ResponseContent getFileContents(App app, UrlContentAccessor contentAccessor) throws ContentProcessingException, IOException, ResourceNotFoundException {
		try {
			ByteArrayOutputStream outputBuffer = new ByteArrayOutputStream();
			contentAccessor.handleRequest(FAVICON_FILENAME, outputBuffer);
			return new BinaryResponseContent( new ByteArrayInputStream(outputBuffer.toByteArray()) );
		} catch (FileNotFoundException ex) {
			throw new ResourceNotFoundException( String.format("No '%s' file was found for the app '%s'", FAVICON_FILENAME, app.getName()), ex );
		}
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
		App app = bundleSet.bundlableNode().app();
		if (app.file(FAVICON_FILENAME).isFile()) {
			List<String> validContentPaths = new ArrayList<>();
			try
			{
				validContentPaths.add( contentPathParser.createRequest(FAVICON_REQUEST) );
			}
			catch (MalformedTokenException ex)
			{
				throw new ContentProcessingException(ex);
			}
			return validContentPaths;
		}
		return Collections.emptyList();
	}

	@Override
	public void setBRJS(BRJS brjs) {
	}

	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}

}
