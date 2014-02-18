package org.bladerunnerjs.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FileUtils;

public class FileUtility {
	public static File createTemporaryDirectory(String prependedFolderName) throws IOException
	{
		if (prependedFolderName.contains("/"))
		{
			throw new IOException("prependedFolderName can't contain a /");
		}
		final File tempdir = File.createTempFile(prependedFolderName, "");
		tempdir.delete();
		tempdir.mkdir();
		Runtime.getRuntime().addShutdownHook(new DeleteTempFileShutdownHook(tempdir));
		File tempSubDir = new File(tempdir, prependedFolderName);
		tempSubDir.mkdir();
		return tempSubDir;
	}
	
	public static void copyFileIfExists(File srcFile, File destFile) throws IOException {
		if(srcFile.exists()) {
			FileUtils.copyFile(srcFile, destFile);
		}
	}
	
	public static void copyDirectoryIfExists(File srcDir, File destDir) throws IOException {
		if(srcDir.exists()) {
			FileUtils.copyDirectory(srcDir, destDir);
		}
	}
	
	public static void zipFolder(File srcFolder, File destZipFile, boolean zipOnlySrcFolderContentsAndNotSrcFolder) throws IOException
	{
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
			if (FastDirectoryFileFilter.isDirectory(srcFile))
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
}
