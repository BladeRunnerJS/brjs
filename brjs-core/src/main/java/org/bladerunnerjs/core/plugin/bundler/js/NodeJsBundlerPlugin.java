package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.core.plugin.bundler.AbstractBundlerPlugin;
import org.bladerunnerjs.core.plugin.bundler.BundlerPlugin;
import org.bladerunnerjs.core.plugin.bundlesource.js.NodeJsSourceModule;
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.JsStyleUtility;
import org.bladerunnerjs.model.utility.RequestParserBuilder;

public class NodeJsBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin, TagHandlerPlugin {
	public static final String JS_STYLE = "node.js";
	
	private ContentPathParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;
	
	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("node-js/bundle.js").as("bundle-request")
				.and("node-js/module/<module>.js").as("single-module-request")
			.where("module").hasForm(".+"); // TODO: ensure we really need such a simple hasForm() -- we didn't use to need it
		
		requestParser = requestParserBuilder.build();
		prodRequestPaths.add(requestParser.createRequest("bundle-request"));
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getTagName() {
		return getRequestPrefix();
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(bundleSet, getValidDevRequestPaths(bundleSet, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException {
		try {
			writeTagContent(bundleSet, getValidProdRequestPaths(bundleSet, locale), writer);
		}
		catch (BundlerProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String getRequestPrefix() {
		return "node-js";
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
		List<String> requestPaths = new ArrayList<>();
		
		for(SourceModule sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof NodeJsSourceModule) {
				requestPaths.add(requestParser.createRequest("single-module-request", sourceFile.getRequirePath()));
			}
		}
		
		return requestPaths;
	}
	
	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException {
		return prodRequestPaths;
	}
	
	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException {
		try {
			if(request.formName.equals("single-module-request")) {
				try(Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					SourceModule jsModule = bundleSet.getBundlableNode().getSourceFile(request.properties.get("module"));
					IOUtils.copy(jsModule.getReader(), writer);
				}
			}
			else if(request.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) {
					for(SourceModule sourceFile : bundleSet.getSourceFiles()) {
						if (sourceFile instanceof NodeJsSourceModule)
						{
							writer.write("// " + sourceFile.getRequirePath() + "\n");
    						IOUtils.copy(sourceFile.getReader(), writer);
    						writer.write("\n\n");
						}
					}
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + request.formName + "'.");
			}
		}
		catch(ConfigException | IOException | RequirePathException e) {
			throw new BundlerProcessingException(e);
		}
	}
	
	@Override
	public List<SourceModule> getSourceFiles(AssetLocation assetLocation)
	{ 
		if (JsStyleUtility.getJsStyle(assetLocation.dir()).equals(JS_STYLE)) {
			return assetLocation.getAssetContainer().root().getAssetFilesWithExtension(assetLocation, NodeJsSourceModule.class, "js");
		}
		else {
			return Arrays.asList();
		}			
	}

	@Override
	public List<LinkedAsset> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}

	@Override
	public List<Asset> getResourceFiles(AssetLocation assetLocation)
	{
		return Arrays.asList();
	}
	
	private void writeTagContent(BundleSet bundleSet, List<String> requestPaths, Writer writer) throws IOException {
		for(String bundlerRequestPath : requestPaths) {
			writer.write("<script type='text/javascript' src='" + bundlerRequestPath + "'></script>\n");
		}
	}
}
