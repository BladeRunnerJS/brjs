package org.bladerunnerjs.model;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.BladerunnerConf;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.logging.LoggerFactory;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.InvalidSdkDirectoryException;
import org.bladerunnerjs.api.plugin.AssetPlugin;
import org.bladerunnerjs.api.spec.utility.MockAppVersionGenerator;
import org.bladerunnerjs.api.spec.utility.MockPluginLocator;
import org.bladerunnerjs.api.spec.utility.StubLoggerFactory;

import org.bladerunnerjs.logging.SLF4JLogger;

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
    			throw new IOException("Expected to find 1 folder inside the provided zip, there was " + temporaryUnzipDirFiles.length);
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
			updateRequirePrefixInRootFiles(aspect, sourceAppRequirePrefix);
		}
		
		for(Bladeset bladeset : tempBrjsApp.bladesets()) {
			String sourceBladesetRequirePrefix;
			if (bladeset == bladeset.app().defaultBladeset() && !bladeset.dir().getName().endsWith("-bladeset")) {
				sourceBladesetRequirePrefix = sourceAppRequirePrefix;				
			} else {
				sourceBladesetRequirePrefix = sourceAppRequirePrefix + "/" + bladeset.getName();
			}
			renameBladeset(bladeset, sourceAppRequirePrefix, sourceBladesetRequirePrefix);
		}

		File jettyEnv = tempBrjsApp.file("WEB-INF/jetty-env.xml");
		if (jettyEnv.isFile()) {
			String jettyXmlContent = org.apache.commons.io.FileUtils.readFileToString(jettyEnv, targetApp.root().bladerunnerConf().getDefaultFileCharacterEncoding());
			String newJettyXmlContent = StringUtils.replacePattern(jettyXmlContent, "([ /;])"+oldAppName+"([ /;])", "$1"+targetApp.getName()+"$2");
			if (!jettyXmlContent.equals(newJettyXmlContent)) {
				org.apache.commons.io.FileUtils.write(jettyEnv, newJettyXmlContent);
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
    		pluginLocator.assetPlugins.addAll(PluginLoader.createPluginsOfType(Mockito.mock(BRJS.class), AssetPlugin.class, VirtualProxyAssetPlugin.class));
    		brjs = new BRJS(tempSdkDir, tempSdkDir, pluginLocator, new StubLoggerFactory(), new MockAppVersionGenerator());
		} finally {
			org.apache.commons.io.FileUtils.deleteQuietly(tempSdkDir);
		}
		return brjs;
	}
	
	private static void renameBladeset(Bladeset bladeset, String sourceAppRequirePrefix, String sourceBladesetRequirePrefix) throws IOException, ConfigException {
		updateRequirePrefix(bladeset, sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
		
		renameTestLocations(bladeset.testTypes(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
		updateRequirePrefixInRootFiles(bladeset.workbench(), sourceAppRequirePrefix);
		
		for(Blade blade : bladeset.blades()) {
			updateRequirePrefix(blade, sourceAppRequirePrefix, sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());
			
			renameTestLocations(blade.testTypes(), sourceAppRequirePrefix, sourceBladesetRequirePrefix, bladeset.requirePrefix());
			
			BladeWorkbench workbench = blade.workbench();			
			updateRequirePrefix(workbench, sourceAppRequirePrefix, sourceBladesetRequirePrefix + "/" + blade.getName(), blade.requirePrefix());
			updateRequirePrefixInRootFiles(workbench, sourceAppRequirePrefix);
		}
	}
	
	private static void renameTestLocations(List<TypedTestPack> testTypes, String sourceAppRequirePrefix, String sourceLocationRequirePrefix, String requirePrefix) throws IOException, ConfigException {
		
		for(TypedTestPack typedTestPack : testTypes)
		{
			for( TestPack testPack : typedTestPack.testTechs()){
				updateRequirePrefix(testPack, sourceAppRequirePrefix, sourceLocationRequirePrefix, requirePrefix);
			}
		}		
	}
	
	private static void updateRequirePrefix(AssetContainer assetContainer, String sourceAppRequirePrefix, String sourceRequirePrefix, String targetRequirePrefix) throws IOException, ConfigException {
		BRJS brjs = assetContainer.root();
		if(!sourceRequirePrefix.equals(targetRequirePrefix)) {
			for (String updatePrefixForLocation : Arrays.asList("src", "src-test", "tests", "resources")) {
				MemoizedFile updateRequirePrefixForLocationDir = assetContainer.file(updatePrefixForLocation);
				MemoizedFile sourceRequirePrefixDir = updateRequirePrefixForLocationDir.file(sourceRequirePrefix);
				MemoizedFile targetRequirePrefixDir = updateRequirePrefixForLocationDir.file(targetRequirePrefix);
				if (sourceRequirePrefixDir.isDirectory()) {
					FileUtils.moveDirectory(sourceRequirePrefixDir, targetRequirePrefixDir);
					MemoizedFile sourceAppRequirePrefixDir = assetContainer.file(updatePrefixForLocation+"/"+sourceAppRequirePrefix);
					if (!targetRequirePrefix.startsWith(sourceAppRequirePrefix) && sourceAppRequirePrefixDir.exists()) {
						FileUtils.deleteDirectory(sourceAppRequirePrefixDir);
					}
				}
				if (updateRequirePrefixForLocationDir.isDirectory()) {
					findAndReplaceInAllTextFiles(brjs, updateRequirePrefixForLocationDir, sourceRequirePrefix, targetRequirePrefix);
				}
			}
		}
	}
	
	private static void updateRequirePrefixInRootFiles(BundlableNode browsableNode, String sourceAppRequirePrefix) throws IOException, ConfigException {
		File[] rootHtmlFiles = browsableNode.dir().getUnderlyingFile().listFiles( (FileFilter)new SuffixFileFilter(".html"));
		if (rootHtmlFiles != null) {
			findAndReplaceInTextFiles(browsableNode.root(), Arrays.asList(rootHtmlFiles), 
					sourceAppRequirePrefix, browsableNode.requirePrefix());
		}
	}
	
	private static void findAndReplaceInAllTextFiles(BRJS brjs, File rootRenameDirectory, String sourceRequirePrefix, String targetRequirePrefix) throws IOException, ConfigException
	{
		IOFileFilter dontMatchWebInfDirFilter = new NotFileFilter( new NameFileFilter("WEB-INF") );
		Collection<File> findAndReplaceFiles = FileUtils.listFiles(rootRenameDirectory, TrueFileFilter.INSTANCE, dontMatchWebInfDirFilter);
		findAndReplaceInTextFiles(brjs, findAndReplaceFiles, sourceRequirePrefix, targetRequirePrefix);
	}
	
	private static void findAndReplaceInTextFiles(BRJS brjs, Collection<File> files, String sourceRequirePrefix, String targetRequirePrefix) throws IOException, ConfigException
	{
		for (File f : files) {
			if (f.length() != 0) {
				findAndReplaceInTextFile(brjs, f, sourceRequirePrefix, targetRequirePrefix);
			}
		}
	}
	
	private static boolean isTextFile(File file) {
		return true;
	}
	
	private static void findAndReplaceInTextFile(BRJS brjs, File file, String oldRequirePrefix, String newRequirePrefix) throws IOException, ConfigException
	{
		for (String extension : ImageIO.getReaderFormatNames()) {
			if (file.getName().endsWith(extension)) {
				return; //image file
			}
		}
		
		String content = org.apache.commons.io.FileUtils.readFileToString(file, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
		String updatedContent = findAndReplaceInText(content, oldRequirePrefix, newRequirePrefix);
		
		if (!content.equals(updatedContent)) {
			FileUtils.write(brjs, file, updatedContent, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
		}
	}
	
	static String findAndReplaceInText(String content, String oldRequirePrefix, String newRequirePrefix) {
		String newNamespace = newRequirePrefix.replace('/', '.');
		String oldNamespace = oldRequirePrefix.replace("/", "[./]");
		Matcher matcher = Pattern.compile("(^|[\\W_])" + oldNamespace).matcher(content);
		StringBuffer newContent = new StringBuffer();
		int startPos = 0;
		
		while(matcher.find(startPos)) { 
			String matchedStr = matcher.group();
			newContent.append(content.substring(startPos, matcher.start()));
			if ( (matchedStr.contains("/") || content.substring(matcher.end()).startsWith("/")) && !matchedStr.contains(".") ) {
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
