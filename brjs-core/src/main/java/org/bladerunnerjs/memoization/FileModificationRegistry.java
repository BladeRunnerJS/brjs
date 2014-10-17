package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.TreeMap;

import org.bladerunnerjs.utility.FileUtility;


public class FileModificationRegistry
{
	
	private TreeMap<String,FileVersion> lastModifiedMap = new TreeMap<>();
	private File rootFile;

	public FileModificationRegistry(File rootFile) { 
		this.rootFile = FileUtility.getCanonicalFileWhenPossible(rootFile); 
	}
	
	public synchronized long getFileVersion(File file) {
		return getOrCreateVersionValue(file).getValue();
	}

	public synchronized void incrementFileVersion(File file) {
		while (file != null && !file.equals(rootFile)) {
			getOrCreateVersionValue(file).incrememntValue();
			file = file.getParentFile();
		}
	}
	
	public void incrementAllVersions()
	{
		for (String key : lastModifiedMap.keySet()) {
			getOrCreateVersionValue(key).incrememntValue();
		}
	}
	
	
	private FileVersion getOrCreateVersionValue(File file)
	{
		return getOrCreateVersionValue( FileUtility.getCanonicalFileWhenPossible(file).getAbsolutePath() );
	}
	
	private FileVersion getOrCreateVersionValue(String canonicalFilePath)
	{
		FileVersion version = lastModifiedMap.get(canonicalFilePath);
		if (version == null) {
			version = new FileVersion();
			lastModifiedMap.put(canonicalFilePath, version);
		}
		return version;
	}
	
}
