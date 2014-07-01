package org.bladerunnerjs.plugin.plugins.bundlers.compositejs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPluginOutput;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.StaticContentPluginOutput;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
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
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentPluginOutput os, String version) throws ContentProcessingException {
		if(contentPath.formName.equals(DEV_BUNDLE_REQUEST) || contentPath.formName.equals(PROD_BUNDLE_REQUEST)) {
			try {
				String minifierSetting = contentPath.properties.get("minifier-setting");
				MinifierPlugin minifierPlugin = brjs.plugins().minifierPlugin(minifierSetting);
				
				try(Writer writer = os.getWriter()) {
					List<InputSource> inputSources = getInputSourcesFromOtherBundlers(contentPath, bundleSet, version);
					minifierPlugin.minify(minifierSetting, inputSources, writer);
				}
			}
			catch(IOException  e) {
				throw new ContentProcessingException(e);
			}
			
		}
		else {
			throw new ContentProcessingException("unknown request form '" + contentPath.formName + "'.");
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
	
	private List<InputSource> getInputSourcesFromOtherBundlers(ParsedContentPath contentPath, BundleSet bundleSet, String version) throws ContentProcessingException {
		List<InputSource> inputSources = new ArrayList<>();
		
		try {
			String charsetName = brjs.bladerunnerConf().getBrowserCharacterEncoding();
			
			for(ContentPlugin contentPlugin : brjs.plugins().contentPlugins("text/javascript")) {
				List<String> requestPaths = (contentPath.formName.equals(DEV_BUNDLE_REQUEST)) ? contentPlugin.getValidDevContentPaths(bundleSet) :
					contentPlugin.getValidProdContentPaths(bundleSet);
				ContentPathParser contentPathParser = contentPlugin.getContentPathParser();
				
				for(String requestPath : requestPaths) {
					
					ParsedContentPath parsedContentPath = contentPathParser.parse(requestPath);
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					// TODO: we might want to make this ContentOutputStream the same as the one passed in so other content plugins can write dynamic content
					ContentPluginOutput pluginOutput = new StaticContentPluginOutput(bundleSet.getBundlableNode().app(), baos);
					
					contentPlugin.writeContent(parsedContentPath, bundleSet, pluginOutput, version);

					InputSource source = new InputSource(requestPath, baos.toString(charsetName), contentPlugin, bundleSet);
					source.setReader(pluginOutput.getReader());
					inputSources.add(source);
					
				}
			}
		}
		catch(ConfigException | IOException | MalformedRequestException e) {
			throw new ContentProcessingException(e);
		}
		
		return inputSources;
	}
}
