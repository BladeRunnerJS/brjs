package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.memoization.FileModificationRegistry;
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
import org.bladerunnerjs.utility.JsStyleUtility;
import org.bladerunnerjs.utility.filemodification.RealTimeAccessor;
import org.mockito.Mockito;


public class NodeImporter {
	
	public static void importAppFromZip(ZipFile sourceAppZip, App targetApp, String targetAppRequirePrefix) throws InvalidSdkDirectoryException, IOException, ConfigException {
		BRJS tempBrjs = createTemporaryBRJSModel();
		
		File temporaryUnzipDir = FileUtility.createTemporaryDirectory( NodeImporter.class, targetApp.getName() );
		FileUtility.unzip(sourceAppZip, temporaryUnzipDir );
		File[] temporaryUnzipDirFiles = temporaryUnzipDir.listFiles();
		if (temporaryUnzipDirFiles.length != 1) {
			throw new IOException("Exepected to find 1 folder inside the provided zip, there was " + temporaryUnzipDirFiles.length);
		}
		
		App sourceApp = tempBrjs.app( targetApp.getName() );
		FileUtils.moveDirectory(temporaryUnzipDirFiles[0], sourceApp.dir());
		
		File unzippedLibDir = sourceApp.file("WEB-INF/lib");
		FileUtils.copyDirectory(targetApp.root().appJars().dir(), unzippedLibDir);
		
		importApp(tempBrjs, sourceApp, targetApp, targetAppRequirePrefix);
	}
	
	public static void importApp(BRJS tempBrjs, App sourceApp, App targetApp, String targetAppRequirePrefix) throws InvalidSdkDirectoryException, IOException, ConfigException {
		App tempBrjsApp = tempBrjs.app(targetApp.getName());
		String sourceAppRequirePrefix = sourceApp.getRequirePrefix();
		
		tempBrjsApp.appConf().setRequirePrefix(targetAppRequirePrefix);
		tempBrjsApp.appConf().write();
		
		
		for(Aspect aspect : tempBrjsApp.aspects()) {
			updateRequirePrefix(aspect.assetLocations(), sourceAppRequirePrefix, sourceAppRequirePrefix, targetAppRequirePrefix);			
			renameTestLocations(aspect.testTypes(), sourceAppRequirePrefix, sourceAppRequirePrefix, targetAppRequirePrefix);
		}
		
		for(Bladeset bladeset : tempBrjsApp.bladesets()) {
			renameBladeset(bladeset, sourceAppRequirePrefix, sourceAppRequirePrefix + "/" + bladeset.getName());
		}
		
		FileUtils.moveDirectory(tempBrjsApp.dir(), targetApp.dir());
	}
	
	
	public static void importBladeset(File sourceBladesetDir, String sourceAppRequirePrefix, String sourceBladesetRequirePrefix, Bladeset targetBladeset) throws InvalidSdkDirectoryException, IOException, ConfigException {
		BRJS tempBrjs = createTemporaryBRJSModel();
		App tempBrjsApp = tempBrjs.app(targetBladeset.app().getName());
		Bladeset tempBrjsBladeset = tempBrjsApp.bladeset(targetBladeset.getName());
		
		FileUtils.copyDirectory(sourceBladesetDir, tempBrjsBladeset.dir());
		tempBrjsApp.appConf().setRequirePrefix(targetBladeset.app().getRequirePrefix());
		tempBrjsApp.appConf().write();
		
		if(!JsStyleUtility.getJsStyle(sourceBladesetDir).equals(JsStyleUtility.getJsStyle(targetBladeset.dir()))) {
			JsStyleUtility.setJsStyle(tempBrjsBladeset.dir(), JsStyleUtility.getJsStyle(sourceBladesetDir));
		}
		
		renameBladeset(tempBrjsBladeset, sourceAppRequirePrefix, sourceBladesetRequirePrefix);
		FileUtils.moveDirectory(tempBrjsBladeset.dir(), targetBladeset.dir());
	}
	
	private static BRJS createTemporaryBRJSModel() throws InvalidSdkDirectoryException, IOException {
		File tempSdkDir = FileUtility.createTemporaryDirectory(NodeImporter.class);
		new File(tempSdkDir, "sdk").mkdir();
		MockPluginLocator pluginLocator = new MockPluginLocator();
		pluginLocator.assetLocationPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetLocationPlugin.class, VirtualProxyAssetLocationPlugin.class));
		pluginLocator.assetPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetPlugin.class, VirtualProxyAssetPlugin.class));
		
		BRJS brjs = new BRJS(tempSdkDir, pluginLocator, new StubLoggerFactory(), new RealTimeAccessor(), new MockAppVersionGenerator(), new FileModificationRegistry());
		
		return brjs;
	}
	
	private static void renameBladeset(Bladeset bladeset, String sourceAppRequirePrefix, String sourceBladesetRequirePrefix) throws IOException {
		updateRequirePrefix(bladeset.assetLocations(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
		
		renameTestLocations(bladeset.testTypes(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
		
		for(Blade blade : bladeset.blades()) {
			updateRequirePrefix(blade.assetLocations(), sourceAppRequirePrefix, sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());
			
			renameTestLocations(blade.testTypes(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
			
			Workbench workbench = blade.workbench();			
			updateRequirePrefix(workbench.assetLocations(), sourceAppRequirePrefix, sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());			
		}
	}
	
	private static void renameTestLocations(List<TypedTestPack> testTypes, String sourceAppRequirePrefix, String sourceLocationRequirePrefix, String requirePrefix) throws IOException{
		for(TypedTestPack typedTestPack : testTypes)
		{
			for( TestPack testPack : typedTestPack.testTechs()){
				updateRequirePrefix(testPack.assetLocations(), sourceAppRequirePrefix, sourceLocationRequirePrefix, requirePrefix);
			}
		}		
	}
	
	private static void updateRequirePrefix(List<AssetLocation> assetLocations, String sourceAppRequirePrefix, String sourceRequirePrefix, String targetRequirePrefix) throws IOException {
		if(!sourceRequirePrefix.equals(targetRequirePrefix)) {
			for(AssetLocation assetLocation : assetLocations) {
				if(assetLocation.dir().exists()) {
					if(assetLocation.file(sourceRequirePrefix).exists()) {
						FileUtils.moveDirectory(assetLocation.file(sourceRequirePrefix), assetLocation.file(targetRequirePrefix));
						if (!targetRequirePrefix.startsWith(sourceAppRequirePrefix) && assetLocation.file(sourceAppRequirePrefix).exists()) {
							FileUtils.deleteDirectory( assetLocation.file(sourceAppRequirePrefix) );
						}
					}
					
					findAndReplaceInAllTextFiles(assetLocation.dir(), sourceRequirePrefix, targetRequirePrefix);
				}
			}
		}
	}
	
	private static void findAndReplaceInAllTextFiles(File rootRenameDirectory, String sourceRequirePrefix, String targetRequirePrefix) throws IOException
	{
		HashMap<String, String> replaceMap = getReplaceMap(sourceRequirePrefix, targetRequirePrefix);
		for(File file : FileUtils.listFiles(rootRenameDirectory, null, true))
		{
			String content = FileUtils.readFileToString(file);
			String updatedContent = findAndReplaceInText(content, replaceMap);
			
			if(content != updatedContent) {
				FileUtils.writeStringToFile(file, updatedContent);
			}
		}
	}
	
	private static HashMap<String, String> getReplaceMap(String oldRequirePrefix, String newRequirePrefix) {
		HashMap<String,String> replaceMap = new HashMap<String,String>();
		String oldNamespace = oldRequirePrefix.replace('/', '.');
		String newNamespace = newRequirePrefix.replace('/', '.');
		
		replaceMap.put("^" + oldNamespace, newNamespace);
		replaceMap.put("([\\W_])"+Pattern.quote(oldNamespace), "$1" + newNamespace.replace("$", "\\$"));
		replaceMap.put("([\\W_])"+Pattern.quote(oldRequirePrefix), "$1" + newRequirePrefix.replace("$", "\\$"));
		
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
