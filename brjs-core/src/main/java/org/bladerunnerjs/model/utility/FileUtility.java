package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;

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
		return tempdir;
	}
	
	public static List<File> listFiles(File dir, IOFileFilter fileFilter) {
		List<File> files = new ArrayList<File>(FileUtils.listFiles(dir, fileFilter, FalseFileFilter.INSTANCE));
		Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
		
		return files;
	}
	
	public static List<File> listDirs(File dir)
	{
		List<File> subDirs = new ArrayList<File>();
		
		for(File fileOrDir : sortFileArray(dir.listFiles()))
		{
			if(fileOrDir.isDirectory() && !fileOrDir.getName().startsWith("."))
			{
				subDirs.add(fileOrDir);
			}
		}
		
		return subDirs;
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
}
