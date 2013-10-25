package com.caplin.cutlass.bundler.xml;

import static com.caplin.cutlass.bundler.ResourceAdder.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public class XmlBundlerFileAppender implements BladeRunnerFileAppender
{
	private static final String RESOURCES_XML_FOLDER = "resources/xml";
	
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
			List<File> xmlFiles = Arrays.asList(libraryResourcesRoot.listFiles((FilenameFilter) new SuffixFileFilter(".xml")));
			Collections.sort(xmlFiles);
			
			files.addAll(xmlFiles);
		}
	}
	
	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		appendResourceDirectory(aspectRoot, RESOURCES_XML_FOLDER, files);
	}
	
	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		// do nothing
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		appendResourceDirectory(bladesetRoot, RESOURCES_XML_FOLDER, files);
	}

	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		appendResourceDirectory(bladeRoot, RESOURCES_XML_FOLDER, files);
	}

	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		appendResourceDirectory(workbenchRoot, RESOURCES_XML_FOLDER, files);
	}

	@Override
	public void appendTestFiles(File testDir, List<File> files)
	{
		appendResourceDirectory(testDir, RESOURCES_XML_FOLDER, files);
	}
}
