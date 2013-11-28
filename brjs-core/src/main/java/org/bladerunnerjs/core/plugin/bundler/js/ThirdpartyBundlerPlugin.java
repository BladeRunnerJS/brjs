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
import org.bladerunnerjs.core.plugin.bundlesource.js.CaplinJsSourceFile;
import org.bladerunnerjs.model.AppJsLibWrapper;
import org.bladerunnerjs.model.AssetFile;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.LinkedAssetFile;
import org.bladerunnerjs.model.NonBladerunnerJsLibManifest;
import org.bladerunnerjs.model.NonBladerunnerJsLibSourceFile;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ShallowJsLib;
import org.bladerunnerjs.model.SourceFile;
import org.bladerunnerjs.model.UnableToInstantiateAssetFileException;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.utility.RequestParserBuilder;


public class ThirdpartyBundlerPlugin extends AbstractBundlerPlugin
{
	private ContentPathParser requestParser;
	private List<String> prodRequestPaths = new ArrayList<>();
	private BRJS brjs;

	{
		RequestParserBuilder requestParserBuilder = new RequestParserBuilder();
		requestParserBuilder
			.accepts("thirdparty/bundle.js").as("bundle-request")
				.and("thirdparty/module/<module>/<file-path>").as("file-request")
			.where("module").hasForm(".+");
		
		requestParser = requestParserBuilder.build();
		prodRequestPaths.add(requestParser.createRequest("bundle-request"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;	
	}
	
	@Override
	public String getTagName()
	{
		return "thirdparty";
	}

	@Override
	public List<SourceFile> getSourceFiles(AssetLocation assetLocation)
	{
		try
		{
    		List<SourceFile> sourceFiles = new ArrayList<SourceFile>();
    		if (assetLocation.getAssetContainer() instanceof ShallowJsLib)
    		{
    			NonBladerunnerJsLibManifest manifest = new NonBladerunnerJsLibManifest(assetLocation);
    			if (manifest.fileExists())
    			{
    				NonBladerunnerJsLibSourceFile sourceFile = (NonBladerunnerJsLibSourceFile) assetLocation.getAssetContainer().root().getAssetFile(NonBladerunnerJsLibSourceFile.class, assetLocation, assetLocation.dir());
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
	public List<LinkedAssetFile> getLinkedResourceFiles(AssetLocation assetLocation)
	{
		// TODO Auto-generated method stub
		return Arrays.asList();
	}

	@Override
	public List<AssetFile> getResourceFiles(AssetLocation assetLocation)
	{
		// TODO Auto-generated method stub
		return Arrays.asList();
	}

	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		throw new RuntimeException("No implemented yet!");
		// TODO Auto-generated method stub
	}

	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, String locale, Writer writer) throws IOException
	{
		throw new RuntimeException("No implemented yet!");
		// TODO Auto-generated method stub
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
			if(request.formName.equals("file-request")) {
				throw new RuntimeException("Not yet implemented!"); //TODO
			}
			else if(request.formName.equals("bundle-request")) {
				try (Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getDefaultOutputEncoding())) 
				{
					for(SourceFile sourceFile : bundleSet.getSourceFiles()) {
						if(sourceFile instanceof NonBladerunnerJsLibSourceFile)
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
		catch(ConfigException | IOException ex) {
			throw new BundlerProcessingException(ex);
		}
	}

	@Override
	public List<String> getValidDevRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		List<String> requestPaths = new ArrayList<>();
		requestPaths.add(requestParser.createRequest("bundle-request"));
		return requestPaths;
	}

	@Override
	public List<String> getValidProdRequestPaths(BundleSet bundleSet, String locale) throws BundlerProcessingException
	{
		return getValidDevRequestPaths(bundleSet, locale);
	}

}
