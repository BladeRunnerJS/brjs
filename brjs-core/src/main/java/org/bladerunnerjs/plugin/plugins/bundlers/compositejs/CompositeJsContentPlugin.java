package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.InputSource;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.MinifierPlugin;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class CompositeJsContentPlugin extends AbstractContentPlugin {
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
		
		if(bundleSet.getSourceModules().size() > 0) {
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		if (contentPath.formName.equals(DEV_BUNDLE_REQUEST) || contentPath.formName.equals(PROD_BUNDLE_REQUEST)) {
			String minifierSetting = contentPath.properties.get("minifier-setting");
			MinifierPlugin minifierPlugin = brjs.plugins().minifierPlugin(minifierSetting);
			
			RequestMode requestMode = (contentPath.formName.equals(PROD_BUNDLE_REQUEST)) ? RequestMode.Prod : RequestMode.Dev;
			
			List<InputSource> inputSources = getInputSourcesFromOtherBundlers(requestMode, contentPath, bundleSet, contentAccessor, version);
			ResponseContent content = new CharResponseContent( bundleSet.getBundlableNode().root(), minifierPlugin.minify(minifierSetting, inputSources) );
			
			closeInputSources(inputSources);
			
			return content;
		}
		else {
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}
	}
	
	@Override
	public boolean outputAllBundles()
	{
		return false;
	}
	
	private void closeInputSources(List<InputSource> inputSources)
	{
		for (InputSource input : inputSources) {
			try
			{
				input.getContentPluginReader().close();
			}
			catch (Exception e)
			{
			}
		}
	}
	
	private List<InputSource> getInputSourcesFromOtherBundlers(RequestMode requestMode, ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		List<InputSource> inputSources = new ArrayList<>();
		
		try {
			for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
				List<String> requestPaths = contentPlugin.getValidContentPaths(bundleSet, requestMode);
				ContentPathParser contentPathParser = contentPlugin.getContentPathParser();
				
				for(String requestPath : requestPaths) {
					ParsedContentPath parsedContentPath = contentPathParser.parse(requestPath);
					inputSources.add( new InputSource(parsedContentPath, contentPlugin, bundleSet, contentAccessor, version) );
				}
			}
		}
		catch (MalformedRequestException e) {
			throw new ContentProcessingException(e);
		}
		
		return inputSources;
	}
	
	
}
