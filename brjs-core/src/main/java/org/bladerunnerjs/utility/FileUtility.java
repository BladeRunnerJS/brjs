package org.bladerunnerjs.utility;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
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
}
