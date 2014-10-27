package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class FileUtility {
	
	// TODO: delete this method
	public static File createTemporarySdkInstall(File existingSDK) throws IOException
	{
		File tempDir = createTemporaryDirectory( FileUtility.class );
		if ( !(tempDir.exists() && tempDir.isDirectory()) ) {
			throw new AssertionError();
		}
		FileUtils.copyDirectory(existingSDK, tempDir);
		return new File(tempDir, "sdk");
	}
	
	
	public static File createTemporaryFile(Class<?> testClass, String... suffixes) throws IOException
	{		
		String folderName = "brjs-"+testClass.getSimpleName()+StringUtils.join(suffixes);
		final File tempFile = File.createTempFile(folderName, "");
		Runtime.getRuntime().addShutdownHook(new DeleteTempFileShutdownHook(tempFile));
		return tempFile;
	}
	
	public static File createTemporaryDirectory(Class<?> testClass, String... subFolderName) throws IOException
	{		
		final File tempDir = createTemporaryFile(testClass);
		tempDir.delete();
		tempDir.mkdir();
		
		if (subFolderName.length > 0) {
			String joinedSubFolderName = StringUtils.join(subFolderName,"-");
			if (joinedSubFolderName.contains("/"))
			{
				throw new IOException("subFolderName can't contain a /");
			}
			File subFolder = new File(tempDir, joinedSubFolderName);
			subFolder.mkdir();
			return subFolder;
		}
		
		return tempDir;
	}
	
	
	
	/** 
	 * Deletes directories from deepest to shallowest to prevent file locking issues caused by the Watch Service on Windows 
	 * (see https://stackoverflow.com/questions/6255463/java7-watchservice-access-denied-error-trying-to-delete-recursively-watched-ne) 
	 */ 
	public static void deleteDirectoryFromBottomUp(File dir) throws IOException
	{
		if (dir.isFile())
		{
			throw new IOException("Expected as dir as an argument, got a file");
		}
		
		File[] files = dir.listFiles();
		if (files == null)
		{
			return;
		}
		
		for (File child : files)
		{
			if (child.isDirectory())
			{
				deleteDirectoryFromBottomUp(child);
			}
			dir.delete();
		}
	}
	
	public static void moveDirectoryContents(File srcDir, File destDir) throws IOException {
		if(!destDir.exists()) {
			FileUtils.moveDirectory(srcDir, destDir);
		}
		else {
			for(File srcFile : srcDir.listFiles()) {
				File destFile = new File(destDir, srcFile.getName());
				
				if(!destFile.exists()) {
					FileUtils.moveToDirectory(srcFile, destDir, false);
				}
				else {
					if(srcFile.isFile()) {
						destFile.delete();
						FileUtils.moveToDirectory(srcFile, destDir, false);
					}
					else {
						moveDirectoryContents(srcFile, destFile);
					}
				}
			}
		}
	}
	
	public static File getCanonicalFile(File file)
	{
		try
		{
			return file.getCanonicalFile();
		}
		catch (IOException e)
		{
			throw new RuntimeException("Unable to calculate canonical file for " + file.getAbsolutePath(), e);
		}
	}
	
	public static File getCanonicalFileWhenPossible(File file)
	{
		try
		{
			return file.getCanonicalFile();
		}
		catch (IOException e)
		{
			return file;
		}
	}
	
}
