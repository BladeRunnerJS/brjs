package org.bladerunnerjs.plugin.bundlers.compositejs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.InputSource;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.MinifierPlugin;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class CompositeJsContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin {
	public static final String PROD_BUNDLE_REQUEST = "prod-bundle-request";
	public static final String DEV_BUNDLE_REQUEST = "dev-bundle-request";
	
	private ContentPathParser contentPathParser = (new ContentPathParserBuilder()).build();
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("js/dev/<minifier-setting>/bundle.js").as(DEV_BUNDLE_REQUEST)
				.and("js/prod/<minifier-setting>/bundle.js").as(PROD_BUNDLE_REQUEST)
			.where("minifier-setting").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix() {
		return "js";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {		
		List<String> requestPaths = new ArrayList<>();
		String requestFormName = (requestMode == RequestMode.Prod) ? PROD_BUNDLE_REQUEST : DEV_BUNDLE_REQUEST;
		
		if(bundleSet.getAssets(SourceModule.class).size() > 0) {
			try {
				for(MinifierPlugin minifier : brjs.plugins().minifierPlugins()) {
					for(String minifierSettingName : minifier.getSettingNames()) {
						requestPaths.add(contentPathParser.createRequest(requestFormName, minifierSettingName));
					}
				}
			}
			catch(MalformedTokenException e) {
				throw new ContentProcessingException(e);
			}
		}
		
		return requestPaths;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException, MalformedRequestException {
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
		
		if (parsedContentPath.formName.equals(DEV_BUNDLE_REQUEST) || parsedContentPath.formName.equals(PROD_BUNDLE_REQUEST)) {
			String minifierSetting = parsedContentPath.properties.get("minifier-setting");
			MinifierPlugin minifierPlugin = brjs.plugins().minifierPlugin(minifierSetting);
			
			RequestMode requestMode = (parsedContentPath.formName.equals(PROD_BUNDLE_REQUEST)) ? RequestMode.Prod : RequestMode.Dev;
			
			List<InputSource> inputSources = getInputSourcesFromOtherBundlers(requestMode, parsedContentPath, bundleSet, contentAccessor, version);
			ResponseContent content = new CharResponseContent( bundleSet.bundlableNode().root(), minifierPlugin.minify(minifierSetting, inputSources) );
			
			return content;
		}
		else {
			throw new ContentProcessingException("unknown request form '" + parsedContentPath.formName + "'.");
		}
	}
	
	@Override
	public boolean outputAllBundles()
	{
		return false;
	}
	
	private List<InputSource> getInputSourcesFromOtherBundlers(RequestMode requestMode, ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		List<InputSource> inputSources = new ArrayList<>();
		
		for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
			List<String> requestPaths = contentPlugin.getValidContentPaths(bundleSet, requestMode);
			
			for(String requestPath : requestPaths) {
				inputSources.add( new InputSource(requestPath, contentPlugin, bundleSet, contentAccessor, version) );
			}
		}
		
		return inputSources;
	}
	
	
}
