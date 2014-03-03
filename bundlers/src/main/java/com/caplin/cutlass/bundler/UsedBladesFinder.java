package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.filefilter.TrueFileFilter;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import com.caplin.cutlass.bundler.js.ClassDictionary;
import com.caplin.cutlass.bundler.js.ClassProcessor;
import com.caplin.cutlass.bundler.js.ClassesTrie;
import com.caplin.cutlass.bundler.js.ClassnameFileMapping;
import com.caplin.cutlass.bundler.js.FileDependencies;
import com.caplin.cutlass.bundler.js.JsSeedBundlerFileAppender;
import com.caplin.cutlass.bundler.js.JsSourceBundlerFileAppender;
import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.RequirePrefixCalculator;


public class UsedBladesFinder
{

	public List<File> findUsedBlades(File aspectRoot) throws ContentProcessingException
	{
		List<File> bladeList = new ArrayList<File>();
		
		List<File> seeds = getSeeds(aspectRoot);
		ClassesTrie bladeClassesTrie = getClassesTrie(CutlassDirectoryLocator.getParentApp(aspectRoot));
		ClassDictionary bladeClassesDictionary = getClassesDictionary(CutlassDirectoryLocator.getParentApp(aspectRoot));
		
		ClassProcessor processor = new ClassProcessor(bladeClassesTrie, bladeClassesDictionary, null);
		for (File seedFile: seeds)
		{
			FileDependencies classDependencies = processor.getClassDependencies(seedFile, false);
			bladeList.addAll(classDependencies.getSourceFiles());
		}	
		
		return bladeList;
	}

	private ClassesTrie getClassesTrie(File appRoot) throws ContentProcessingException
	{		
		List<ClassnameFileMapping> allBladeMappings = getAllBladesMappings(appRoot);
		ClassesTrie trie = new ClassesTrie();	
		for (ClassnameFileMapping mapping: allBladeMappings)
		{
			trie.addClass(mapping.getClassname());
		}
		
		return trie;
	}
	
	private ClassDictionary getClassesDictionary(File appRoot) throws ContentProcessingException
	{
		List<ClassnameFileMapping> allBladeMappings = getAllBladesMappings(appRoot);
		ClassDictionary dictionary = new ClassDictionary();
		for (ClassnameFileMapping mapping: allBladeMappings)
		{
			dictionary.add(mapping.getFile(), mapping.getClassname());
		}
		
		return dictionary;
	}
	
	private List<ClassnameFileMapping> getAllBladesMappings(File appRoot) throws ContentProcessingException
	{
		List<ClassnameFileMapping> mappings = new ArrayList<ClassnameFileMapping>();
		
		List<File> bladesets = CutlassDirectoryLocator.getChildBladesets(appRoot);
		for (File bladeset : bladesets)
		{
			List<File> blades = CutlassDirectoryLocator.getChildBlades(bladeset);
			for (File blade : blades)
			{
				mappings.add( new ClassnameFileMapping( getClassnameForBlade(blade), blade ) );
			}
		}
		return mappings;
	}
	
	private String getClassnameForBlade(File blade) throws ContentProcessingException
	{
		String requirePrefix = "";
		try
		{
			requirePrefix = RequirePrefixCalculator.getAppRequirePrefix(blade);
		}
		catch (NamespaceException ex)
		{
			throw new ContentProcessingException(ex, "There was an error calculating the namespace for the app");
		}
		
		String bladesetNamespace = RequirePrefixCalculator.getBladesetRequirePrefix(blade);
		String bladeNamespace = RequirePrefixCalculator.getBladeRequirePrefix(blade);
		
		return requirePrefix+"."+bladesetNamespace+"."+bladeNamespace;
	}
	
	private List<File> getSeeds(File aspectRoot)
	{
		List<File> seedDirs = new ArrayList<File>();
		List<File> seeds = new ArrayList<File>();
		
		new JsSourceBundlerFileAppender().appendAppAspectFiles(aspectRoot, seedDirs);
		new JsSeedBundlerFileAppender().appendAppAspectFiles(aspectRoot, seedDirs);
		
		for (File dir : seedDirs)
		{
			seeds.addAll(BundlerFileUtils.recursiveListFiles(dir, TrueFileFilter.TRUE));
		}
		
		return seeds;
	}
	
}
