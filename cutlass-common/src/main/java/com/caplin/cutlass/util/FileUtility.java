package com.caplin.cutlass.util;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.PathFileComparator;

import com.caplin.cutlass.CutlassConfig;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.utility.DeleteTempFileShutdownHook;

import java.io.*;
import java.util.*;

public class FileUtility extends org.bladerunnerjs.utility.FileUtility {
	
	
	public static Collection<MemoizedFile> sortFiles(Collection<MemoizedFile> files)
	{		
		ArrayList<MemoizedFile> filesCopy = new ArrayList<>(files);
		Collections.sort( filesCopy, PathFileComparator.PATH_COMPARATOR );
		return filesCopy;
	}
	
	public static File[] sortFiles(MemoizedFile[] files)
	{
		return sortFiles( Arrays.asList(files) ).toArray(new File[0]);
	}

	public static List<MemoizedFile> getAllFilesAndFoldersMatchingFilterIncludingSubdirectories(MemoizedFile directory, FileFilter filter)
	{
		ArrayList<MemoizedFile> files = new ArrayList<>();
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
	
	private static void recurseIntoSubfoldersAndAddAllFilesMatchingFilter(List<MemoizedFile> files, MemoizedFile file, FileFilter filter)
	{
		if(file.isDirectory())
		{
			for(MemoizedFile r : file.listFiles())
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
		File tempDir = createTemporaryDirectory( FileUtility.class );
		if ( !(tempDir.exists() && tempDir.isDirectory()) ) {
			throw new AssertionError();
		}
		FileUtils.copyDirectory(existingSDK, tempDir);
		return new File(tempDir, CutlassConfig.SDK_DIR);
	}
	
}
