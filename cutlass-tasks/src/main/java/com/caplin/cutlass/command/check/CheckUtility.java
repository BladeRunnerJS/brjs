package com.caplin.cutlass.command.check;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.FileFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.utility.FileUtility;
import org.bladerunnerjs.model.App;
import com.caplin.cutlass.BRJSAccessor;
import org.bladerunnerjs.model.JsNonBladeRunnerLib;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.model.path.AppPath;
import com.caplin.cutlass.structure.model.path.SdkPath;

public class CheckUtility
{

	private FileFilter CAPLIN_JAR_FILE_NAME_FILTER = new AndFileFilter(new PrefixFileFilter("br-"), new SuffixFileFilter(".jar"));
	
	private Map<String,String> environmentKeys = new LinkedHashMap<String,String>();
	
	public CheckUtility()
	{
		environmentKeys.put("Java Vendor", "java.vendor");
		environmentKeys.put("Java Version", "java.version");
		environmentKeys.put("OS Name", "os.name");
		environmentKeys.put("OS Version", "os.version");
		environmentKeys.put("OS Arch", "os.arch");
	}
	
	public String printEnvironmentDetails()
	{
		StringBuilder msg = new StringBuilder();
		for (String key : environmentKeys.keySet())
		{
			String propertyKey = environmentKeys.get(key);
			String propertyValue = System.getProperty(propertyKey);
			msg.append(key + " : " + propertyValue + "\n");
		}
		return msg.toString();
	}
	
	public List<File> getJarsFromSDKLibsApplicationDirectory(File sdkDir)
	{
		File javaLibsApplicationDirectory = new SdkPath(sdkDir).libsPath().javaLibsPath().applicationLibsPath().getDir();
		File[] files = javaLibsApplicationDirectory.listFiles(CAPLIN_JAR_FILE_NAME_FILTER);
		
		return Arrays.asList(files);
	}
	
	public List<File> getJarsFromApplication(File application) throws Exception
	{
		File applicationWebInfLibDirectory = new File(application, "/WEB-INF/lib");
		
		if (applicationWebInfLibDirectory.exists() == false)
		{
			throw new Exception("Could not find directory '" + applicationWebInfLibDirectory.getAbsolutePath() + "'");
		}
		
		File[] files = FileUtility.sortFileArray(applicationWebInfLibDirectory.listFiles(CAPLIN_JAR_FILE_NAME_FILTER));
		
		return Arrays.asList(files);
	}
	
	public void getJarDifferences(List<File> sdkLibsSystemJars, List<File> applicationWebInfJars, List<File> jarsToAdd, Map<File, File> jarsWithChanges, List<File> jarsToRemove) 
			throws IOException
	{
		jarsToRemove.addAll(applicationWebInfJars);
		
		for(File sdkLibSystemJar : sdkLibsSystemJars)
		{
			checkIfSystemJarIsInApplication(applicationWebInfJars, jarsWithChanges, jarsToRemove, sdkLibSystemJar, jarsToAdd);
		}
	}
	
	public void checkJarDifferencesAndAddMessageListingThem(StringBuilder messageToShowUser, List<File> sdkLibsApplicationJars, File application) throws CommandOperationException
	{
		List<File> jarsToAdd = new ArrayList<File>();
		List<File> jarsToRemove = new ArrayList<File>();
		Map<File, File> jarsWithChanges = new HashMap<File, File>();
		File sdkApplicationJarsDir = null;
		List<File> applicationJars = null;
		
		try
		{
			applicationJars = this.getJarsFromApplication(application);
			sdkApplicationJarsDir = new SdkPath(application).libsPath().javaLibsPath().applicationLibsPath().getDir();
			getJarDifferences(sdkLibsApplicationJars, applicationJars, jarsToAdd, jarsWithChanges, jarsToRemove);
		}
		catch (Exception e)
		{
			throw new CommandOperationException(".", e);
		}
		
		messageToShowUser.append("For application '" + application.getName() + "':\n");
		messageToShowUser.append(getMessageListingOutstandingJarChanges(sdkApplicationJarsDir, jarsToAdd, jarsToRemove, jarsWithChanges));
	}
	
	public StringBuilder getMessageListingOutstandingJarChanges(File sdkApplicationJarsDir, List<File> jarsToAdd, List<File> jarsToRemove, Map<File, File> jarsWithChanges)
	{
		StringBuilder messageToShowUser = new StringBuilder();
		
		appendJarList("The following jars should be added to this apps' \"WEB-INF/lib\" from the SDK:", jarsToAdd, messageToShowUser);
		appendJarList("The following jars should be deleted from this apps' \"WEB-INF/lib\":", jarsToRemove, messageToShowUser);
		appendJarList("The following jars have been modified inside this apps' \"WEB-INF/lib\":", jarsWithChanges.keySet(), messageToShowUser);
		
		if (messageToShowUser.length() == 0)
		{
			messageToShowUser.append(" - Jar consistency check - OK\n");
		}
		
		messageToShowUser.append("\n");
		
		return messageToShowUser;
	}

	private void appendJarList(String message, List<File> jarList, StringBuilder messageToShowUser) 
	{
		if (jarList.size() > 0)
		{
			messageToShowUser.append(message + "\n");
		}
		for(File jar : jarList)
		{
			messageToShowUser.append(" - '" + jar.getAbsolutePath() + "'\n");
		}
	}

	private void appendJarList(String message, Set<File> keySet, StringBuilder messageToShowUser) 
	{
		if (keySet.size() > 0)
		{
			messageToShowUser.append(message + "\n");
		}
		for(File jar : keySet)
		{
			messageToShowUser.append(" - '" + jar.getAbsolutePath() + "'\n");
		}
	}
	
	public StringBuilder checkThatWeHaveNothingInPatchesDirectory(StringBuilder messageToShowUser)
	{
		File patchesDirectory = BRJSAccessor.root.jsPatches().dir();
		
		if(patchesDirectory.exists())
		{
			List<File> patchFiles = FileUtility.getAllFilesAndFoldersMatchingFilterIncludingSubdirectories(patchesDirectory, FileFileFilter.FILE);
			
			if(patchFiles.size() > 0)
			{
				messageToShowUser.append("Patch files were found inside the '" + patchesDirectory.getName() + "' directory, " +
										 "please check to see if they are still necessary:\n");
				
				for(File patchFile : patchFiles)
				{
					if (patchFile.isFile() == true)
					{
						messageToShowUser.append(" - '" + patchFile.getAbsolutePath() + "'\n");
					}
				}
			}
		}
		
		return messageToShowUser;
	}

	private void checkIfSystemJarIsInApplication(List<File> applicationWebInfJars, Map<File, File> jarsWithChanges, List<File> jarsToRemove, File sdkLibSystemJar, List<File> jarsToAdd) 
			throws IOException
	{
		for(File applicationWebInfJar : applicationWebInfJars)
		{
			if(applicationWebInfJar.getName().equals(sdkLibSystemJar.getName()))
			{
				jarsToRemove.remove(applicationWebInfJar);
				
				if(FileUtils.contentEquals(sdkLibSystemJar, applicationWebInfJar) == false)
				{
					jarsWithChanges.put(sdkLibSystemJar, applicationWebInfJar);
				}
				
				return;
			}
		}
		
		jarsToAdd.add(sdkLibSystemJar);
	}

	public void checkForApplicationThirdpartyLibraries(StringBuilder messageToShowUser, List<JsNonBladeRunnerLib> sdkThirdpartyLibraries, App application)
	{
		if (sdkThirdpartyLibraries.size() == 0) 
		{
			messageToShowUser.append("There are no thirdparty libraries present in the SDK.");
		}
		
		File appThirdpartyDir = new AppPath(application.dir()).thirdpartyLibsPath().getDir();
		List<File> overridenThirdpartyLibraries = getOverridenThirdpartyLibraries(appThirdpartyDir, sdkThirdpartyLibraries);
		
		if(overridenThirdpartyLibraries.size() > 0)
		{
			messageToShowUser.append("The following thirdparty-libraries also exist inside the SDK:\n");

			for(File overridenLib : overridenThirdpartyLibraries)
			{
				messageToShowUser.append(" - '" + overridenLib.getAbsolutePath() + "'\n");
			}
			messageToShowUser.append("\n");
		}
	}

	private List<File> getOverridenThirdpartyLibraries(File appThirdpartyDir, List<JsNonBladeRunnerLib> sdkThirdpartyLibraries)
	{
		List<File> overridenThirdpartyLibraries = new ArrayList<File>();
		
		if (appThirdpartyDir.exists() == true)
		{
			List<File> appThirdpartyLibraries = CutlassDirectoryLocator.getDirectories(appThirdpartyDir);

			for(File appThirdpartyLib : appThirdpartyLibraries)
			{
				for(JsNonBladeRunnerLib sdkThirdpartyLib : sdkThirdpartyLibraries)
				{
					// TODO: all named nodes should have a getName() method as checking the dir name is a hack that may be break in the future
					if(sdkThirdpartyLib.dir().getName().equalsIgnoreCase(appThirdpartyLib.getName()))
					{
						overridenThirdpartyLibraries.add(appThirdpartyLib);
					}
				}
			}							
		}
		
		return overridenThirdpartyLibraries;
	}
}
