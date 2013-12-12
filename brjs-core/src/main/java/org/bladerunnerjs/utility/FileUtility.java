package org.bladerunnerjs.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.PathFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;

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
	
	public static List<File> listDirs(File dir)
	{
		FileFilter filter = FileFilterUtils.and( DirectoryFileFilter.INSTANCE, FileFilterUtils.notFileFilter(new PrefixFileFilter(".")) );
		List<File> files = Arrays.asList( dir.listFiles(filter) );
		Collections.sort(files, NameFileComparator.NAME_COMPARATOR);
		
		return files;
	}
	
	public static List<File> recursiveListDirs(File dir)
	{
		List<File> dirs = new LinkedList<File>();
		if (!dir.isDirectory())
		{
			return dirs;
		}
		recursiveListDirs(dir, dirs);
		return dirs;
	}
	
	private static void recursiveListDirs(File rootDir, List<File> dirs)
	{
		File[] children = rootDir.listFiles();
		if (children == null)
		{
			return;
		}
		
		for (File child : children)
		{
			if (child.isDirectory())
			{
				dirs.add(child);
				recursiveListDirs(child, dirs);
			}
		}
	}
	
	public static List<File> listFiles(File dir, IOFileFilter fileFilter) {
		if (!dir.isDirectory())
		{
			return new ArrayList<>();
		}
		List<File> files = new ArrayList<File>(FileUtils.listFiles(dir, fileFilter, FalseFileFilter.INSTANCE));
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
}
