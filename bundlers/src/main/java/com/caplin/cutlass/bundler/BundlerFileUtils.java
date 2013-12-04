package com.caplin.cutlass.bundler;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.util.FileUtility;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;


public class BundlerFileUtils
{
	// TODO: move this method to VersionInfo.write()
	public static void writeSdkVersion(Writer writer) throws IOException
	{
		File versionFile = BRJSAccessor.root.versionInfo().getFile();
		String sdkVersion = "";
		String sdkBuildDate = "";
		if (versionFile.exists()) 
		{
			sdkVersion = BRJSAccessor.root.versionInfo().getVersionNumber();
			sdkBuildDate = BRJSAccessor.root.versionInfo().getBuildDate();
		}
		
		sdkVersion = (sdkVersion.equals("")) ? "Unknown" : sdkVersion;
		sdkBuildDate = (sdkBuildDate.equals("")) ? "Unknown" : sdkBuildDate;
		
		
		String createdWithText = "/**  Created with SDK Version " + sdkVersion + " (build date " + sdkBuildDate + ").  **/";
		String bannerText = "/"+StringUtils.repeat("*", createdWithText.length() - 2)+"/";
		
		// sdk version written as a string so it doesn't get removed by comment strippers
		List<String> headerContent = Arrays.asList(
				bannerText,
				createdWithText,
				bannerText,
				"",
				""
		);
		writer.write( StringUtils.join(headerContent, "\n") );
	}
	
	public static List<File> recursiveListFiles(List<File> roots, IOFileFilter filter)
	{
		List<File> files = new ArrayList<File>();
		for (File root : roots)
		{
			recursiveListFiles(root, files, filter);
		}
		return files;
	}
	
	public static List<File> recursiveListFiles(File root, IOFileFilter filter)
	{
		List<File> files = new ArrayList<File>();
		recursiveListFiles(root, files, filter);
		return files;
	}

	public static void recursiveListFiles(File root, List<File> files, IOFileFilter filter)
	{
		if(!root.isHidden() && root.getName().charAt(0) != '.')
		{
			if (root.isDirectory())
			{
				for (File child : FileUtility.sortFileArray(root.listFiles()))
				{
					recursiveListFiles(child, files, filter);
				}
			}
			else if (root.isFile() && filter.accept(root, root.getName()))
			{
				files.add(root);
			}
		}
	}

	/**
	 * Writes the bundle to the specified <tt>OutputStream</tt> by appending
	 * each file together, separating each file by the specified delimiter
	 * 
	 * @param sourceFiles
	 * @param writer
	 * @param delimiter
	 * @throws IOException 
	 */
	public static void writeBundle(List<File> sourceFiles, Writer writer) throws IOException
	{
		for (File file : sourceFiles)
		{
			try(FileReader input = new FileReader(file))
			{
				IOUtils.copy(input, writer);
				writer.write("\n\n");
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void writeClassPackages(List<File> sourceFiles, Writer writer) throws BundlerProcessingException
	{
		if(sourceFiles.size() > 0)
		{
			Map<String, Map> packageStructure = new HashMap<String, Map>();
			File rootDir = CutlassDirectoryLocator.getRootDir(sourceFiles.get(0));
			
			for (File sourceFile : sourceFiles)
			{
				File sourceDir = getSourceDir(sourceFile, rootDir);
				
				if(sourceDir != null)
				{
					addPackageToStructure(packageStructure, getPackage(sourceDir, sourceFile));
				}
			}
			
			if(packageStructure.size() > 0)
			{
				try
				{
					writer.write("// package definition block\n");
					for(String packageName : packageStructure.keySet())
					{
						writer.write("window." + packageName + " = ");
						JSONObject.writeJSONString(packageStructure.get(packageName), writer);
						writer.write(";\n");
					}
					writer.write("\n");
					writer.flush();
				}
				catch(IOException e)
				{
					throw new BundlerProcessingException(e, "Error while writing the package definition block");
				}
			}
		}
	}
	
	private static File getSourceDir(File sourceFile, File rootDir)
	{
		File nextDir = sourceFile.getParentFile();
		
		// TODO: @writables-hack
		while((nextDir != null) && (!nextDir.getName().equals("src")) && (!nextDir.getName().equals("src-test")))
		{
			nextDir = nextDir.getParentFile();
			
			// TODO: @writables-hack
			if((nextDir != null) && nextDir.equals(rootDir))
			{
				return null;
			}
		}
		
		return nextDir;
	}
	
	private static List<String> getPackage(File sourceDir, File sourceFile)
	{
		LinkedList<String> packageList = new LinkedList<String>();
		File packageDir = sourceFile.getParentFile();
		
		while(!packageDir.equals(sourceDir))
		{
			packageList.addFirst(packageDir.getName());
			packageDir = packageDir.getParentFile();
		}
		
		return packageList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static void addPackageToStructure(Map<String, Map> packageStructure, List<String> packageList)
	{
		Map<String, Map> currentPackage = packageStructure;
		
		for(String packageName : packageList)
		{
			Map<String, Map> nextPackage;
			
			if(currentPackage.containsKey(packageName))
			{
				nextPackage = currentPackage.get(packageName);
			}
			else
			{
				nextPackage = new HashMap<String, Map>();
				currentPackage.put(packageName, nextPackage);
			}
			
			currentPackage = nextPackage;
		}
	}
}
