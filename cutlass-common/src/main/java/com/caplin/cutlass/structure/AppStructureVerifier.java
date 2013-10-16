package com.caplin.cutlass.structure;

import java.io.File;

import org.apache.commons.lang3.StringUtils;

import org.bladerunnerjs.model.sinbin.CutlassConfig;


public class AppStructureVerifier 
{
	public static File getApplicationDirFileObject(File sdkBaseDir, String applicationName)
	{
		File applicationsDir = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR);
		File applicationDir = new File(applicationsDir, applicationName);
		
		return applicationDir;
	}
	
	public static boolean applicationExists(File sdkBaseDir, String applicationName)
	{
		File applicationsDir = getApplicationDirFileObject(sdkBaseDir, applicationName);

		return applicationsDir.exists();
	}
	
	public static File getAspectDirFileObject(File sdkBaseDir, String applicationName, String aspectName)
	{
		File applicationDir = getApplicationDirFileObject(sdkBaseDir, applicationName);
		File aspectDirectory = new File(applicationDir, qualifyAspectFolderName(aspectName));
		
		return aspectDirectory;
	}
	
	public static boolean applicationAspectExists(File sdkBaseDir, String applicationName, String aspectName)
	{
		File aspectDirectory = getAspectDirFileObject(sdkBaseDir, applicationName, aspectName);
				
		return aspectDirectory.exists();
	}

	public static File getBladesetDirFileObject(File sdkBaseDir, String applicationName, String bladesetName)
	{
		File applicationDir = getApplicationDirFileObject(sdkBaseDir, applicationName);
		File bladesetDir = new File(applicationDir, qualifyBladesetFolderName(bladesetName));
		
		return bladesetDir;
	}
	
	public static boolean bladesetExists(File sdkBaseDir, String applicationName, String bladesetName)
	{
		File bladesetDir = getBladesetDirFileObject(sdkBaseDir, applicationName, bladesetName);
		
		return bladesetDir.exists();
	}
	
	public static File getBladeDirFileObject(File sdkBaseDir, String applicationName, String bladesetName, String bladeName)
	{
		File bladesetDir = getBladesetDirFileObject(sdkBaseDir, applicationName, bladesetName);
		File bladeDir = new File(bladesetDir, CutlassConfig.BLADES_CONTAINER_DIR + File.separator + bladeName);
		
		return bladeDir;
	}
	
	public static boolean bladeExists(File sdkBaseDir, String applicationName, String bladesetName, String bladeName)
	{
		File bladeDir = getBladeDirFileObject(sdkBaseDir, applicationName, bladesetName, bladeName);
		
		return bladeDir.exists();
	}
	
	public static String chompBladesetFromString(String bladesetName)
	{
		return StringUtils.removeEnd(bladesetName, CutlassConfig.BLADESET_SUFFIX);
	}
	 
	public static String qualifyBladesetFolderName(String bladesetName)
	{
		return chompBladesetFromString(bladesetName) + CutlassConfig.BLADESET_SUFFIX;
	}
	
	public static String chompAspectFromString(String aspectName)
	{
		return StringUtils.removeEnd(aspectName, CutlassConfig.ASPECT_SUFFIX);
	}
	
	public static String qualifyAspectFolderName(String aspectName)
	{
		return chompAspectFromString(aspectName) + CutlassConfig.ASPECT_SUFFIX;
	}

}
