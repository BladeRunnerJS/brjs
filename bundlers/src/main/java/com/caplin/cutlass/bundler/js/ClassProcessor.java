package com.caplin.cutlass.bundler.js;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;

import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

import com.caplin.cutlass.bundler.exception.UnknownBundlerException;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;
import com.caplin.cutlass.bundler.js.Match;
import com.caplin.cutlass.bundler.js.aliasing.AliasInformation;
import com.caplin.cutlass.bundler.js.aliasing.ScenarioAliases;
import com.caplin.cutlass.bundler.js.analyser.CodeAnalyser;
import com.caplin.cutlass.bundler.js.analyser.NullCodeAnalyser;
import com.caplin.cutlass.util.JsCommentStrippingReader;

public class ClassProcessor
{
	private ClassesTrie classTrie = null;
	private ClassDictionary classDictionary;
	private ClassDictionary patchDictionary;
	private Set<String> processedClasses = new HashSet<>();
	private List<String> pendingClasses = new ArrayList<>();
	private ScenarioAliases matchedAliases = new ScenarioAliases();
	private Logger logger = BRJSAccessor.root.logger(LoggerType.UTIL, ClassProcessor.class);
	public CodeAnalyser analyser = new NullCodeAnalyser();
	
	public ClassProcessor(ClassesTrie classTrie, ClassDictionary classDictionary, ClassDictionary patchDictionary)
	{
		this.classTrie = classTrie;
		this.classDictionary = classDictionary;
		this.patchDictionary = patchDictionary;
	}
	
	public ClassDictionary getDictionary()
	{
		return classDictionary;
	}
	
	public FileDependencies getClassDependencies(File theFile) throws BundlerProcessingException
	{
		return getClassDependencies(theFile, true);
	}
	
	public FileDependencies getClassDependencies(File file, boolean includeSeedFileInList) throws BundlerProcessingException
	{
		FileDependencies foundDependencies = new FileDependencies();
		List<String> foundClassNames = new ArrayList<String>();
		File sourceFile = file;
		
		analyser.setRoot(sourceFile.getName());
		
		do
		{
			List<String> classesToProcess = pendingClasses;
			pendingClasses = new ArrayList<>();
			
			if(sourceFile != null)
			{
				processSourceFile(sourceFile, foundClassNames, foundDependencies);
				sourceFile = null;
			}
			
			for(String classToProcess : classesToProcess)
			{
				processClass(classToProcess, foundClassNames, foundDependencies, null);
			}
		} while(pendingClasses.size() > 0);
		
		if (!includeSeedFileInList)
		{
			foundDependencies.getSourceFiles().remove(file);
		}
		
		return foundDependencies;
	}
	
	public ScenarioAliases getActiveAliases()
	{
		return matchedAliases;
	}
	
	private void processClass(String classname, List<String> foundClassnames, FileDependencies foundDependencies, Match matched ) throws BundlerProcessingException
	{
		if (processedClasses.contains(classname))
		{
			return;
		}

		if(matched != null){
			analyser.add(matched);
		}
		
		processedClasses.add(classname);

		File file = classDictionary.lookup(classname);
		if (file == null)
		{
			return;
		}
		if (file.isDirectory())
		{
			/* used by the used blades finder */
			foundDependencies.getSourceFiles().add(file);
		} 
		else 
		{
			processSourceFile(file, classname, foundClassnames, foundDependencies);
		}
		if (foundClassnames != null)
		{
			foundClassnames.add(classname);
		}
		
		if(matched != null){
			analyser.addCompleted();
		}
	}
	
	private void processSourceFile(File sourceFile, List<String> foundClassnames, FileDependencies foundDependencies) throws BundlerProcessingException
	{
		processSourceFile(sourceFile, "", foundClassnames, foundDependencies);
	}
	
	private void processSourceFile(File sourceFile, String classname, List<String> foundClassnames, FileDependencies foundDependencies) throws BundlerProcessingException
	{
		logger.debug("processing file '" + sourceFile.getAbsolutePath() + "'");
		
		if (sourceFile.isHidden() || sourceFile.getName().charAt(0) == '.')
			return;
		
		try(Reader reader = BundlerFileReaderFactory.getBundlerFileReader(sourceFile))
		{
			String sourceFileExtension = StringUtils.substringAfterLast(sourceFile.getName(), ".");
			processDependencies(sourceFile, new BufferedReader(new JsCommentStrippingReader(reader, false)), foundClassnames, foundDependencies, sourceFileExtension);

			if (classDictionary.lookup(sourceFile) != null)
			{
				foundDependencies.getSourceFiles().add(sourceFile);
				if (!classname.equals(""))
				{
					appendPatchFileIfExists(foundDependencies.getSourceFiles(), classname);
				}
			}
		}
		catch (IOException e)
		{
			throw new UnknownBundlerException(e);
		}
	}

	private void appendPatchFileIfExists(List<File> sourceFiles, String classname)
	{
		if (patchDictionary != null && (patchDictionary.contains(classname)))
		{
			sourceFiles.add(patchDictionary.lookup(classname));
		}
	}

	// Process dependencies recursively using a depth first approach
	// to minimise memory usage of the write buffers.
	// Can't emit a class until all its static dependencies are written.
	private void processDependencies(File sourceFile, Reader reader, List<String> foundClassnames, FileDependencies foundDependencies, String sourceFileExtension) throws BundlerProcessingException
	{
		DependencyFinder finder = new DependencyFinder(classTrie, sourceFileExtension);
		int latest;
		
		try
		{
			while ((latest = reader.read()) != -1)
			{
				char latestChar = (char) latest;
				
				processClassChar( foundClassnames, foundDependencies, finder, latestChar );
			}
			
			processClassChar( foundClassnames, foundDependencies, finder, '\n' );
		}
		catch (IOException e)
		{
			throw new BundlerProcessingException(e, "Error while processing the file " + sourceFile.getAbsolutePath());
		}
	}

	private void processClassChar( List<String> foundClassnames, FileDependencies foundDependencies, DependencyFinder finder, char latestChar )
			throws IOException, BundlerProcessingException
	{
		Match matched = finder.next( latestChar );
		
		if ( matched == null )
		{
			return;
		}
		
		if ( matched.isStaticDependency() )
		{
			processStaticDependency( foundClassnames, foundDependencies, matched );
		}
		else if( matched.isThirdPartyDependency() )
		{
			processThirdPartyDependency( foundDependencies, matched );
		}
		else if( matched.isAlias() )
		{
			processAlias( foundClassnames, foundDependencies, matched );
		}
		else
		{
			processMatchedClass( foundClassnames, foundDependencies, matched );
		}
	}

	private void processStaticDependency( List<String> foundClassnames, FileDependencies foundDependencies, Match matched )	throws BundlerProcessingException
	{
		String className = matched.getDependencyName();
		
		logger.debug( "found static class dependency: " + className );
		
		processClass( className, foundClassnames, foundDependencies, matched );
	}

	private void processThirdPartyDependency( FileDependencies foundDependencies, Match matched )
	{
		String thirdPartyLibrary = matched.getDependencyName();
		
		logger.debug( "found thirdpary library dependency: " + thirdPartyLibrary );
		
		analyser.add( matched );
		analyser.addCompleted();
		
		if( !foundDependencies.getThirdPartyLibraries().contains( thirdPartyLibrary ) )
		{
			foundDependencies.getThirdPartyLibraries().add( thirdPartyLibrary );
		}
	}

	private void processAlias( List<String> foundClassnames, FileDependencies foundDependencies, Match matched ) throws BundlerProcessingException
	{
		String qualifiedAlias = matched.getDependencyName();
		AliasInformation aliasInformation = this.classTrie.getAliasInformation( qualifiedAlias );
		
		String aliasName = aliasInformation.getAliasName();
		AliasDefinition matchedAliasDefinition = aliasInformation.getAliasDefinition();
		
		processMatchedClass( foundClassnames, foundDependencies, matched );
		processClass(matchedAliasDefinition.getInterfaceName(), foundClassnames, foundDependencies, null);
		matchedAliases.addAlias(aliasName, new AliasDefinition(matchedAliasDefinition));
	}
	
	private void processMatchedClass( List<String> foundClassnames, FileDependencies foundDependencies, Match matched ) throws BundlerProcessingException
	{
		String className = matched.getDependencyName();
		
		logger.debug( "found class dependency: " + className );
		
		// if we are using an analyser then process all class dependencies, even if they've been processed before
		if(!(analyser instanceof NullCodeAnalyser))
		{
			processClass( className, foundClassnames, foundDependencies, matched );
		}
		else
		{
			if ( !processedClasses.contains( className ) )
			{
				pendingClasses.add(className);
			}
		}
	}
}
