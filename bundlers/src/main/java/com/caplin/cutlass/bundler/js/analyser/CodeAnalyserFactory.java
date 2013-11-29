package com.caplin.cutlass.bundler.js.analyser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.caplin.cutlass.bundler.BundlerFileUtils;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.bundler.js.ClassProcessor;
import com.caplin.cutlass.bundler.js.ClassProcessorFactory;
import com.caplin.cutlass.bundler.js.ClassnameFileMapping;
import com.caplin.cutlass.bundler.js.SourceFileLocator;
import com.caplin.cutlass.bundler.js.aliasing.AliasRegistry;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AppJsLibWrapper;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ShallowJsLib;

public class CodeAnalyserFactory
{
	/**
	 * @param baseDir - Directory containing seed files e.g. index.html, index.jsp
	 * @throws BundlerProcessingException 
	 */
	public static CodeAnalyser getCodeAnalyser(File baseDir) throws BundlerProcessingException
	{
		List<ClassnameFileMapping> sourceFiles = SourceFileLocator.getAllSourceFiles(baseDir, null);
		List<ClassnameFileMapping> patchFiles = new ArrayList<>();
		List<String> thirdpartyClassnames = SourceFileLocator.getThirdPartyLibraryNames(baseDir);
		List<File> seedFiles = SourceFileLocator.getAllSeedFiles(baseDir, null);
		AliasRegistry aliasRegistry = new AliasRegistry(baseDir, null, SourceFileLocator.createValidClasses(sourceFiles));
		ClassProcessor classProcessor = ClassProcessorFactory.createClassProcessor(sourceFiles, patchFiles, thirdpartyClassnames, aliasRegistry);
		CodeAnalyser codeAnalyser = new DefaultCodeAnalyser();
		
		classProcessor.analyser = codeAnalyser;
		for(File seedFile : seedFiles)
		{
			classProcessor.getClassDependencies(seedFile);
		}
		
		return codeAnalyser;
	}
	
	/**
	 * 
	 * @param baseDir
	 * @return
	 * @throws BundlerProcessingException 
	 */
	public static CodeAnalyser getLibraryCodeAnalyser(App appNode, File packageDir) throws BundlerProcessingException
	{
		List<File> libraryRootDirs = new ArrayList<File>();
		
		for(JsLib jsLibrary: appNode.jsLibs())
		{
			if ( jsLibrary instanceof ShallowJsLib || (jsLibrary instanceof AppJsLibWrapper && ((AppJsLibWrapper)jsLibrary).getWrappedJsLib() instanceof ShallowJsLib) ) 
			{
				// ignore
			}
			else
			{
    			File libraryRoot = jsLibrary.src().dir();
    
    			if(!libraryRoot.exists())
    			{
    				throw new BundlerProcessingException("Cant find: " + libraryRoot.getAbsolutePath() );
    			}
    			
    			libraryRootDirs.addAll(Arrays.asList(libraryRoot));
			}
		}
		
		Set<ClassnameFileMapping> mappings = SourceFileLocator.getClassNameMappings(libraryRootDirs, SourceFileLocator.SOURCE_FILENAME_FILTER);
		ArrayList<ClassnameFileMapping> mappingsList = new ArrayList<ClassnameFileMapping>(mappings);
		
		List<ClassnameFileMapping> sourceFiles = SourceFileLocator.getAllSourceFiles(packageDir, null);
		List<String> thirdpartyClassnames = SourceFileLocator.getThirdPartyLibraryNames(packageDir);
		
		AliasRegistry aliasRegistry = new AliasRegistry(packageDir, null, SourceFileLocator.createValidClasses(sourceFiles));
		ClassProcessor classProcessor = ClassProcessorFactory.createClassProcessor(mappingsList, new ArrayList<ClassnameFileMapping>(), thirdpartyClassnames, aliasRegistry);
		
		List<File> seeds = BundlerFileUtils.recursiveListFiles(packageDir, SourceFileLocator.SOURCE_FILENAME_FILTER);
		CodeAnalyser codeAnalyser = new DefaultCodeAnalyser();
		
		classProcessor.analyser = codeAnalyser;
		for(File seedFile : seeds)
		{
			classProcessor.getClassDependencies(seedFile);
		}
		
		return codeAnalyser;
	}
}