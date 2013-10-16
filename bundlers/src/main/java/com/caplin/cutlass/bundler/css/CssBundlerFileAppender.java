package com.caplin.cutlass.bundler.css;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import static com.caplin.cutlass.bundler.ResourceAdder.*;

import org.bladerunnerjs.model.sinbin.CutlassConfig;
import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import com.caplin.cutlass.bundler.LibraryManifest;
import com.caplin.cutlass.bundler.ThirdPartyLibraryFinder;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class CssBundlerFileAppender implements BladeRunnerFileAppender
{
	private static final String RESOURCES_STYLE_FOLDER = "resources/style";
	private static final String THEMES_FOLDER = "/themes/";
	private final String theme;
	private final ThirdPartyLibraryFinder libraryFinder;
	
	public CssBundlerFileAppender(String theme)
	{
		this.theme = theme;
		libraryFinder = new ThirdPartyLibraryFinder();
	}
	
	@Override
	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files) throws BundlerProcessingException
	{
		if(theme.equals(CutlassConfig.COMMON_CSS))
		{
			Map<String, File> libraryDirectories = libraryFinder.getThirdPartyLibraryDirectories(appRoot);
			files.addAll(getCssFilesFromLibraryDirectories(libraryDirectories));
		}
	}
	
	@Override
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws BundlerProcessingException
	{
		// do nothing
	}
	
	@Override
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files)
	{
		if (theme.equals(CutlassConfig.COMMON_CSS) && libraryResourcesRoot.exists())
		{
			List<File> cssFiles = Arrays.asList(libraryResourcesRoot.listFiles((FilenameFilter) new SuffixFileFilter(".css")));
			Collections.sort(cssFiles);
			
			files.addAll(cssFiles);
		}
	}

	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		appendResourceDirectory(aspectRoot, THEMES_FOLDER + theme + "/", files);
	}
	
	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		appendResourceDirectory(aspectRoot, THEMES_FOLDER + theme + "/", files);
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		appendResourceDirectory(bladesetRoot, THEMES_FOLDER + theme + "/", files);
	}

	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		appendResourceDirectory(bladeRoot, THEMES_FOLDER + theme + "/", files);
	}

	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		appendResourceDirectory(workbenchRoot, RESOURCES_STYLE_FOLDER, files);
	}

	@Override
	public void appendTestFiles(File testDir, List<File> files)
	{
		if(theme.equals(CutlassConfig.COMMON_CSS))
		{
			appendResourceDirectory(testDir, RESOURCES_STYLE_FOLDER, files);
		}
	}

	private List<File> getCssFilesFromLibraryDirectories(Map<String, File> libraryDirectories) throws BundlerProcessingException
	{
		List<File> thirdPartyCssFiles = new ArrayList<File>();
		for(File libraryDirectory: libraryDirectories.values())
		{
			thirdPartyCssFiles.addAll(addCssFilesForThirdPartyLibrary(libraryDirectory));
		}
		return thirdPartyCssFiles;
	}
	
	private List<File> addCssFilesForThirdPartyLibrary(File libraryDirectory) throws BundlerProcessingException
	{
		List<File> libraryCssFiles = new ArrayList<File>();
		LibraryManifest manifest = new LibraryManifest(libraryDirectory);
		for(String cssFilePath : manifest.getCssFiles())
		{
			File cssFile = new File(libraryDirectory, cssFilePath);
			if(!cssFile.exists())
			{
				String errorMessage = "Could not find thirdparty library css file '" + cssFile.getName() 
						+ "' for library '" + libraryDirectory.getName() + "'";
				throw new BundlerFileProcessingException(cssFile, errorMessage);
			}
			libraryCssFiles.add(cssFile);
		}
		return libraryCssFiles;
	}
}
