package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.TreeMap;

import org.bladerunnerjs.utility.FileUtility;


public class FileModificationRegistry
{
	
	private TreeMap<String,Long> lastModifiedMap = new TreeMap<>();
	private File rootFile;

	public FileModificationRegistry(File rootFile) { 
		this.rootFile = FileUtility.getCanonicalFileWhenPossible(rootFile); 
	}
	
	public synchronized Long getFileVersion(File file) {
		String canonicalFilePath = createKeyAndInitEmptyValueIfRequired(file);
		return lastModifiedMap.get(canonicalFilePath);
	}

	public synchronized void incrementFileVersion(File file) {
		Long currentLastModified;
		File fileToIncrement = FileUtility.getCanonicalFileWhenPossible(file);
		while (fileToIncrement != null && !fileToIncrement.equals(rootFile)) {
			String canonicalFilePath = createKeyAndInitEmptyValueIfRequired(fileToIncrement);
			currentLastModified = lastModifiedMap.get(canonicalFilePath);
			lastModifiedMap.put(canonicalFilePath, ++currentLastModified);
			fileToIncrement = fileToIncrement.getParentFile();
		}
	}
	
	
	private String createKeyAndInitEmptyValueIfRequired(File file)
	{
		String canonicalFilePath = FileUtility.getCanonicalFileWhenPossible(file).getAbsolutePath();
		if (!lastModifiedMap.containsKey(canonicalFilePath)) {
			lastModifiedMap.put(canonicalFilePath, Long.valueOf(0));
		}
		return canonicalFilePath;
	}
	
}
