package com.caplin.cutlass.bundler.thirdparty;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.core.plugin.AbstractPlugin;
import com.caplin.cutlass.LegacyFileBundlerPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ContentPathParser;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.CutlassConfig;

import com.caplin.cutlass.bundler.BundlerFileUtils;
import com.caplin.cutlass.bundler.ThirdPartyLibraryFinder;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;

import com.caplin.cutlass.bundler.io.BundleWriterFactory;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;
import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.path.AppPath;
import com.caplin.cutlass.structure.model.path.ThirdpartyLibPath;

public class ThirdPartyBundler extends AbstractPlugin implements LegacyFileBundlerPlugin
{
	private static final NotFileFilter notManifestFileFilter = new NotFileFilter(new NameFileFilter(CutlassConfig.LIBRARY_MANIFEST_FILENAME, IOCase.INSENSITIVE));
	private final ContentPathParser requestParser = RequestParserFactory.createThirdPartyBundlerRequestParser();
	private final ThirdPartyLibraryFinder libraryFinder = new ThirdPartyLibraryFinder();
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "thirdparty.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return requestParser.getRequestForms();
	}
	
	@Override
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
	{
		String requestPath = StringUtils.substringBeforeLast(requestName, "?");		
		
		ParsedContentPath request = requestParser.parse(requestPath);
		String resourcePath = getResourcePath(baseDir, request.properties);
		File file = new File(resourcePath);
		
		List<File> bundledFiles = new ArrayList<File>();
		if(file.exists())
		{
			bundledFiles.add(file);
		}
		return bundledFiles;
	}

	@Override
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws RequestHandlingException
	{
		// TODO remove duplication between ThirdPartyBundler and ImageBundler
		// (e.g. make them extend abstract SingleFileBundler (?))
		if(sourceFiles.size() == 0)
		{
			throw new ResourceNotFoundException();
		}
		else if(sourceFiles.size() > 1)
		{
			throw new BundlerProcessingException("More than one library resource file was requested to be added to a single bundle.");
		}
		else
		{
			File fileToOutput = sourceFiles.get(0);
			String filePath = fileToOutput.getAbsolutePath();
			String extension = StringUtils.substringAfterLast(filePath, ".");
			extension = StringUtils.substringBeforeLast(extension, "_thirdparty");
			if (CutlassConfig.IMAGE_EXTENSIONS.contains(extension))
			{
				writeImageToOutput(fileToOutput, outputStream);
			}
			else
			{
				writeTextFileToOutput(fileToOutput, outputStream);
			}
		}
	}
	
	private void writeImageToOutput(File image, OutputStream outputStream) throws RequestHandlingException
	{
		try
		{
			InputStream input = new FileInputStream(image);
			try
			{
				IOUtils.copy(input, outputStream);
			}
			finally
			{
				input.close();
			}
		}
		catch (IOException e)
		{
			throw new BundlerFileProcessingException(image, e, "Error while writing bundle");
		}
	}
	
	private void writeTextFileToOutput(File textFile, OutputStream outputStream) throws RequestHandlingException
	{
		try(Writer writer = BundleWriterFactory.createWriter(outputStream);
			Reader reader = BundlerFileReaderFactory.getBundlerFileReader(textFile))
		{
			IOUtils.copy(reader, writer);
			writer.flush();
		}
		catch (IOException e)
		{
			throw new BundlerFileProcessingException(textFile, e, "Error while writing bundle");
		}
	}
	
	@Override
	public List<String> getValidRequestStrings(AppMetaData appMetaData)
	{
		File appDir = appMetaData.getApplicationDirectory();
		Map<String, File> libraryDirectories = libraryFinder.getThirdPartyLibraryDirectories(appDir);
		List<String> libraryResourceRequests = getThirdPartyLibraryResourceRequests(libraryDirectories);
		return libraryResourceRequests;
	}
	
	private List<String> getThirdPartyLibraryResourceRequests(Map<String, File> libraryDirectories)
	{
		List<String> resourceRequests = new ArrayList<String>();
		for(Entry<String, File> entry : libraryDirectories.entrySet())
		{
			String libraryName = entry.getKey();
			File libraryDir = entry.getValue();
			List<File> libraryResources = new ArrayList<File>();
			libraryResources.addAll(BundlerFileUtils.recursiveListFiles(entry.getValue(), notManifestFileFilter ));
			for(File libraryResource : libraryResources)
			{
				String resourceRequest = "thirdparty-libraries/" + libraryName + 
						"/" + getRelativeFilePath(libraryDir, libraryResource) + CutlassConfig.THIRDPARTY_BUNDLE_SUFFIX;
				resourceRequests.add(resourceRequest);
			}
		}
		return resourceRequests;
	}
	
	private boolean isLibraryInParentApp(File baseDir, Map<String, String> requestProperties)
	{
		ThirdpartyLibPath libPath = AppPath.locateAncestorPath(baseDir).thirdpartyLibsPath().libPath(requestProperties.get("library"));
		
		return libPath.getDir().exists();
	}
	
	private String getResourcePath(File baseDir, Map<String, String> requestProperties)
	{
		String resourcePath;
		if(isLibraryInParentApp(baseDir, requestProperties))
		{
			resourcePath = getResourcePathInParentApp(baseDir, requestProperties);
		}
		else
		{
			resourcePath = getResourcePathInSdk(baseDir, requestProperties);
		}
		return resourcePath;
	}

	private String getResourcePathInParentApp(File baseDir, Map<String, String> requestProperties)
	{
		ThirdpartyLibPath libPath = AppPath.locateAncestorPath(baseDir).thirdpartyLibsPath().libPath(requestProperties.get("library"));
		
		return new File(libPath.getDir(), requestProperties.get("resourcePath")).getPath();
	}
	
	private String getResourcePathInSdk(File baseDir, Map<String, String> requestProperties)
	{
		File sdkThirdpartyDir = SdkModel.getSdkPath(baseDir).libsPath().javascriptLibsPath().thirdpartyLibsPath().getDir();
		String resourcePathInSdk = sdkThirdpartyDir.getPath() + File.separator + requestProperties.get("library") +
			File.separator + requestProperties.get("resourcePath");
		
		return resourcePathInSdk;
	}
	
	private String getRelativeFilePath(File base, File target)
	{
		URI baseFileURI = base.toURI();
		URI pathFileURI = target.toURI();
		URI relativeURI = baseFileURI.relativize(pathFileURI);
		
		return relativeURI.toString();
	}
}