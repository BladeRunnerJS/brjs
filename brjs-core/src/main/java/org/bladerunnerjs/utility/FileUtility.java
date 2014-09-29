package org.bladerunnerjs.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

public class FileUtility {
	
	public static File createTemporaryFile(Class<?> testClass) throws IOException
	{		
		String folderName = "brjs-"+testClass.getSimpleName();
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
	
	public static void zipFolder(File srcFolder, File destZipFile, boolean zipOnlySrcFolderContentsAndNotSrcFolder) throws IOException
	{
		destZipFile.getParentFile().mkdirs();
		
		FileOutputStream fileWriter = new FileOutputStream(destZipFile);
		ZipOutputStream zip = new ZipOutputStream(fileWriter);
		
		if(zipOnlySrcFolderContentsAndNotSrcFolder)
		{
			for (File file : srcFolder.listFiles())
			{
				addFileToZip("", file, zip, false);
			}
		}
		else
		{
			addFolderToZip("", srcFolder, zip);
		}
		
		zip.flush();
		zip.close();
	}
	
	private static void addFileToZip(String path, File srcFile, ZipOutputStream zip, boolean flag) throws IOException
	{
		if (flag == true)
		{
			zip.putNextEntry(new ZipEntry(path + "/" + srcFile.getName() + "/"));
		}
		else
		{
			if (srcFile.isDirectory())
			{
				addFolderToZip(path, srcFile, zip);
			}
			else
			{
				int len;
				byte[] buf = new byte[1024];
				FileInputStream in = new FileInputStream(srcFile);
				String pathPrefix = (!path.equals("")) ? path+"/" : "";
				zip.putNextEntry(new ZipEntry(pathPrefix + srcFile.getName()));
				
				while ((len = in.read(buf)) > 0)
				{
					zip.write(buf, 0, len);
				}
				
				in.close();
			}
		}
	}

	private static void addFolderToZip(String path, File srcFolder, ZipOutputStream zip) throws IOException
	{
		if (srcFolder.list().length == 0)
		{
			addFileToZip(path, srcFolder, zip, true);
		} 
		else 
		{
			for (File file : srcFolder.listFiles())
			{
				if (path.equals(""))
				{
					addFileToZip(srcFolder.getName(), file, zip, false);
				}
				else
				{
					addFileToZip(path + "/" + srcFolder.getName(), file, zip, false);
				}
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void unzip(ZipFile zipFile, File targetLocation) throws IOException {			 
		Enumeration files = zipFile.entries();
		File f = null;
		FileOutputStream fos = null;
		
		while (files.hasMoreElements()) {
			try {
				ZipEntry entry = (ZipEntry) files.nextElement();
				InputStream eis = zipFile.getInputStream(entry);
				
				f = new File(targetLocation, entry.getName());
				
				if (entry.isDirectory()) 
				{
					f.mkdirs();
					continue;
				} 
				else {
					f.getParentFile().mkdirs();
					f.createNewFile();
				}
			
				fos = new FileOutputStream(f);
				IOUtils.copy(eis, fos);
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
				continue;		  
			} 
			finally {
				if (fos != null) 
				{
					try {
						fos.close();
					}
					catch (IOException e) {
						// ignore
					}
				}
			}
		}
		zipFile.close();
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
}
