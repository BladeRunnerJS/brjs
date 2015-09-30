package org.bladerunnerjs.plugin.bundlers.thirdparty;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.CompositeContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class ThirdpartyGlobalizationContentPlugin extends AbstractContentPlugin implements CompositeContentPlugin {
	private ContentPathParser contentPathParser;
	private List<String> requestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		try {
			ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
			contentPathParserBuilder
				.accepts("thirdparty-globalization/globalize.js").as("globalization-request");

			contentPathParser = contentPathParserBuilder.build();
			requestPaths.add(contentPathParser.createRequest("globalization-request"));
		}
		catch (MalformedTokenException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}

	@Override
	public String getRequestPrefix() {
		return "thirdparty-globalization";
	}

	@Override
	public String getCompositeGroupName() {
		return "text/javascript";
	}

	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}

	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
		return requestPaths;
	}

	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException, ResourceNotFoundException {
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);

		if (parsedContentPath.formName.equals("globalization-request")) {
			List<Reader> readerList = new ArrayList<Reader>();

			if(hasUnencapsulatedSourceModule(bundleSet)) {
				for(ThirdpartySourceModule sourceFile : bundleSet.sourceModules(ThirdpartySourceModule.class)) {
					if (sourceFile instanceof ThirdpartySourceModule) {
						ThirdpartySourceModule thirdpartyModule = (ThirdpartySourceModule) sourceFile;

						readerList.add(new StringReader("window." + thirdpartyModule.getGlobalisedName() +
							" = System.syncImport('"+thirdpartyModule.getPrimaryRequirePath()+"');\n"));
					}
				}
			}

			return new CharResponseContent( brjs, readerList );
		}
		else {
			throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
		}
	}

	private boolean hasUnencapsulatedSourceModule(BundleSet bundleSet)
	{
		for(SourceModule sourceFile : bundleSet.sourceModules())
		{
			if (sourceFile.isGlobalisedModule()) {
				return true;
			}
		}
		return false;
	}
}
