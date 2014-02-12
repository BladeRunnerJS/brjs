package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.JsLib;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;

public class JsSeedBundlerFileAppender implements BladeRunnerFileAppender
{
	private static final String INDEX_HTML = "index.html";
	private static final String INDEX_HTM = "index.htm";
	private static final String INDEX_JSP = "index.jsp";
	private static final String RESOURCES_XML_DIR = "resources/xml";
	private static final String RESOURCES_HTML_DIR = "resources/html";
	private static final String TESTS_DIR = "tests";
	
	@Override
	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files)
	{
		// TODO
	}
	
	@Override
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws ContentProcessingException
	{
		// do nothing
	}
	
	@Override
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files)
	{
		JsLib jsLib = BRJSAccessor.root.locateAncestorNodeOfClass(libraryResourcesRoot, JsLib.class);
		
		// this hack should be removed -- it should actually be the source file finder that adds library seeds as they are needed
		if(jsLib.parentNode() instanceof App)
		{
			files.addAll(Arrays.asList(libraryResourcesRoot.listFiles((FilenameFilter) new SuffixFileFilter(Arrays.asList(".xml", ".html", ".htm")))));
		}
	}

	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		addRelativePathsToFiles(aspectRoot, files, Arrays.asList(INDEX_HTML, INDEX_HTM, INDEX_JSP, RESOURCES_XML_DIR, RESOURCES_HTML_DIR));
		if(appRootIsDefaultAspect(aspectRoot))
		{
			addWebcentricDirectoryIfExists(aspectRoot, files);
		}
	}
	
	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		// do nothing
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		addRelativePathsToFiles(bladesetRoot, files, Arrays.asList(RESOURCES_XML_DIR, RESOURCES_HTML_DIR));
	}

	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		addRelativePathsToFiles(bladeRoot, files, Arrays.asList(RESOURCES_XML_DIR, RESOURCES_HTML_DIR));
	}

	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		addRelativePathsToFiles(workbenchRoot, files, Arrays.asList(INDEX_HTML, INDEX_HTM, INDEX_JSP, RESOURCES_XML_DIR, RESOURCES_HTML_DIR));
	}

	@Override
	public void appendTestFiles(File testDir, List<File> files)
	{
		new JsSourceBundlerFileAppender().appendTestFiles(testDir, files);
		addRelativePathsToFiles(testDir, files, Arrays.asList(TESTS_DIR));
		addRelativePathsToFiles(testDir, files, Arrays.asList(RESOURCES_XML_DIR, RESOURCES_HTML_DIR));
	}
	
	private void addWebcentricDirectoryIfExists(File appRoot, List<File> files)
	{
		File webcentricResourcesRoot = new File(appRoot, CutlassConfig.WEBCENTRIC_XML_FOLDER);
		if(webcentricResourcesRoot.exists())
		{
			files.add(webcentricResourcesRoot);
		}
	}

	private boolean appRootIsDefaultAspect(File appRoot)
	{
		return appRoot.equals(CutlassDirectoryLocator.getDefaultAppAspect(appRoot));
	}

	private void addRelativePathsToFiles(File appRoot, List<File> files, List<String> relativePaths)
	{
		for (String relativePath : relativePaths)
		{
			files.add(new File(appRoot, relativePath));
		}
	}
}
