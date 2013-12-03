package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import com.caplin.cutlass.util.FileUtility;

import com.caplin.cutlass.bundler.ThirdPartyLibraryFinder;
import com.caplin.cutlass.bundler.js.aliasing.AliasRegistry;
import com.caplin.cutlass.bundler.js.aliasing.ScenarioAliases;

public class SourceFileFinder
{
	public List<File> getSourceFiles(File baseDir, File testDir, File patchesDir)  throws RequestHandlingException
	{
		List<ClassnameFileMapping> sourceFiles = SourceFileLocator.getAllSourceFiles(baseDir, testDir);
		List<ClassnameFileMapping> patchFiles = SourceFileLocator.getAllPatchFiles(patchesDir);
		List<String> thirdpartyClassnames = SourceFileLocator.getThirdPartyLibraryNames(baseDir);
		AliasRegistry aliasRegistry = new AliasRegistry(baseDir, testDir, SourceFileLocator.createValidClasses(sourceFiles));
		ClassProcessor classProcessor = ClassProcessorFactory.createClassProcessor(sourceFiles, patchFiles, thirdpartyClassnames, aliasRegistry);
		List<File> seedFiles = SourceFileLocator.getAllSeedFiles(baseDir, testDir);
		Set<File> sourceFileDependencies = new LinkedHashSet<File>();
		Set<String> thirdPartyLibraries = new HashSet<String>();
		
		for (File seedFile : seedFiles)
		{
			FileDependencies classDependencies = classProcessor.getClassDependencies(seedFile, false);
			sourceFileDependencies.addAll(classDependencies.getSourceFiles());
			thirdPartyLibraries.addAll(classDependencies.getThirdPartyLibraries());
		}
		
		ThirdPartyFileFinder thirdpartyFileFinder = new ThirdPartyFileFinder(new ThirdPartyLibraryFinder());
		List<File> thirdPartyLibraryFiles = thirdpartyFileFinder.getThirdPartyLibraryFiles(baseDir, thirdPartyLibraries);
		Set<File> finalListOfBundledFiles = new LinkedHashSet<File>();
		
		finalListOfBundledFiles.addAll(thirdPartyLibraryFiles);
		finalListOfBundledFiles.addAll(sourceFileDependencies);
		finalListOfBundledFiles.add( createTemporaryAliasesFile( aliasRegistry, classProcessor.getDictionary(), classProcessor ) );
		
		return new ArrayList<File>(finalListOfBundledFiles);
	}
	
	// TODO: modify the Bundler interface to return a list of Writables rather than a list of files, so we don't need
	// this hack -- not doing now as the correct design must take source-maps (and maybe LessCSS) into account, so it's
	// best to delay until we do that work (@writables-hack)
	private File createTemporaryAliasesFile( AliasRegistry aliasRegistry, ClassDictionary classDictionary, ClassProcessor classProcessor )
	{
		File temporaryAliasesFile = null;
		
		try
		{
			temporaryAliasesFile = FileUtility.createTemporaryFile("alias-file_", ".tmp");
			
			try(FileWriter temporaryAliasesWriter = new FileWriter(temporaryAliasesFile))
			{
				ScenarioAliases activeAliases = classProcessor.getActiveAliases();
				temporaryAliasesWriter.write("caplin.__aliasData = " + aliasRegistry.getJson( classDictionary, activeAliases ) + ";");
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		
		return temporaryAliasesFile;
	}
}
