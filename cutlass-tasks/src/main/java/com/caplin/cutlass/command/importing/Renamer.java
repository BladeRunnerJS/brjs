package com.caplin.cutlass.command.importing;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.model.exception.ConfigException;
import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.conf.AppConf;
import com.caplin.cutlass.exception.NamespaceException;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.NamespaceCalculator;

public class Renamer
{
	private static final String[] textBasedFileExtensions = {"txt", "js", "xml", "properties", "bundle", "conf", "css", "html", "jsp", "java"};
	
	public static void renameApplication(File applicationDirectory, String oldNamespace, String newNamespace, String oldAppName, String newAppName) throws IOException, NamespaceException, ConfigException
	{
		if (!newNamespace.equals(oldNamespace))
		{
			AppConf appConf = null;
			try {
				/* try to use the exisitng app conf to keep all of the current config options */
				appConf = AppConf.getConf(applicationDirectory);
			} 
			catch (Exception ex)
			{
				/* no app.conf for renamed app (its from a template) so create a new app conf with default values */
				appConf = new AppConf(oldNamespace, CutlassConfig.DEFAULT_APP_LOCALES);
			}
			appConf.appNamespace = newNamespace;
			AppConf.writeConf(applicationDirectory, appConf);
		}
		
		for(File bladeset : CutlassDirectoryLocator.getChildBladesets(applicationDirectory))
		{
			String bladesetNamespace = "." + NamespaceCalculator.getBladesetNamespace(bladeset);
			renameBladeset(bladeset, oldNamespace + bladesetNamespace, newNamespace + bladesetNamespace);
		}
		
		for(File aspect : CutlassDirectoryLocator.getApplicationAspects(applicationDirectory))
		{
			renameAspect(aspect, oldNamespace, newNamespace);
		}
		
		renameDatabaseUrl(applicationDirectory, oldAppName, newAppName);
		renameDirectory(applicationDirectory, newAppName);
	}

	public static void renameAspect(File aspectDir, String oldNamespace, String newNamespace) throws IOException 
	{
		findAndReplaceInAllTextFiles(aspectDir, oldNamespace, newNamespace);
		moveSrcDirectoryContentToNewNamespaceDirectoryStructure(oldNamespace, newNamespace, aspectDir);
	}

	public static void renameBladeset(File bladesetDirectory, String oldBladesetNamespace, String newBladesetNamespace) throws IOException
	{
		for(File blade : CutlassDirectoryLocator.getChildBlades(bladesetDirectory))
		{
			String bladeNamespace = "." + blade.getName();
			renameSrcSubfoldersAndBladeOrBladesetDirectory(oldBladesetNamespace + bladeNamespace, newBladesetNamespace + bladeNamespace, blade, blade.getName());
		}
		
		findAndReplaceInAllTextFiles(bladesetDirectory, oldBladesetNamespace, newBladesetNamespace);
		renameSrcSubfoldersAndBladeOrBladesetDirectory(oldBladesetNamespace, newBladesetNamespace, bladesetDirectory, newBladesetNamespace.split("\\.")[1] + CutlassConfig.BLADESET_SUFFIX);
	}
	
	public static void renameBlade(File bladeDirectory, final String oldBladeNamespace, final String newBladeNamespace) throws IOException
	{
		findAndReplaceInAllTextFiles(bladeDirectory, oldBladeNamespace, newBladeNamespace);
		renameSrcSubfoldersAndBladeOrBladesetDirectory(oldBladeNamespace, newBladeNamespace, bladeDirectory, newBladeNamespace.split("\\.")[2]);
	}
	
	private static void renameSrcSubfoldersAndBladeOrBladesetDirectory(String oldNamespace, String newNamespace, File resource, String newResourceFolderName) throws IOException
	{
		moveSrcDirectoryContentToNewNamespaceDirectoryStructure(oldNamespace, newNamespace, resource);
		renameDirectory(resource, newResourceFolderName);
	}
	
	private static void moveSrcDirectoryContentToNewNamespaceDirectoryStructure(String oldNamespace, String newNamespace, File resourceThatCanHoldSrcDirectory)	throws IOException
	{
		List<File> srcFolders = new ArrayList<File>();
		
		for (String dirName : CutlassConfig.POSSIBLE_SRC_DIR_NAMES)
		{
			srcFolders.addAll( FileUtility.getAllFilesAndFoldersMatchingFilterIncludingSubdirectories(resourceThatCanHoldSrcDirectory, 
					(FileFilter) new AndFileFilter(new NameFileFilter(dirName), DirectoryFileFilter.INSTANCE)) );			
		}
		
		for(File srcFolder : srcFolders)
		{
			if (srcFolder.listFiles().length > 0)
			{
				moveNamespaceDirectory(srcFolder, oldNamespace.replace('.', '/'), newNamespace.replace('.', '/'));
			}
		}
	}
	
	private static void moveNamespaceDirectory(File srcDirectory, String oldNamespace, String newNamespace) throws IOException
	{
		File oldNamespaceDirectory = new File(srcDirectory, oldNamespace);
		File newNamespaceDirectory = new File(srcDirectory, newNamespace);
		File oldApplicationNamespaceDirectory = srcDirectory.listFiles()[0];
		
		boolean namespaceIsTheSame = oldNamespace.equalsIgnoreCase(newNamespace);
		
		if(oldNamespaceDirectory.exists() && !namespaceIsTheSame)
		{
			FileUtils.moveDirectory(oldNamespaceDirectory, newNamespaceDirectory);
		}
		
		FileUtility.recursivelyDeleteEmptyDirectories(oldApplicationNamespaceDirectory);
	}
	
	private static HashMap<String, String> getReplaceMap(String oldNamespace, String newNamespace) {
		HashMap<String,String> replaceMap = new HashMap<String,String>();
		replaceMap.put("^"+oldNamespace, newNamespace);
		
		replaceMap.put("([\\W_])"+Pattern.quote(oldNamespace), "$1"+newNamespace.replace("$", "\\$"));
		replaceMap.put("([\\W_])"+Pattern.quote(oldNamespace.replace('.', '/')), "$1"+newNamespace.replace('.', '/').replace("$", "\\$"));
		
		return replaceMap;
	}
	
	private static String findAndReplaceInText(String content, HashMap<String,String> replaceMap) {
		for (String find : replaceMap.keySet())
		{
			String replace = replaceMap.get(find);
			content = content.replaceAll(find, replace);	
		}
		return content;
	}
	
	private static void findAndReplaceInAllTextFiles(File rootRenameDirectory, String oldNamespace, String newNamespace) throws IOException
	{
		HashMap<String, String> replaceMap = getReplaceMap(oldNamespace, newNamespace);
		for(File file : FileUtils.listFiles(rootRenameDirectory, textBasedFileExtensions, true))
		{
			String content = FileUtils.readFileToString(file);
			content = findAndReplaceInText(content, replaceMap);
			FileUtils.writeStringToFile(file, content);
		}
	}
	
	private static void renameDatabaseUrl(File existingAppDir, String oldAppName, String newAppName) throws IOException
	{
		File jettyEnvXml = new File(existingAppDir, "WEB-INF/jetty-env.xml");
		String oldDatabaseUrl = "/" +  oldAppName + "/" + oldAppName;
		String newDatabaseUrl = "/" +  newAppName + "/" + newAppName;
		if (jettyEnvXml.exists())
		{
			String content = FileUtils.readFileToString(jettyEnvXml);
			content = StringUtils.replace(content, oldDatabaseUrl, newDatabaseUrl);
			FileUtils.writeStringToFile(jettyEnvXml, content);
		}
	}

	private static void renameDirectory(File directoryToRename, String newDirectoryName) throws IOException
	{
		if(!directoryToRename.getName().equals(newDirectoryName))
		{
			File newFolder = new File(directoryToRename.getParentFile(), newDirectoryName);
			FileUtils.moveDirectory(directoryToRename, newFolder);
		}
	}
}
