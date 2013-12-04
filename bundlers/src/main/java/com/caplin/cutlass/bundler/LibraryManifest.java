package com.caplin.cutlass.bundler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;

public class LibraryManifest
{
	private static final String commaWithOptionalSpacesSeparator = "[\\s]*,[\\s]*";
	
	private List<String> depends;
	private List<String> javascriptFiles;
	private List<String> cssFiles;
	
	public LibraryManifest(File libraryDirectory)
	{
		depends = new ArrayList<String>();
		javascriptFiles = new ArrayList<String>();
		cssFiles = new ArrayList<String>();
		
		File libraryManifestFile = new File(libraryDirectory, CutlassConfig.LIBRARY_MANIFEST_FILENAME);
		if(libraryManifestFile.exists())
		{
			Properties manifestFileProperties = new Properties();
			
			try(Reader bundlerFileReader = BundlerFileReaderFactory.getBundlerFileReader(libraryManifestFile))
			{
				manifestFileProperties.load(bundlerFileReader);
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			depends = getManifestFileProperty(manifestFileProperties, "depends");
			javascriptFiles = getManifestFileProperty(manifestFileProperties, "js");
			cssFiles = getManifestFileProperty(manifestFileProperties, "css");
		}
	}
	
	public List<String> getManifestFileProperty(Properties manifestFile, String propertyName)
	{
		List<String> propertyValues = new ArrayList<String>();
		String propertyValueString = manifestFile.getProperty(propertyName);
		if(propertyValueString != null)
		{
			propertyValues = Arrays.asList(propertyValueString.split(commaWithOptionalSpacesSeparator));
		}
		return propertyValues;
	}
	
	public List<String> getLibraryDependencies()
	{
		return depends;
	}

	public List<String> getJavascriptFiles()
	{
		return javascriptFiles;
	}

	public List<String> getCssFiles()
	{
		return cssFiles;
	}

}
