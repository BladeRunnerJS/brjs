package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
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
	public String getCompositeGroupName() {
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		return generateRequiredRequestPaths(bundleSet, DEV_BUNDLE_REQUEST, locales);
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		return generateRequiredRequestPaths(bundleSet, PROD_BUNDLE_REQUEST, locales);
	}
	
	@Override
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		if (contentPath.formName.equals(DEV_BUNDLE_REQUEST) || contentPath.formName.equals(PROD_BUNDLE_REQUEST)) {
			String minifierSetting = contentPath.properties.get("minifier-setting");
			MinifierPlugin minifierPlugin = brjs.plugins().minifierPlugin(minifierSetting);
			
			List<InputSource> inputSources = getInputSourcesFromOtherBundlers(contentPath, bundleSet, contentAccessor, version);
			ResponseContent content = new CharResponseContent( bundleSet.getBundlableNode().root(), minifierPlugin.minify(minifierSetting, inputSources) );
			
			closeInputSources(inputSources);
			
			return content;
		}
		else {
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
		}
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

	private List<String> generateRequiredRequestPaths(BundleSet bundleSet, String requestFormName, Locale... locales) throws ContentProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		if(bundleSet.getSourceModules().size() > 0) {
			// TODO: we need to be able to determine which minifier is actually in use so we don't need to create lots of redundant bundles
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
	
	private List<InputSource> getInputSourcesFromOtherBundlers(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		List<InputSource> inputSources = new ArrayList<>();
		
		try {
			for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
				List<String> requestPaths = (contentPath.formName.equals(DEV_BUNDLE_REQUEST)) ? contentPlugin.getValidDevContentPaths(bundleSet) :
					contentPlugin.getValidProdContentPaths(bundleSet);
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
