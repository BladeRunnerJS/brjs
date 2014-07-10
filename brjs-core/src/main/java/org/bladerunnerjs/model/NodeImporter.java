package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
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
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filemodification.OptimisticFileModificationService;
import org.mockito.Mockito;


public class NodeImporter {
	private static final String[] textBasedFileExtensions = {"txt", "js", "xml", "properties", "bundle", "conf", "css", "html", "jsp", "java"};
	
	public static void importApp(File sourceAppDir, String sourceAppRequirePrefix, App targetApp, String targetAppRequirePrefix) throws InvalidSdkDirectoryException, IOException, ConfigException {
		BRJS tempBrjs = createTemporaryBRJSModel();
		App tempBrjsApp = tempBrjs.app(targetApp.getName());
		
		FileUtils.copyDirectory(sourceAppDir, tempBrjsApp.dir());
		tempBrjsApp.appConf().setRequirePrefix(targetAppRequirePrefix);
		tempBrjsApp.appConf().write();
		
		for(Aspect aspect : tempBrjsApp.aspects()) {
			updateRequirePrefix(aspect.assetLocations(), sourceAppRequirePrefix, targetAppRequirePrefix);
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
	
	private static void renameBladeset(Bladeset bladeset, String sourceBladesetRequirePrefix) throws IOException {
		updateRequirePrefix(bladeset.assetLocations(), sourceBladesetRequirePrefix, bladeset.requirePrefix());
		
		for(Blade blade : bladeset.blades()) {
			updateRequirePrefix(blade.assetLocations(), sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());
		}
	}
	
	private static void updateRequirePrefix(List<AssetLocation> assetLocations, String sourceRequirePrefix, String targetRequirePrefix) throws IOException {
		for(AssetLocation assetLocation : assetLocations) {
			if(assetLocation.dir().exists()) {
				if(assetLocation.file(sourceRequirePrefix).exists()) {
					FileUtils.moveDirectory(assetLocation.file(sourceRequirePrefix), assetLocation.file(targetRequirePrefix));
				}
				
				findAndReplaceInAllTextFiles(assetLocation.dir(), sourceRequirePrefix, targetRequirePrefix);
			}
		}
	}
	
	// TODO: change this so it processes all files containing the 'sourceRequirePrefix'
	private static void findAndReplaceInAllTextFiles(File rootRenameDirectory, String sourceRequirePrefix, String targetRequirePrefix) throws IOException
	{
		HashMap<String, String> replaceMap = getReplaceMap(sourceRequirePrefix, targetRequirePrefix);
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
}
