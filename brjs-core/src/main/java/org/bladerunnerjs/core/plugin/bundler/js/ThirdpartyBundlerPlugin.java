package org.bladerunnerjs.core.plugin.bundler.js;

import java.io.File;
import java.io.FileReader;
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
import org.bladerunnerjs.core.plugin.taghandler.TagHandlerPlugin;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAsset;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.ThirdpartyBundlerSourceModule;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.SourceModule;
import org.bladerunnerjs.model.UnableToInstantiateAssetFileException;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class ThirdpartyBundlerPlugin extends AbstractBundlerPlugin implements BundlerPlugin, TagHandlerPlugin
{
	private ContentPathParser requestParser;
	private BRJS brjs;

	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
    		.accepts("thirdparty/bundle.js").as("bundle-request")
				.and("thirdparty/<module>/bundle.js").as("single-module-request")
				.and("thirdparty/module/<module>/<file-path>").as("file-request")
			.where("module").hasForm(".+")
				.and("file-path").hasForm(".+");
		
		requestParser = requestParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;	
	}
	
	@Override
	public String getTagName()
	{
		return getRequestPrefix();
	}

	@Override
	public List<SourceModule> getSourceFiles(AssetLocation assetLocation)
	{
		try
		{
    		List<SourceModule> sourceFiles = new ArrayList<SourceModule>();
    		if (assetLocation.getAssetContainer() instanceof JsLib)
    		{
    			NonBladerunnerJsLibManifest manifest = new NonBladerunnerJsLibManifest(assetLocation);
    			if (manifest.fileExists())
    			{
    				ThirdpartyBundlerSourceModule sourceFile = (ThirdpartyBundlerSourceModule) assetLocation.getAssetContainer().root().getAssetFile(ThirdpartyBundlerSourceModule.class, assetLocation, assetLocation.dir());
    				sourceFile.initManifest(manifest);
    				sourceFiles.add( sourceFile );
    			}
    		}
    		return sourceFiles;
		}
		catch (ConfigException | UnableToInstantiateAssetFileException ex)
		{
			throw new RuntimeException(ex);
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

	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		for (String requestPath : getValidDevRequestPaths(bundleSet, locale))
		{
			writer.write("<script type='text/javascript' src='" + requestPath + "'></script>\n");
		}
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		for (String requestPath : getValidProdRequestPaths(bundleSet, locale))
		{
			writer.write("<script type='text/javascript' src='" + requestPath + "'></script>\n");
		}
	}
	
	@Override
	public String getRequestPrefix()
	{
		return "thirdparty.bundle";
	}
	
	@Override
	public String getMimeType()
	{
		return "text/javascript";
	}

	@Override
	public ContentPathParser getContentPathParser()
	{
		return requestParser;
	}

	@Override
	public void writeContent(ParsedContentPath request, BundleSet bundleSet, OutputStream os) throws BundlerProcessingException
	{
		try {
			if (request.formName.equals("bundle-request"))
			{
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) 
				{
					for(SourceModule sourceFile : bundleSet.getSourceFiles()) {
						if(sourceFile instanceof ThirdpartyBundlerSourceModule)
						{
    						writer.write("// " + sourceFile.getRequirePath() + "\n");
    						IOUtils.copy(sourceFile.getReader(), writer);
    						writer.write("\n\n");
						}
					}
				}
			}
			else if(request.formName.equals("file-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) 
				{
					String libName = request.properties.get("module");
					App app = bundleSet.getBundlableNode().getApp();
					JsLib lib = app.nonBladeRunnerLib(libName);
					if (!lib.dirExists())
					{
						throw new BundlerProcessingException("Library " + lib.getName() + " doesn't exist.");
					}
					
					String filePath = request.properties.get("file-path");
					File file = lib.file(filePath);
					if (!file.exists())
					{
						throw new BundlerProcessingException("File " + file.getAbsolutePath() + " doesn't exist.");
					}
					
					writer.write("// " + file.getPath() + "\n");
					IOUtils.copy(new FileReader(file), writer);
					writer.write("\n\n");
				}
			}
			else if(request.formName.equals("single-module-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) 
				{
					SourceModule jsModule = bundleSet.getBundlableNode().getSourceFile(request.properties.get("module"));
					writer.write("// " + jsModule.getRequirePath() + "\n");
					IOUtils.copy(jsModule.getReader(), writer);
					writer.write("\n\n");
				}
			}
			else {
				throw new BundlerProcessingException("unknown request form '" + request.formName + "'.");
			}
		}
		catch(RequirePathException | ConfigException | IOException ex) {
			throw new BundlerProcessingException(ex);
		}
	}

	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale)
	{
		List<String> requestPaths = new ArrayList<>();
		for(SourceModule sourceFile : bundleSet.getSourceFiles()) {
			if(sourceFile instanceof ThirdpartyBundlerSourceModule) {
				requestPaths.add(requestParser.createRequest("single-module-request", sourceFile.getRequirePath()));
			}
		}
		return requestPaths;
	}

	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale)
	{
		List<String> requestPaths = new ArrayList<>();
		requestPaths.add(requestParser.createRequest("bundle-request"));
		return requestPaths;
	}

}
