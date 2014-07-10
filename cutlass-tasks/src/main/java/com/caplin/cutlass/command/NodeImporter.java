package com.caplin.cutlass.command;

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
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.plugin.AssetLocationPlugin;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetPlugin;
import org.bladerunnerjs.plugin.utility.PluginLoader;
import org.bladerunnerjs.testing.utility.MockAppVersionGenerator;
import org.bladerunnerjs.testing.utility.MockPluginLocator;
import org.bladerunnerjs.testing.utility.StubLoggerFactory;
import org.bladerunnerjs.utility.filemodification.OptimisticFileModificationService;
import org.mockito.Mockito;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;


public class NodeImporter {
	private static final String[] textBasedFileExtensions = {"txt", "js", "xml", "properties", "bundle", "conf", "css", "html", "jsp", "java"};
	
	public static void importApp(File sourceAppDir, String sourceAppRequirePrefix, App targetApp, String targetAppRequirePrefix) throws InvalidSdkDirectoryException, IOException, ConfigException {
		BRJS tempBrjs = createTemporaryBRJSModel();
		App tempBrjsApp = tempBrjs.app(targetApp.getName());
		
		FileUtils.copyDirectory(sourceAppDir, tempBrjsApp.dir());
		tempBrjsApp.appConf().setRequirePrefix(targetAppRequirePrefix);
		tempBrjsApp.appConf().write();
		
		for(Aspect aspect : tempBrjsApp.aspects()) {
			renameAspect(aspect, sourceAppRequirePrefix, targetAppRequirePrefix);
		}
		
		for(Bladeset bladeset : tempBrjsApp.bladesets()) {
			renameBladeset(bladeset, sourceAppRequirePrefix + "/" + bladeset.getName());
		}
		
		FileUtils.moveDirectory(tempBrjsApp.dir(), targetApp.dir());
	}
	
	public static void importBladeset(File sourceBladesetDir, String sourceBladesetRequirePrefix, Bladeset targetBladeset) throws InvalidSdkDirectoryException, IOException, ConfigException {
		BRJS tempBrjs = createTemporaryBRJSModel();
		App tempBrjsApp = tempBrjs.app(targetBladeset.app().getName());
		Bladeset tempBrjsBladeset = tempBrjsApp.bladeset(targetBladeset.getName());
		
		FileUtils.copyDirectory(sourceBladesetDir, tempBrjsBladeset.dir());
		tempBrjsApp.appConf().setRequirePrefix(targetBladeset.app().getRequirePrefix());
		
		renameBladeset(tempBrjsBladeset, sourceBladesetRequirePrefix);
		FileUtils.moveDirectory(tempBrjsBladeset.dir(), targetBladeset.dir());
	}
	
	private static BRJS createTemporaryBRJSModel() throws InvalidSdkDirectoryException, IOException {
		MockPluginLocator pluginLocator = new MockPluginLocator();
		pluginLocator.assetLocationPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetLocationPlugin.class, VirtualProxyAssetLocationPlugin.class));
		pluginLocator.assetPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetPlugin.class, VirtualProxyAssetPlugin.class));
		
		return new BRJS(FileUtility.createTemporaryDirectory("node-importer"), pluginLocator, new OptimisticFileModificationService(), new StubLoggerFactory(), new MockAppVersionGenerator());
	}
	
	private static void renameAspect(Aspect aspect, String sourceAppRequirePrefix, String targetAppRequirePrefix) throws IOException {
		findAndReplaceInAllTextFiles(aspect.dir(), sourceAppRequirePrefix, targetAppRequirePrefix);
		moveSrcDirectoryContentToNewNamespaceDirectoryStructure(sourceAppRequirePrefix, targetAppRequirePrefix, aspect.dir());
	}
	
	private static void renameBladeset(Bladeset bladeset, String sourceBladesetRequirePrefix) throws IOException {
		findAndReplaceInAllTextFiles(bladeset.dir(), sourceBladesetRequirePrefix, bladeset.requirePrefix());
		renameSrcSubfoldersAndBladeOrBladesetDirectory(sourceBladesetRequirePrefix, bladeset.requirePrefix(), bladeset.dir(), bladeset.getName() + CutlassConfig.BLADESET_SUFFIX);
		
		for(Blade blade : bladeset.blades()) {
			renameBlade(blade, sourceBladesetRequirePrefix + "/" + blade.getName());
		}
	}
	
	private static void renameBlade(Blade blade, String sourceBladeRequirePrefix) throws IOException {
		renameSrcSubfoldersAndBladeOrBladesetDirectory(sourceBladeRequirePrefix, blade.requirePrefix(), blade.dir(), blade.getName());
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
	
	private static void renameDirectory(File directoryToRename, String newDirectoryName) throws IOException
	{
		if(!directoryToRename.getName().equals(newDirectoryName))
		{
			File newFolder = new File(directoryToRename.getParentFile(), newDirectoryName);
			FileUtils.moveDirectory(directoryToRename, newFolder);
		}
	}
}
