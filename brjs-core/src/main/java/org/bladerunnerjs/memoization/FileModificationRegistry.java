package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.TreeMap;

import org.bladerunnerjs.utility.FileUtils;


public class FileModificationRegistry
{
	
	private TreeMap<String,FileVersion> lastModifiedMap = new TreeMap<>();
	private File rootFile;

	public FileModificationRegistry(File rootFile) { 
		this.rootFile = FileUtils.getCanonicalFileWhenPossible(rootFile); 
	}
	
	public synchronized long getFileVersion(File file) {
		FileVersion version = getOrCreateVersionValue(file);
		return version.getValue();
	}

	public synchronized void incrementFileVersion(File file) {
		while (file != null && !file.equals(rootFile)) {
			getOrCreateVersionValue(file).incrememntValue();
			file = file.getParentFile();
		}
	}
	
	public synchronized void incrementChildFileVersions(File file) {
		if (file instanceof MemoizedFile) {
			file = new File(file.getAbsolutePath()); // create a standard file so listFiles() isnt cached
		}
		getOrCreateVersionValue(file).incrememntValue();
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				incrementChildFileVersions(child);
			}
		}
	}
	
	
	private FileVersion getOrCreateVersionValue(File file)
	{
		return getOrCreateVersionValue( FileUtils.getCanonicalFileWhenPossible(file).getAbsolutePath() );
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
