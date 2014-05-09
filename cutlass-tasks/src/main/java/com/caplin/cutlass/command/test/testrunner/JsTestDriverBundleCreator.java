package com.caplin.cutlass.command.test.testrunner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.utility.FileUtility;

import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class JsTestDriverBundleCreator
{

	public static final String BUNDLES_DIR_NAME = "bundles";
	
	public static void createRequiredBundles(BRJS brjs, File jsTestDriverConf)
			throws FileNotFoundException, YamlException, IOException, MalformedRequestException, ResourceNotFoundException, ContentProcessingException
	{
		File bundlesDir = new File(jsTestDriverConf.getParentFile(), BUNDLES_DIR_NAME);
		FileUtility.deleteDirectory(bundlesDir);
		bundlesDir.mkdir();
		
		Map<String, Object> configMap = getMapFromYamlConfig(jsTestDriverConf);
		
		File baseDirectory = getBaseDirectory(jsTestDriverConf, configMap);
		
		
		BundlerHandler bundlerHandler = new BundlerHandler(brjs);
		
		for (String resourceToLoad : getListOfResourcesToLoad(configMap))
		{
			File requestedFile = new File(baseDirectory, resourceToLoad);
			
			if (fileIsInBundlesDirectory(requestedFile) && isNotWildcardFilename(requestedFile))
			{
				String bundlePath = StringUtils.substringAfterLast( requestedFile.getAbsolutePath(), BUNDLES_DIR_NAME+File.separator);
				bundlePath = StringUtils.replace(bundlePath, "\\", "/");
				bundlerHandler.createBundleFile(requestedFile, bundlePath);
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
	
	private static File getBaseDirectory(File jsTestDriverConf, Map<String, Object> configMap)
	{
		File root = jsTestDriverConf.getParentFile();
		String relativeBasePath = (String) configMap.get("basepath");
		return new File(root, relativeBasePath);
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
