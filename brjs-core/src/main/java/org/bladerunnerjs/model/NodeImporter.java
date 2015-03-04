package org.bladerunnerjs.model;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.plugin.AssetLocationPlugin;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.spec.utility.MockAppVersionGenerator;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.StubLoggerFactory;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetLocationPlugin;
import org.bladerunnerjs.plugin.proxy.VirtualProxyAssetPlugin;
import org.bladerunnerjs.plugin.utility.PluginLoader;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.JsStyleAccessor;
import org.bladerunnerjs.utility.ZipUtility;
import org.mockito.Mockito;

import com.google.common.collect.ImmutableMap;


@SuppressWarnings("unused")
public class NodeImporter {
	
	public static void importAppFromZip(ZipFile sourceAppZip, App targetApp, String targetAppRequirePrefix) throws InvalidSdkDirectoryException, IOException, ConfigException {
		BRJS tempBrjs = createTemporaryBRJSModel();
		
		File temporaryUnzipDir = FileUtils.createTemporaryDirectory( NodeImporter.class, targetApp.getName() );
		try {
    		ZipUtility.unzip(sourceAppZip, temporaryUnzipDir );
    		File[] temporaryUnzipDirFiles = temporaryUnzipDir.listFiles();
    		if (temporaryUnzipDirFiles.length != 1) {
    			throw new IOException("Exepected to find 1 folder inside the provided zip, there was " + temporaryUnzipDirFiles.length);
    		}
    		
    		App tmpBrjsSourceApp = tempBrjs.app( targetApp.getName() );
    		File unzippedAppDir = temporaryUnzipDirFiles[0];
    		FileUtils.moveDirectory(tmpBrjsSourceApp, unzippedAppDir, tmpBrjsSourceApp.dir());
    		
    		File unzippedLibDir = tmpBrjsSourceApp.file("WEB-INF/lib");
    		FileUtils.copyDirectory(targetApp, targetApp.root().appJars().dir(), unzippedLibDir);
    		
    		String sourceAppName = unzippedAppDir.getName();
    		importApp(tempBrjs, sourceAppName, tmpBrjsSourceApp, targetApp, targetAppRequirePrefix);
		} finally {
			org.apache.commons.io.FileUtils.deleteQuietly(temporaryUnzipDir);
		}
	}
	
	public static void importApp(BRJS tempBrjs, String oldAppName, App sourceApp, App targetApp, String targetAppRequirePrefix) throws InvalidSdkDirectoryException, IOException, ConfigException {
		App tempBrjsApp = tempBrjs.app(targetApp.getName());
		String sourceAppRequirePrefix = sourceApp.getRequirePrefix();
		
		tempBrjsApp.appConf().setRequirePrefix(targetAppRequirePrefix);
		tempBrjsApp.appConf().write();
		
		
		for(Aspect aspect : tempBrjsApp.aspects()) {
			updateRequirePrefix(aspect, sourceAppRequirePrefix, sourceAppRequirePrefix, targetAppRequirePrefix);
			renameTestLocations(aspect.testTypes(), sourceAppRequirePrefix, sourceAppRequirePrefix, targetAppRequirePrefix);
		}
		
		for(Bladeset bladeset : tempBrjsApp.bladesets()) {
			renameBladeset(bladeset, sourceAppRequirePrefix, sourceAppRequirePrefix + "/" + bladeset.getName());
		}

		File jettyEnv = tempBrjsApp.file("WEB-INF/jetty-env.xml");
		if (jettyEnv.isFile()) {
			String prefixAndSuffixRegex = "([ /;])";
			String jettyEnvContent = org.apache.commons.io.FileUtils.readFileToString(jettyEnv);
			Matcher matcher = Pattern.compile(prefixAndSuffixRegex+oldAppName+prefixAndSuffixRegex).matcher(jettyEnvContent);
			if (matcher.find()) {
				findAndReplaceInTextFile(tempBrjs, jettyEnv, prefixAndSuffixRegex+oldAppName+prefixAndSuffixRegex, matcher.group(1)+targetApp.getName()+matcher.group(2));
			}
			else {
				//do nothing - we are still keeping the old file content
			}
		}
		
		FileUtils.moveDirectory(tempBrjsApp.dir(), targetApp.dir());
	}
	
	public static void importBladeset(Bladeset sourceBladeset, String sourceAppRequirePrefix, String sourceBladesetRequirePrefix, Bladeset targetBladeset) throws InvalidSdkDirectoryException, IOException, ConfigException {
		MemoizedFile sourceBladesetDir = sourceBladeset.dir();
		BRJS tempBrjs = createTemporaryBRJSModel();
		App tempBrjsApp = tempBrjs.app(targetBladeset.app().getName());
		Bladeset tempBrjsBladeset = tempBrjsApp.bladeset(targetBladeset.getName());
		
		FileUtils.copyDirectory(sourceBladesetDir, tempBrjsBladeset.dir());
		tempBrjsApp.appConf().write();
		tempBrjsApp.appConf().setRequirePrefix(targetBladeset.app().getRequirePrefix());
		
		BRJS brjs = targetBladeset.root();
		if(!brjs.jsStyleAccessor().getJsStyle(sourceBladesetDir).equals(brjs.jsStyleAccessor().getJsStyle(targetBladeset.dir()))) {
			tempBrjsBladeset.root().jsStyleAccessor().setJsStyle(tempBrjsBladeset.dir(), brjs.jsStyleAccessor().getJsStyle(sourceBladesetDir));
		}
		
		renameBladeset(tempBrjsBladeset, sourceAppRequirePrefix, sourceBladesetRequirePrefix);
		FileUtils.moveDirectory(tempBrjsBladeset.dir(), targetBladeset.dir());
	}
	
	private static BRJS createTemporaryBRJSModel() throws InvalidSdkDirectoryException, IOException {
		BRJS brjs;
		File tempSdkDir = FileUtils.createTemporaryDirectory(NodeImporter.class);
		try {
    		new File(tempSdkDir, "sdk").mkdir();
    		MockPluginLocator pluginLocator = new MockPluginLocator();
    		pluginLocator.assetLocationPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetLocationPlugin.class, VirtualProxyAssetLocationPlugin.class));
    		pluginLocator.assetPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetPlugin.class, VirtualProxyAssetPlugin.class));
    		brjs = new BRJS(tempSdkDir, pluginLocator, new StubLoggerFactory(), new MockAppVersionGenerator());
		} finally {
			org.apache.commons.io.FileUtils.deleteQuietly(tempSdkDir);
		}
		return brjs;
	}
	
	private static void renameBladeset(Bladeset bladeset, String sourceAppRequirePrefix, String sourceBladesetRequirePrefix) throws IOException {
		updateRequirePrefix(bladeset, sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
		
		renameTestLocations(bladeset.testTypes(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
		
		for(Blade blade : bladeset.blades()) {
			updateRequirePrefix(blade, sourceAppRequirePrefix, sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());
			
			renameTestLocations(blade.testTypes(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
			
			BladeWorkbench workbench = blade.workbench();			
			updateRequirePrefix(workbench, sourceAppRequirePrefix, sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());			
		}
	}
	
	private static void renameTestLocations(List<TypedTestPack> testTypes, String sourceAppRequirePrefix, String sourceLocationRequirePrefix, String requirePrefix) throws IOException{
		
		for(TypedTestPack typedTestPack : testTypes)
		{
			for( TestPack testPack : typedTestPack.testTechs()){
				updateRequirePrefix(testPack, sourceAppRequirePrefix, sourceLocationRequirePrefix, requirePrefix);
			}
		}		
	}
	
	private static void updateRequirePrefix(AssetContainer assetContainer, String sourceAppRequirePrefix, String sourceRequirePrefix, String targetRequirePrefix) throws IOException {
		if(!sourceRequirePrefix.equals(targetRequirePrefix)) {
			for(AssetLocation assetLocation : assetContainer.assetLocations()) {
				if(assetLocation.dir().exists()) {
					if(assetLocation.file(sourceRequirePrefix).exists()) {
						FileUtils.moveDirectory(assetLocation.file(sourceRequirePrefix), assetLocation.file(targetRequirePrefix));
						if (!targetRequirePrefix.startsWith(sourceAppRequirePrefix) && assetLocation.file(sourceAppRequirePrefix).exists()) {
							FileUtils.deleteDirectory( assetLocation.file(sourceAppRequirePrefix) );
						}
					}
					
				}
			}
			for(AssetLocation assetLocation : assetContainer.assetLocations()) { // do this in a seperate loop since the asset locations will change when they are renamed
				if(assetLocation.dir().exists()) {
					findAndReplaceInAllTextFiles(assetLocation.root(), assetLocation.dir(), sourceRequirePrefix, targetRequirePrefix);
				}
			}
		}
	}
	
	private static void findAndReplaceInAllTextFiles(BRJS brjs, File rootRenameDirectory, String sourceRequirePrefix, String targetRequirePrefix) throws IOException
	{
		IOFileFilter dontMatchWebInfDirFilter = new NotFileFilter( new NameFileFilter("WEB-INF") );
		Collection<File> findAndReplaceFiles = FileUtils.listFiles(rootRenameDirectory, TrueFileFilter.INSTANCE, dontMatchWebInfDirFilter);
		findAndReplaceInTextFiles(brjs, findAndReplaceFiles, sourceRequirePrefix, targetRequirePrefix);
	}
	
	private static void findAndReplaceInTextFiles(BRJS brjs, Collection<File> files, String sourceRequirePrefix, String targetRequirePrefix) throws IOException
	{
		for (File f : files) {
			findAndReplaceInTextFile(brjs, f, sourceRequirePrefix, targetRequirePrefix);
		}
	}
	
	private static void findAndReplaceInTextFile(BRJS brjs, File file, String oldRequirePrefix, String newRequirePrefix) throws IOException
	{
		String content = org.apache.commons.io.FileUtils.readFileToString(file);
		String updatedContent = findAndReplaceInText(content, oldRequirePrefix, newRequirePrefix);
		
		if(content != updatedContent) {
			FileUtils.write(brjs, file, updatedContent);
		}
	}
	
	static String findAndReplaceInText(String content, String oldRequirePrefix, String newRequirePrefix) {
		if (oldRequirePrefix.endsWith("default")) { 
			oldRequirePrefix = oldRequirePrefix.substring(0, oldRequirePrefix.length() - "default".length() - 1);
		}
		String newNamespace = newRequirePrefix.replace('/', '.');
		Matcher matcher = Pattern.compile("(^|[\\W_])" + oldRequirePrefix.replace("/", "[./]")).matcher(content);
		StringBuffer newContent = new StringBuffer();
		int startPos = 0;
		
		while(matcher.find(startPos)) { 
			String matchedStr = matcher.group();
			newContent.append(content.substring(startPos, matcher.start()));
			if (matchedStr.contains("/")) {
				newContent.append(matcher.group(1) + newRequirePrefix);
			}
			else if (content.substring(matcher.end()).startsWith("/")) {
				newContent.append(matcher.group(1) + newRequirePrefix);
			}
			else {
				newContent.append(matcher.group(1) + newNamespace);
			}
			startPos = matcher.end();
		}
		newContent.append(content.substring(startPos));
		
		return newContent.toString();
	}
}
