package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.TreeMap;

import org.bladerunnerjs.utility.FileUtility;


public class FileModificationRegistry
{
	
	private TreeMap<String,Long> lastModifiedMap = new TreeMap<>();

	// a temporary hack so we can optionally use values from disk until fully moved over to the new file modified checker
	private boolean useLogicalModifiedTime;
	public FileModificationRegistry() { this(false); }
	public FileModificationRegistry(boolean useLogicalModifiedTime) { this.useLogicalModifiedTime = useLogicalModifiedTime; }
	
	
	public Long getLastModified(File file) {
		if (useLogicalModifiedTime) {
			String canonicalFilePath = createKeyAndInitEmptyValueIfRequired(file);
			return lastModifiedMap.get(canonicalFilePath);
		}
		return file.lastModified();
	}

	public void updateLastModified(File file) {
		String canonicalFilePath = createKeyAndInitEmptyValueIfRequired(file);
		Long currentLastModified = lastModifiedMap.get(canonicalFilePath);
		lastModifiedMap.put(canonicalFilePath, ++currentLastModified);
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
