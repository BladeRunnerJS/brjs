package com.caplin.cutlass.bundler.js;

import static com.caplin.cutlass.bundler.BundlerConstants.BUNDLE_EXT;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.core.plugin.AbstractPlugin;
import com.caplin.cutlass.LegacyFileBundlerPlugin;

import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.AppMetaData;

import com.caplin.cutlass.bundler.BundlerFileUtils;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.bundler.io.BundleWriterFactory;
import com.caplin.cutlass.bundler.js.minification.Minifier;
import com.caplin.cutlass.bundler.js.minification.MinifierFactory;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.structure.BundlePathsFromRoot;

public class JsBundler extends AbstractPlugin implements LegacyFileBundlerPlugin
{
	private final ContentPathParser requestParser = RequestParserFactory.createJsBundlerRequestParser();
	private Minifier minifier;
	
	public JsBundler()
	{
		minifier = MinifierFactory.createMinifier(null);
	}
	
	public JsBundler(String minifierName)
	{
		minifier = MinifierFactory.createMinifier(minifierName);
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "js.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return requestParser.getRequestForms();
	}
	
	@Override
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
	{
		ParsedContentPath request = requestParser.parse(requestName);
		
		if(request.properties.containsKey("path"))
		{
			String path = request.properties.get("path");
			
			return Arrays.asList(new File(path));
		}
		else if(requestName.endsWith("map_js.bundle"))
		{
			return Arrays.asList(new File("map_js.bundle"));
		}
		else
		{
			SourceFileFinder finder = new SourceFileFinder();
			
			return finder.getSourceFiles(baseDir, testDir, BRJSAccessor.root.jsPatches().dir());
		}
	}

	@Override
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
	{
		if(sourceFiles.size() == 1)
		{
			File file = sourceFiles.get(0);
			
			if(file.getName().equals("map_js.bundle"))
			{
				serveSourceMap(minifier, outputStream);
			}
			// TODO: re-enable this (and delete the else beneath it) next time we try to get source maps working
//			else if(file.getName().equals("js.bundle"))
//			{
//				bundleFiles(sourceFiles, outputStream);
//			}
//			else
//			{
//				serveFile(file, outputStream);
//			}
			else
			{
				bundleFiles(sourceFiles, outputStream);
			}
		}
		else if (sourceFiles.size() > 1)
		{
			bundleFiles(sourceFiles, outputStream);
		}
	}
	
	@Override
	public List<String> getValidRequestStrings(AppMetaData appMetaData)
	{
		return Arrays.asList(BundlePathsFromRoot.JS + "js" + BUNDLE_EXT);
	}
	
	private void serveSourceMap(Minifier minifier, OutputStream outputStream) throws BundlerProcessingException
	{
		Writer writer = BundleWriterFactory.createWriter(outputStream);
		
		try
		{
			minifier.writeSourceMap(writer);
		}
		catch (IOException e)
		{
			throw new BundlerProcessingException(e, "Error while writing source map.");
		}
		finally
		{
			BundleWriterFactory.closeWriter(writer);
		}
	}
	
	private void bundleFiles(List<File> sourceFiles, OutputStream outputStream) throws BundlerProcessingException
	{
		Writer writer = BundleWriterFactory.createWriter(outputStream);
		CharacterCountingWriter headerWriter = new CharacterCountingWriter(writer);
		
		try
		{
			BundlerFileUtils.writeSdkVersion(headerWriter);
			BundlerFileUtils.writeClassPackages(sourceFiles, headerWriter);
			minifier.minifySourceCode(sourceFiles, writer, headerWriter.offsetLine, headerWriter.offsetIndex);
			writer.write("caplin.onLoad();\n");
			// TODO: re-enable this next time we try to get source maps working
			//writer.write("//@ sourceMappingURL=map_js.bundle\n");
		}
		catch (IOException e)
		{
			throw new BundlerProcessingException(e, "Error while bundling files.");
		}
		finally
		{
			BundleWriterFactory.closeWriter(writer);
		}
	}
}
