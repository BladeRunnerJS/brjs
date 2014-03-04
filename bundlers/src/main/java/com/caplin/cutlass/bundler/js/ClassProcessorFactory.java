package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.logging.LoggerType;

import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.bundler.js.aliasing.AliasDefinition;
import com.caplin.cutlass.bundler.js.aliasing.AliasInformation;
import com.caplin.cutlass.bundler.js.aliasing.AliasRegistry;
import com.caplin.cutlass.bundler.js.aliasing.ScenarioAliases;

public class ClassProcessorFactory
{
	
	public static ClassProcessor createClassProcessor(List<ClassnameFileMapping> sourceFiles, List<ClassnameFileMapping> patchFiles, List<String> thirdpartyClassnames, AliasRegistry aliasRegistry)
	{
		ClassesTrie classTrie = new ClassesTrie();
		ClassDictionary libraryClassDictionary = new ClassDictionary();
		ClassDictionary patchClassDictionary = new ClassDictionary();
		
		populateDictionary(sourceFiles, libraryClassDictionary, "class");
		populateDictionary(patchFiles, patchClassDictionary, "patch");
		
		populateTrieWithSourceFiles(sourceFiles, classTrie);
		populateTrieWithThirdpartyLibraries(thirdpartyClassnames, classTrie);
		
		populateDictionaryAndTrieWithAliases(aliasRegistry, libraryClassDictionary, classTrie);
		
		ClassProcessor processor = new ClassProcessor(classTrie, libraryClassDictionary, patchClassDictionary);
		return processor;
	}
	
	private static void populateDictionary(List<ClassnameFileMapping> sourceFiles, ClassDictionary classDictionary, String mappingType)
	{
		for (ClassnameFileMapping mapping : sourceFiles)
		{
			classDictionary.add(mapping.getFile(), mapping.getClassname());
			
			Logger logger = BRJSAccessor.root.logger(LoggerType.UTIL, ClassProcessorFactory.class);
			logger.debug("adding " + mappingType + " mapping: " + mapping.toString());
		}
	}
	
	private static void populateTrieWithSourceFiles(List<ClassnameFileMapping> sourceFiles, ClassesTrie classTrie)
	{
		for (ClassnameFileMapping mapping : sourceFiles)
		{
			classTrie.addClass(mapping.getClassname());
		}
	}
	
	private static void populateTrieWithThirdpartyLibraries(List<String> thirdpartyClassnames, ClassesTrie classTrie)
	{
		for(String libraryName : thirdpartyClassnames)
		{
			classTrie.addClass(libraryName);
		}
	}
	
	private static void populateDictionaryAndTrieWithAliases(AliasRegistry aliasRegistry, ClassDictionary classDictionary, ClassesTrie classTrie)
	{
		ScenarioAliases aliases = aliasRegistry.getAliases();
		
		for(String aliasName : aliases.getAliasNames())
		{
			addAliasToDictionaryAndTrie( classDictionary, classTrie, aliases, aliasName);
		}
	}

	private static void addAliasToDictionaryAndTrie( ClassDictionary classDictionary, ClassesTrie classTrie, ScenarioAliases scenarioAliases,
			String aliasName )
	{
		AliasDefinition aliasDefinition = scenarioAliases.getAlias( aliasName );
		
		String className = aliasDefinition.getClassName();
		File resolvedClassFile = classDictionary.lookup( className );
		
		if( resolvedClassFile == null && className != null )
		{
			throw new RuntimeException( "alias '" + aliasName + "' points to the non-existent class '" + className + "'." );
		}
		
		AliasInformation aliasInformation = new AliasInformation( aliasName, aliasDefinition, scenarioAliases );
		
		classTrie.addAlias( aliasName, aliasInformation );
		classDictionary.add( resolvedClassFile, aliasName );
	}
}