package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.TrueFileFilter;
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
		
		List<String> incrementedPaths = new ArrayList<>();
		
		if (file.isDirectory()) {
			for (File child : org.apache.commons.io.FileUtils.listFilesAndDirs(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				incrementedPaths.add( FileUtils.getCanonicalFileWhenPossible(file).getAbsolutePath() );
				getOrCreateVersionValue(child).incrememntValue();
			}
		}
		
		String fileCanonicalPath = FileUtils.getCanonicalFileWhenPossible(file).getAbsolutePath();
		for (String path : lastModifiedMap.keySet()) {
			if (fileCanonicalPath.startsWith(path) && !incrementedPaths.contains(fileCanonicalPath)) {
				lastModifiedMap.get(path).incrememntValue();
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
