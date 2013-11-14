package org.bladerunnerjs.model.utility;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.bladerunnerjs.model.sinbin.CutlassConfig;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;


public class FileUtility {
	
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

	public static List<File> getAllFilesAndFoldersMatchingFilterIncludingSubdirectories(File directory, FileFilter filter)
	{
		ArrayList<File> files = new ArrayList<File>();
		recurseIntoSubfoldersAndAddAllFilesMatchingFilter(files, directory, filter);
		return files;
	}
	
	// Copy the contents of the assetContainer (not the actual folder itself) to targetLocation
	public static void copyDirectoryContents(File assetContainer , File targetLocation) throws IOException 
	{
		if (assetContainer.isDirectory()) {
			if (!targetLocation.exists()) {
				targetLocation.mkdirs();
			}
			
			String[] children = assetContainer.list();
			for (int i=0; i<children.length; i++) {
				copyDirectoryContents(new File(assetContainer, children[i]),
						new File(targetLocation, children[i]));
			}
		} else {
			
			InputStream in = new FileInputStream(assetContainer);
			OutputStream out = new FileOutputStream(targetLocation);
			
			// Copy the bits from instream to outstream
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		}
	}
	
	public static void deleteDirContent(File dir) throws IOException {
		File[] content = dir.listFiles();
		for (int i=0; i < content.length; i++)
		{
			deleteDirAndContents(content[i]);
		}
	}

	public static void deleteDirAndContents(File content) throws IOException 
	{
		if (content.isDirectory()) {
			String[] children = content.list();
			for (int i=0; i<children.length; i++) {
			   	deleteDirAndContents(new File(content, children[i]));
			}
		}

		// The directory is now empty so delete it
		if(!content.delete())
		{
			 throw new IOException("Failed to delete the file " + content.getAbsolutePath());
		}
	}
	
	public static boolean dirsExist(List<File> fileList, PrintStream out) {
		boolean exists = true;
		for (int i = 0; i < fileList.size(); i++)
		{
			if(!fileList.get(i).exists())
			{
				out.append("Could not find: " +fileList.get(i).getAbsolutePath());
				exists = false;
				break;
			}
		}		
		return exists;
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
	
	public static void createResourcesFromSdkTemplate(File templateDir, File targetDir) throws IOException
	{
		createResourcesFromSdkTemplate(templateDir, targetDir, new NotFileFilter(new NameFileFilter("null.txt")));
	}
	
	public static void createResourcesFromSdkTemplate(File templateDir, File targetDir, FileFilter fileFilter) throws IOException
	{
		List<File> list = getAllFilesAndFoldersMatchingFilterIncludingSubdirectories(templateDir, fileFilter);
		
		if (targetDir.exists() == false)
		{
			targetDir.mkdirs();
		}
		
		for (File f : list)
		{			
			String relativePathFromTemplateDir = f.getAbsolutePath().replace(templateDir.getAbsolutePath(), "");
			File newResourceToAdd = new File(targetDir, relativePathFromTemplateDir);

			if (f.isDirectory() == true)
			{
				createFolder(newResourceToAdd);
			}
			else 
			{
				createFile(f, newResourceToAdd);
			}
		}
	}
	
	public static void createFile(File source, File newFileLocation) throws IOException
	{
		if (source.exists() == true)
		{
			if (newFileLocation.exists() == false)
			{
				if (newFileLocation.getParentFile().exists() == false)
				{
					createFolder(newFileLocation.getParentFile());
				}
				
				FileUtils.copyFile(source, newFileLocation);
			}
		}
	}
	
	public static void createFolder(File folderToAdd)
	{
		if(!folderToAdd.exists())
		{
			folderToAdd.mkdirs();	
		}		
	}
	
	public static List<File> getSortedListOfSubFolders(File sourceFolder)
	{
		List<File> subfolders = new ArrayList<File>();
		
		for(File content : sortFileArray(sourceFolder.listFiles()))
		{
			if(content.isDirectory() && !content.getName().startsWith("."))
			{
				subfolders.add(content);
			}
		}
		
		return subfolders;
	}

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
		return tempdir;
	}
	
	public static File createTemporaryDirectoryWithSpecifiedFolderName(String folderName) throws IOException
	{
		File tempFolder = createTemporaryDirectory("temp");
		File folder = new File(tempFolder, folderName);
		
		folder.mkdir();
		
		return folder;
	}
	
	public static File createTemporaryFile(String prefix, String suffix) throws IOException
	{
		final File temp = File.createTempFile(prefix, suffix);
		Runtime.getRuntime().addShutdownHook(new DeleteTempFileShutdownHook(temp));
		return temp;
	}
		
	public static void recursivelyDeleteEmptyDirectories(File fileToDelete)
	{
		if(fileToDelete.isDirectory())
		{
			for(File subFile : fileToDelete.listFiles())
			{
				recursivelyDeleteEmptyDirectories(subFile);
			}
			
			if(fileToDelete.listFiles().length == 0)
			{
				fileToDelete.delete();
			}
		}
	}
	
	private static void recurseIntoSubfoldersAndAddAllFilesMatchingFilter(List<File> files, File file, FileFilter filter)
	{
		if(file.isDirectory())
		{
			for(File r : FileUtility.sortFileArray(file.listFiles()))
			{
				if (!r.getName().startsWith("."))
				{
					recurseIntoSubfoldersAndAddAllFilesMatchingFilter(files, r, filter);
				}
			}
		}
		
		if(filter.accept(file) && !file.getName().startsWith("."))
		{
			files.add(file);
		}
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
	
	public static File[] sortFileArray(File[] files)
	{
		if (files == null)
		{
			return new File[]{};
		}
		else
		{
			List<File> filesList = Arrays.asList(files);
			Collections.sort(filesList, new Comparator<File>()
			{
				@Override
				public int compare(File file1, File file2)
				{
					return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
				}
			});
			return (File[]) filesList.toArray();
		}
	}

	public static File createTemporarySdkInstall(File existingSDK) throws IOException
	{
		File tempDir = createTemporaryDirectory("tempSdk");
		if ( !(tempDir.exists() && tempDir.isDirectory()) ) {
			throw new AssertionError();
		}
		FileUtils.copyDirectory(existingSDK, tempDir);
		return new File(tempDir, CutlassConfig.SDK_DIR);
	}

	public static String normalizeLineEndings(String content)
	{
		return content.replaceAll("\r", "");
	}
	
	public static void createHiddenFileAndFolder(File location) throws IOException
	{
		if(!System.getProperty("os.name").toLowerCase().contains("windows"))
		{
			new File(location, ".hiddenDir").mkdirs();
			new File(location, ".hiddenFile").createNewFile();	
		}
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
	
}
