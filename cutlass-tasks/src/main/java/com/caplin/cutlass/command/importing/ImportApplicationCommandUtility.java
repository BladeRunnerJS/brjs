package com.caplin.cutlass.command.importing;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.bladerunnerjs.model.ThreadSafeStaticBRJSAccessor;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.exception.command.CommandOperationException;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.util.FileUtility;

import org.bladerunnerjs.utility.ServerUtility;

public class ImportApplicationCommandUtility
{
	
	public File createApplicationDirIfItDoesNotAlreadyExist(File sdkBaseDir, String applicationName) throws CommandOperationException
	{
		File applicationsDirectory = new File(sdkBaseDir.getParentFile(), CutlassConfig.APPLICATIONS_DIR);
		File newApplicationDirectory = new File(applicationsDirectory, applicationName);
		
		if(newApplicationDirectory.exists() == true)
		{
			throw new CommandOperationException("Application name '" + applicationName + "' is already in use.");
		}
		
		newApplicationDirectory.mkdirs();
		
		return newApplicationDirectory;
	}
	
	public void unzipApplicationToTemporaryDirectoryForNewApplication(String applicationZip, File sdkBaseDir, File temporaryDirectoryForNewApplication) throws ZipException, IOException, CommandOperationException
	{
		File zipFile = new File(sdkBaseDir, applicationZip);
		
		if(zipFile.getName().endsWith(".zip") == false)
		{
			throw new CommandOperationException("The provided zip file didn't have a .zip suffix: '" +
				zipFile.getAbsolutePath() + "'.");
		}
		
		if(zipFile.exists() == false)
		{
			zipFile = new File(applicationZip);
		}
		
		if(zipFile.exists() == false)
		{
			throw new CommandOperationException("Couldn't find zip file at '" + zipFile.getAbsolutePath() + "'.");
		}
		
		ZipFile applicationZipFile = new ZipFile(zipFile);
		FileUtility.unzip(applicationZipFile, temporaryDirectoryForNewApplication);
	}

	public void copyCutlassSDKJavaLibsIntoApplicationWebInfDirectory(File sdkBaseDir, File applicationDir) throws IOException
	{
		File installedSDKJavaApplicationLibFolder = new File(sdkBaseDir, "libs/java/application");
		File newApplicationDirectoryWEBINFLibFolder = new File(applicationDir, "/WEB-INF/lib");
		
		FileUtility.copyDirectoryContents(installedSDKJavaApplicationLibFolder, newApplicationDirectoryWEBINFLibFolder);
		
		if (ThreadSafeStaticBRJSAccessor.root != null) {
			Logger logger = ThreadSafeStaticBRJSAccessor.root.logger(ImportApplicationCommandUtility.class);
			logger.info("Successfully copied SDK application jars to '" + applicationDir.getName() + "/WEB-INF/lib'");
		}
	}

	public String getCurrentApplicationName(File temporaryDirectoryForNewApplication) throws CommandOperationException
	{
		String[] applicationsUnzippedIntoTemporaryDirectory = temporaryDirectoryForNewApplication.list();
		
		if(applicationsUnzippedIntoTemporaryDirectory.length > 1)
		{
			throw new CommandOperationException("More than one folder at root of application zip.");
		}
		
		return applicationsUnzippedIntoTemporaryDirectory[0];
	}
	
	// TODO: update to use the new AppDeployerUtility
	public void createAutoDeployFileForApp(File appDir, int jettyPort) throws IOException
	{
		File autoDeployFile = new File(appDir, CutlassConfig.AUTO_DEPLOY_CONTEXT_FILENAME);
		if (ServerUtility.isPortBound(jettyPort) && !autoDeployFile.exists())
		{
			autoDeployFile.createNewFile();
		}
	}
	
	public void deleteAutoDeployFileForApp(File appDir)
	{
		File autoDeployFile = new File(appDir, CutlassConfig.AUTO_DEPLOY_CONTEXT_FILENAME);
		if (autoDeployFile.exists())
		{
			autoDeployFile.delete();
		}
	}
}
