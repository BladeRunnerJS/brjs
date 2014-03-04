package com.caplin.cutlass.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.PathFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.NameFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;

import com.caplin.cutlass.CutlassConfig;

import org.bladerunnerjs.utility.DeleteTempFileShutdownHook;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class FileUtility extends org.bladerunnerjs.utility.FileUtility {
	public static List<File> listDirs(File dir)
	{
		FileFilter filter = FileFilterUtils.and( DirectoryFileFilter.INSTANCE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")) );
		List<File> files = Arrays.asList( dir.listFiles(filter) );
		Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
		
		return files;
	}
	
	public static Collection<File> sortFiles(Collection<File> files)
	{		
		ArrayList<File> filesCopy = new ArrayList<File>(files);
		Collections.sort( filesCopy, PathFileComparator.PATH_COMPARATOR );
		return filesCopy;
	}
	
	public static File[] sortFiles(File[] files)
	{
		return sortFiles( Arrays.asList(files) ).toArray(new File[0]);
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
			for(File r : FileUtility.sortFiles(file.listFiles()))
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
}
