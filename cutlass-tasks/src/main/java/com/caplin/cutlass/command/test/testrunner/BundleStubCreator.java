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


import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;

public class BundleStubCreator
{

	public static void createRequiredStubs(File jsTestDriverConf) throws IOException
	{
		Map<String, Object> configMap = getMapFromYamlConfig(jsTestDriverConf);
		
		File baseDirectory = getBaseDirectory(jsTestDriverConf, configMap);
		
		for(String resourceToLoad : getListOfResourcesToLoad(configMap))
		{
			File requestedFile = new File(baseDirectory, resourceToLoad);
			
			if(fileIsInBundlesDirectory(requestedFile) && !requestedFile.exists() && isNotWildcardFilename(requestedFile))
			{
				requestedFile.getParentFile().mkdirs();
				requestedFile.createNewFile();
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
		if(parent.getName().equals("bundles"))
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
