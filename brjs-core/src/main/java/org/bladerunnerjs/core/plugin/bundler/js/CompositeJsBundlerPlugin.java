package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.minifier.InputSource;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.core.plugin.bundler.js.MinifierSetting;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;



public class CompositeJsBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin {
	private ContentPathParser requestParser = (new RequestParserBuilder()).build();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("js/dev/<locale>/<minifier-setting>/bundle.js").as("dev-bundle-request")
				.and("js/prod/<locale>/<minifier-setting>/bundle.js").as("prod-bundle-request")
			.where("locale").hasForm("[a-z]{2}_[A-Z]{2}")
				.and("minifier-setting").hasForm("[a-z-]+");
		
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return "js.bundle";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(tagAttributes, true, bundleSet, locale, writer);
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		writeTagContent(tagAttributes, false, bundleSet, locale, writer);
	}
	
	@Override
	public String getRequestPrefix() {
		return "js";
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return requestParser;
	}
	
	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return generateRequiredRequestPaths(true, bundleSet, locale);
	}
	
	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return generateRequiredRequestPaths(false, bundleSet, locale);
	}
	
	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		if(request.formName.equals("dev-bundle-request") || request.formName.equals("prod-bundle-request")) {
			try {
				String minifierSetting = request.properties.get("minifier-setting");
				MinifierPlugin minifierPlugin = brjs.plugins().minifier(minifierSetting);
				
				try(Writer writer = new OutputStreamWriter(os)) {
					List<InputSource> inputSources = getInputSourcesFromOtherBundlers(request, bundleSet);
					minifierPlugin.minify(minifierSetting, inputSources, writer);
				}
			}
			catch(IOException e) {
				throw new BundlerProcessingException(e);
			}
			
		}
		else {
			throw new BundlerProcessingException("unknown request form '" + request.formName + "'.");
		}
	}
	
	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
	
	@Override
	public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
	
	@Override
	public List<AssetFile> getResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
	
	private void writeTagContent(Map<String, String> tagAttributes, boolean isDev, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		MinifierSetting minifierSettings = new MinifierSetting(tagAttributes);
		String minifierSetting = (isDev) ? minifierSettings.devSetting() : minifierSettings.prodSetting();
		
		if(minifierSetting.equals(MinifierSetting.SEPARATE_JS_FILES)) {
			for(BundlerPlugin bundlerPlugin : brjs.plugins().bundlers("text/javascript")) {
				if( !bundlerPlugin.equals(this) ) {
					if(isDev) {
						bundlerPlugin.writeDevTagContent(tagAttributes, bundleSet, locale, writer);
					}
					else {
						bundlerPlugin.writeProdTagContent(tagAttributes, bundleSet, locale, writer);
					}
				}
			}
		}
		else {
			String bundleRequestForm = (isDev) ? "dev-bundle-request" : "prod-bundle-request";
			
			writer.write("<script type='text/javascript' src='" + requestParser.createRequest(bundleRequestForm, locale, minifierSetting) + "'></script>\n");
		}
	}
	
	private List<String> generateRequiredRequestPaths(boolean isDev, BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(BundlerPlugin bundlerPlugin : brjs.plugins().bundlers("text/javascript")) {
			if( !bundlerPlugin.equals(this) ) {
				if(isDev) {
					requestPaths.addAll(bundlerPlugin.getValidDevRequestPaths(bundleSet, locale));
				}
				else {
					requestPaths.addAll(bundlerPlugin.getValidProdRequestPaths(bundleSet, locale));
				}
			}
		}
		
		return requestPaths;
	}
	
	private List<InputSource> getInputSourcesFromOtherBundlers(ParsedContentPath request, BundleSet bundleSet) throws BundlerProcessingException {
		List<InputSource> inputSources = new ArrayList<>();
		
		try {
			String charsetName = brjs.bladerunnerConf().getDefaultOutputEncoding();
			
			for(BundlerPlugin bundlerPlugin : brjs.plugins().bundlers("text/javascript")) {
				if( !bundlerPlugin.equals(this) ) {
					String locale = request.properties.get("locale");
					List<String> requestPaths = (request.formName.equals("dev-bundle-request")) ? bundlerPlugin.getValidDevRequestPaths(bundleSet, locale) :
						bundlerPlugin.getValidProdRequestPaths(bundleSet, locale);
					ContentPathParser requestParser = bundlerPlugin.getContentPathParser();
					
					for(String requestPath : requestPaths) {
						ParsedContentPath parsedRequest = requestParser.parse(requestPath);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
						bundlerPlugin.writeContent(parsedRequest, bundleSet, baos);
						inputSources.add(new InputSource(requestPath, baos.toString(charsetName), bundlerPlugin, bundleSet));
					}
				}
			}
		}
		catch(ConfigException | IOException | MalformedRequestException e) {
			throw new BundlerProcessingException(e);
		}
		
		return inputSources;
	}
}
