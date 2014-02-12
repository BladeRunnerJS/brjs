package com.caplin.cutlass.bundler.js.aliasing;

import java.io.File;
import java.util.List;

import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class AliasFileAppender implements BladeRunnerFileAppender
{
	private static final String RESOURCES_ALIASES_FILE = "resources/aliases.xml";
	private static final String RESOURCES_ALIASES_DEFINITIONS_FILE = "resources/aliasDefinitions.xml";
	private static final String ALIASES_DEFINITIONS_FILE = "aliasDefinitions.xml";
	
	@Override
	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files)
	{		
		// do nothing
	}
	
	@Override
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws ContentProcessingException
	{
		// do nothing
	}
	
	@Override
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files)
	{
		appendAliasesFile(libraryResourcesRoot, ALIASES_DEFINITIONS_FILE, files);
	}
	
	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		appendAliasesFile(aspectRoot, RESOURCES_ALIASES_FILE, files);
	}
	
	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		// do nothing
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		appendAliasesFile(bladesetRoot, RESOURCES_ALIASES_DEFINITIONS_FILE, files);
	}
	
	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		appendAliasesFile(bladeRoot, RESOURCES_ALIASES_DEFINITIONS_FILE, files);
	}
	
	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		appendAliasesFile(workbenchRoot, RESOURCES_ALIASES_FILE, files);
	}
	
	@Override
	public void appendTestFiles(File testDir, List<File> files)
	{
		appendAliasesFile(testDir, RESOURCES_ALIASES_DEFINITIONS_FILE, files);
		appendAliasesFile(testDir, RESOURCES_ALIASES_FILE, files);
	}
	
	private void appendAliasesFile(File rootDir, String aliasesPath, List<File> files)
	{
		File aliasFile = new File(rootDir, aliasesPath);
		
		if(aliasFile.exists())
		{
			files.add(aliasFile);
		}
	}
}
