package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.reader.JsCodeBlockStrippingDependenciesReader;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class JsTestDriverBundleCreator
{

	public static final String BUNDLES_DIR_NAME = "bundles";
	private static Logger logger;
	
	public static void createRequiredBundles(BRJS brjs, MemoizedFile jsTestDriverConf)
			throws FileNotFoundException, YamlException, IOException, MalformedRequestException, ResourceNotFoundException, ContentProcessingException, ModelOperationException
	{
		logger = brjs.logger(JsTestDriverBundleCreator.class);
		File bundlesDir = new File(jsTestDriverConf.getParentFile(), BUNDLES_DIR_NAME);
		FileUtils.deleteDirectoryFromBottomUp(bundlesDir);
		FileUtils.deleteQuietly(brjs, bundlesDir);
		bundlesDir.mkdir();
		
		Map<String, Object> configMap = getMapFromYamlConfig(jsTestDriverConf);
		
		File baseDirectory = getBaseDirectory(jsTestDriverConf, configMap);
		
		TestPack testPack = brjs.locateAncestorNodeOfClass(jsTestDriverConf, TestPack.class);
		if(testPack == null){
			throw new RuntimeException("Unable to find test pack which represents the path " + jsTestDriverConf.getParentFile());
		}
		
		BundlerHandler bundlerHandler = new BundlerHandler(testPack);
		
		for (String resourceToLoad : getListOfResourcesToLoad(configMap))
		{
			File requestedFile = new File(baseDirectory, resourceToLoad);
			
			if (fileIsInBundlesDirectory(requestedFile) && isNotWildcardFilename(requestedFile))
			{
				String bundlePath = StringUtils.substringAfterLast( requestedFile.getAbsolutePath(), BUNDLES_DIR_NAME+File.separator);
				bundlePath = StringUtils.replace(bundlePath, "\\", "/");
				bundlerHandler.createBundleFile(requestedFile, bundlePath, brjs.getAppVersionGenerator().getDevVersion());
			}
		}
		for (File currentTestFile : jsTestDriverConf.getParentFile().file("tests").listFiles())
		{
			if (currentTestFile.isFile())
			{
				Matcher m = JsCodeBlockStrippingDependenciesReader.SELF_EXECUTING_FUNCTION_DEFINITION_REGEX_PATTERN.matcher(org.apache.commons.io.FileUtils.readFileToString(currentTestFile));
				if (!m.find())
				{
					logger.warn("The CommonJS test '" + currentTestFile.getName() + "' is not wrapped within an IIFE, which may cause unreliability in tests.");
				}
			}
		}
	}

	private static boolean isNotWildcardFilename(File requestedFile)
	{
		return !requestedFile.getName().contains("*");
	}

	private static Map<String, Object> getMapFromYamlConfig(File jsTestDriverConf) throws FileNotFoundException, YamlException, IOException
	{
		YamlReader reader = null;
		try {
			reader = new YamlReader(new FileReader(jsTestDriverConf));
			@SuppressWarnings("unchecked")
			Map<String, Object> configMap = (Map<String, Object>)reader.read();
			return configMap;
		}
		finally
		{
			if (reader != null)
			{
				reader.close();
			}
		}
	}
	
	private static MemoizedFile getBaseDirectory(MemoizedFile jsTestDriverConf, Map<String, Object> configMap)
	{
		MemoizedFile root = jsTestDriverConf.getParentFile();
		String relativeBasePath = (String) configMap.get("basepath");
		return root.file(relativeBasePath);
	}

	private static List<String> getListOfResourcesToLoad(Map<String, Object> configMap)
	{
		List<String> resources = new ArrayList<String>();
		resources.addAll(getSafeCollectionFromMap(configMap, "load"));
		resources.addAll(getSafeCollectionFromMap(configMap, "serve"));
		return resources;
	}

	private static boolean fileIsInBundlesDirectory(File file)
	{
		File parent = file.getParentFile();
		if(parent == null)
		{
			return false;
		}
		if(parent.getName().equals(BUNDLES_DIR_NAME))
		{
			return true;
		}
		return fileIsInBundlesDirectory(parent);
	}

	private static Collection<String> getSafeCollectionFromMap(Map<String, Object> configMap, String key)
	{
		@SuppressWarnings("unchecked")
		List<String> collection = (List<String>) configMap.get(key);
		if(collection == null)
		{
			return Collections.emptyList();
		}
		else
		{
			return collection;
		}
	}	
	
}
