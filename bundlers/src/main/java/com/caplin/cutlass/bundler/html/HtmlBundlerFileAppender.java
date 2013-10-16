package com.caplin.cutlass.bundler.html;

import static com.caplin.cutlass.bundler.ResourceAdder.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class HtmlBundlerFileAppender implements BladeRunnerFileAppender
{
	private static final String RESOURCES_HTML_FOLDER = "resources/html";
	
	@Override
	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files)
	{
		// do nothing	
	}
	
	@Override
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws BundlerProcessingException
	{
		// do nothing
	}
	
	@Override
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files)
	{
		if(libraryResourcesRoot.exists())
		{
			List<File> htmlFiles = Arrays.asList(libraryResourcesRoot.listFiles((FilenameFilter) new SuffixFileFilter(Arrays.asList(".html", ".htm"))));
			Collections.sort(htmlFiles);
			
			files.addAll(htmlFiles);
		}
	}

	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		appendResourceDirectory(aspectRoot, RESOURCES_HTML_FOLDER, files);
	}
	
	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		// do nothing
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		appendResourceDirectory(bladesetRoot, RESOURCES_HTML_FOLDER, files);
	}

	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		appendResourceDirectory(bladeRoot, RESOURCES_HTML_FOLDER, files);
	}

	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		appendResourceDirectory(workbenchRoot, RESOURCES_HTML_FOLDER, files);
	}

	@Override
	public void appendTestFiles(File testDir, List<File> files)
	{
		appendResourceDirectory(testDir, RESOURCES_HTML_FOLDER, files);
	}
}
