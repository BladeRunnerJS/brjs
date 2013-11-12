package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundlesource.FileSetFactory;
import org.bladerunnerjs.core.plugin.bundlesource.NullFileSetFactory;
import org.bladerunnerjs.core.plugin.minifier.InputSource;
import org.bladerunnerjs.core.plugin.minifier.MinifierPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedRequest;
import org.bladerunnerjs.model.RequestParser;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class CompositeJsBundlerPlugin implements BundlerPlugin {
	private RequestParser requestParser = (new RequestParserBuilder()).build();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder.accepts("js/dev/<locale>/<minifier-setting>/js.bundle").as("dev-bundle-request")
			.and("js/prod/<locale>/<minifier-setting>/js.bundle").as("prod-bundle-request")
			.where("locale").hasForm("[a-z]{2}_[A-Z]{2}");
		
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return "js";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		MinifierSetting minifierSetting = new MinifierSetting(tagAttributes);
		
		writeTagContent(true, tagAttributes, bundleSet, locale, writer, minifierSetting.prodSetting().equals("separate"));
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		MinifierSetting minifierSetting = new MinifierSetting(tagAttributes);
		
		writeTagContent(false, tagAttributes, bundleSet, locale, writer, minifierSetting.prodSetting().equals("separate"));
	}
	
	@Override
	public String getMimeType() {
		return "text/javascript";
	}
	
	@Override
	public FileSetFactory getFileSetFactory() {
		return new NullFileSetFactory();
	}
	
	@Override
	public RequestParser getRequestParser() {
		return requestParser;
	}
	
	@Override
	public List<String> generateRequiredDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return generateRequiredRequestPaths(true, bundleSet, locale);
	}
	
	@Override
	public List<String> generateRequiredProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return generateRequiredRequestPaths(false, bundleSet, locale);
	}
	
	@Override
	public void handleRequest(ParsedRequest request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		if(request.formName.equals("dev-bundle-request") || request.formName.equals("prod-bundle-request")) {
			try {
				String minifierSetting = request.properties.get("minifier-setting");
				MinifierPlugin minifierPlugin = brjs.minifierPlugin(minifierSetting);
				
				try(Writer writer = new OutputStreamWriter(os)) {
					minifierPlugin.minify(minifierSetting, getInputSources(request, bundleSet), writer);
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
	
	private List<InputSource> getInputSources(ParsedRequest request, BundleSet bundleSet) throws BundlerProcessingException {
		List<InputSource> inputSources = new ArrayList<>();
		
		try {
			String charsetName = brjs.bladerunnerConf().getDefaultOutputEncoding();
			
			for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins("text/javascript")) {
				if(bundlerPlugin != this) {
					String locale = request.properties.get("locale");
					List<String> requestPaths = (request.formName.equals("dev-bundle-request")) ? bundlerPlugin.generateRequiredDevRequestPaths(bundleSet, locale) :
						bundlerPlugin.generateRequiredProdRequestPaths(bundleSet, locale);
					RequestParser requestParser = bundlerPlugin.getRequestParser();
					
					for(String requestPath : requestPaths) {
						ParsedRequest parsedRequest = requestParser.parse(requestPath);
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						
						bundlerPlugin.handleRequest(parsedRequest, bundleSet, baos);
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
	
	private void writeTagContent(boolean isDev, Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer, boolean separateFiles) throws IOException {
		MinifierSetting minifierSetting = new MinifierSetting(tagAttributes);
		
		if(separateFiles) {
			for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins("text/javascript")) {
				if(bundlerPlugin != this) {
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
			writer.write("<script type='text/javascript' src='" + requestParser.createRequest("bundle-request", locale, minifierSetting.devSetting()) + "'></script>\n");
		}
	}
	
	private List<String> generateRequiredRequestPaths(boolean isDev, BundleSet bundleSet, String locale) throws BundlerProcessingException {
		List<String> requestPaths = new ArrayList<>();
		
		for(BundlerPlugin bundlerPlugin : brjs.bundlerPlugins("text/javascript")) {
			if(bundlerPlugin != this) {
				if(isDev) {
					requestPaths.addAll(bundlerPlugin.generateRequiredDevRequestPaths(bundleSet, locale));
				}
				else {
					requestPaths.addAll(bundlerPlugin.generateRequiredProdRequestPaths(bundleSet, locale));
				}
			}
		}
		
		return requestPaths;
	}
}
