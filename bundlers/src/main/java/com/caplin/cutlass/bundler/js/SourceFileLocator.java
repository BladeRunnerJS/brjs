package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.caplin.cutlass.bundler.BladeRunnerSourceFileProvider;
import com.caplin.cutlass.bundler.BundlerFileUtils;
import com.caplin.cutlass.bundler.ThirdPartyLibraryFinder;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public class SourceFileLocator
{
	public static final IOFileFilter SEED_FILENAME_FILTER = new SuffixFileFilter(Arrays.asList(".js", ".xml", ".html", ".htm", ".jsp"));
	public static final IOFileFilter SOURCE_FILENAME_FILTER = new SuffixFileFilter(Arrays.asList(".js"));
	public static final String JS_FILE_EXT = ".js";
	
	public static List<String> getThirdPartyLibraryNames(File baseDir)
	{
		ThirdPartyLibraryFinder libraryFinder = new ThirdPartyLibraryFinder();
		Map<String, File> librariesToDirectories = libraryFinder.getThirdPartyLibraryDirectories(baseDir);
		return new ArrayList<String>(librariesToDirectories.keySet());
	}

	public static List<ClassnameFileMapping> getAllSourceFiles(File baseDir, File testDir ) throws ContentProcessingException
	{
		BladeRunnerSourceFileProvider sourceFileProvider = new BladeRunnerSourceFileProvider(new JsSourceBundlerFileAppender());
		
		JsNamespaceVerifier namespaceVerifier = new JsNamespaceVerifier();
		
		try 
		{
			namespaceVerifier.verifyThatJsFilesAreInCorrectNamespace(baseDir);
		}
		catch (NamespaceException ex)
		{
			throw new ContentProcessingException(ex);
		}
		
		List<File> sourceFileDirs = sourceFileProvider.getSourceFiles(baseDir, testDir);
		Set<ClassnameFileMapping> classnameFileMappings = getClassNameMappings(sourceFileDirs, SOURCE_FILENAME_FILTER);
		return new ArrayList<ClassnameFileMapping>(classnameFileMappings);
	}
	
	public static List<ClassnameFileMapping> getAllPatchFiles(File patchesDir) throws ContentProcessingException
	{		
		if (patchesDir == null || !patchesDir.isDirectory())
		{
			return new ArrayList<ClassnameFileMapping>();
		}
		List<File> patchDirs = Arrays.asList(patchesDir);
		Set<ClassnameFileMapping> classnameFileMappings = getClassNameMappings(patchDirs, SOURCE_FILENAME_FILTER);
		return new ArrayList<ClassnameFileMapping>(classnameFileMappings);
	}
	
	public static List<File> getAllSeedFiles(File baseDir, File testDir) throws ContentProcessingException
	{
		BladeRunnerSourceFileProvider seedFileProvider = new BladeRunnerSourceFileProvider(new JsSeedBundlerFileAppender());
		
		List<File> seedFiles = new ArrayList<File>();
		List<File> seedFileDirs = seedFileProvider.getSourceFiles(baseDir, testDir);
		
		Set<ClassnameFileMapping> classnameFileMappings = getClassNameMappings(seedFileDirs, SEED_FILENAME_FILTER);
		for (ClassnameFileMapping mapping : classnameFileMappings)
		{
			seedFiles.add(mapping.getFile());
		}
		File[] seedFilesArray = seedFiles.toArray(new File[0]);
		ArrayUtils.reverse(seedFilesArray);
		return Arrays.asList(seedFilesArray);
	}
	
	public static Set<ClassnameFileMapping> getClassNameMappings(List<File> rootDirs, IOFileFilter filter)
	{
		Set<ClassnameFileMapping> classnameFileMappings = new LinkedHashSet<ClassnameFileMapping>();
		for (File root : rootDirs)
		{
			List<File> calculatedFiles = BundlerFileUtils.recursiveListFiles(root, filter);
			addToClassnameFileMappingSet(classnameFileMappings, root, calculatedFiles);
		}
		return classnameFileMappings;
	}
	
	private static void addToClassnameFileMappingSet(Set<ClassnameFileMapping> theSet, File classnameBase, List<File> files)
	{
		for (File thisFile : files)
		{
			String classname = generateClassnameFromFile(classnameBase, thisFile);
			theSet.add(new ClassnameFileMapping(classname, thisFile));
		}
	}
	
	private static String generateClassnameFromFile(File root, File file)
	{
		String rootPath = root.getAbsolutePath().replace('\\', '/');
		String filePath = file.getAbsolutePath().replace('\\', '/');
		String classnamePath = filePath.substring(rootPath.length());
		String classFilePath = classnamePath;
		if (classFilePath.startsWith("/"))
		{
			classFilePath = classFilePath.replaceFirst("/", "");
		}
		if (classFilePath.endsWith(JS_FILE_EXT))
		{
			classFilePath = StringUtils.removeEnd(classFilePath, JS_FILE_EXT);
			String result = classFilePath.replace('/', '.');
			return result;
		}
		return "";
	}
	
	public static Set<String> createValidClasses(List<ClassnameFileMapping> sourceFiles)
	{
		Set<String> validClasses = new HashSet<>();
		
		for(ClassnameFileMapping classFileMapping : sourceFiles)
		{
			validClasses.add(classFileMapping.getClassname());
		}
		
		return validClasses;
	}
}