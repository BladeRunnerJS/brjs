package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;

import com.caplin.cutlass.bundler.LibraryManifest;
import com.caplin.cutlass.bundler.ThirdPartyLibraryFinder;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;


public class ThirdPartyFileFinder
{
	private Logger logger = BRJSAccessor.root.logger(LoggerType.UTIL, ThirdPartyFileFinder.class);
	private ThirdPartyLibraryFinder libraryFinder;
	
	public ThirdPartyFileFinder(ThirdPartyLibraryFinder libraryFinder) {
		this.libraryFinder = libraryFinder;
	}

	public List<File> getThirdPartyLibraryFiles(File baseDir, Set<String> thirdPartyLibraries) throws RequestHandlingException
	{	
		LinkedHashMap<String, LibraryManifest> orderedLibraries = getOrderedLibraries(baseDir, thirdPartyLibraries);
		List<File> thirdPartyLibraryFiles = getFilesFromLibraries(baseDir, orderedLibraries);
		return thirdPartyLibraryFiles;
	}
	
	private List<File> getFilesFromLibraries(File baseDir, LinkedHashMap<String, LibraryManifest> orderedLibraries) throws RequestHandlingException
	{
		List<File> filesOfLibraries = new ArrayList<File>();
		for(Entry<String, LibraryManifest> libraryWithManifest : orderedLibraries.entrySet())
		{
			String libraryName = libraryWithManifest.getKey();
			LibraryManifest manifest = libraryWithManifest.getValue();
			List<String> libraryFilePaths = manifest.getJavascriptFiles();
			File libraryDirectory = libraryFinder.getThirdPartyLibraryDirectory(baseDir, libraryName).getAbsoluteFile();
			filesOfLibraries.addAll(getLibraryFiles(libraryDirectory, libraryFilePaths));
		}
		return filesOfLibraries;
	}
	
	private LinkedHashMap<String, LibraryManifest> getOrderedLibraries(File baseDir, Set<String> thirdPartyLibraries) throws ContentProcessingException
	{
		LinkedHashMap<String, LibraryManifest> orderedLibraries = new LinkedHashMap<String, LibraryManifest>();
		for(String library : thirdPartyLibraries)
		{
			populateLibraryDependencies(baseDir, library, orderedLibraries);
		}
		return orderedLibraries;
	}
	
	private void populateLibraryDependencies(File baseDir, String library, LinkedHashMap<String, LibraryManifest> orderedLibraries) throws ContentProcessingException
	{
		File libraryDirectory = libraryFinder.getThirdPartyLibraryDirectory(baseDir, library);
		LibraryManifest manifest = new LibraryManifest(libraryDirectory);
		
		List<String> dependentLibs = manifest.getLibraryDependencies();
		for(String dependentLib : dependentLibs)
		{
			populateLibraryDependencies(baseDir, dependentLib, orderedLibraries);
		}
		
		orderedLibraries.put(library, manifest);
	}

	private List<File> getLibraryFiles(File libraryDirectory, List<String> filePaths) throws RequestHandlingException
	{
		List<File> files = new ArrayList<File>();
		if(filePaths.size() > 0)
		{
			for(String filePath : filePaths)
			{
				File libraryFile = new File(libraryDirectory, filePath);
				if(!libraryFile.exists())
				{
					String errorMessage = "Could not find thirdparty library file '" + libraryFile.getName() 
							+ "' for library '" + libraryDirectory.getName() + "'";
					throw new ContentProcessingException(errorMessage);
				}
				files.add(libraryFile);
				
				logger.debug("adding library file: '" + libraryFile.getPath() + "'");
			}
		}
		else
		{
			files.addAll(Arrays.asList(libraryDirectory.listFiles((FileFilter)new SuffixFileFilter(".js"))));
		}
		return files;
	}
	
}
