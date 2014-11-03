package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.filefilter.TrueFileFilter;


public class FileModificationRegistry
{
	
	private TreeMap<String,FileVersion> lastModifiedMap = new TreeMap<>();
	private File rootFile;
	private Map<File, File> canonicalFileMap = new HashMap<>();

	public FileModificationRegistry(File rootFile) { 
		this.rootFile = getCanonicalFile(rootFile); 
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
		
		incrementFileVersion(file);
		
		List<String> incrementedPaths = new ArrayList<>();
		
		if (file.isDirectory()) {
			for (File child : org.apache.commons.io.FileUtils.listFilesAndDirs(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				incrementedPaths.add( getCanonicalFile(file).getAbsolutePath() );
				getOrCreateVersionValue(child).incrememntValue();
			}
		}
		
		String fileCanonicalPath = getCanonicalFile(file).getAbsolutePath();
		for (String path : lastModifiedMap.keySet()) {
			if (fileCanonicalPath.startsWith(path) && !incrementedPaths.contains(fileCanonicalPath)) {
				lastModifiedMap.get(path).incrememntValue();
			}
		}
	}
	
	
	private FileVersion getOrCreateVersionValue(File file)
	{
		if (file instanceof MemoizedFile) {
			return getOrCreateVersionValue( ((MemoizedFile) file).getCanonicalPath() );
		}
		return getOrCreateVersionValue( getCanonicalFile(file).getAbsolutePath() );
	}
	
	private FileVersion getOrCreateVersionValue(String canonicalFilePath)
	{
		FileVersion version;
		if (!lastModifiedMap.containsKey(canonicalFilePath)) {
			version = new FileVersion();
			lastModifiedMap.put(canonicalFilePath, version);
		} else {
			version = lastModifiedMap.get(canonicalFilePath);
		}
		return version;
	}
	
	private File getCanonicalFile(File file) {
		File canonicalFile;
		if (!canonicalFileMap.containsKey(file)) {
			try
			{
				canonicalFile = file.getCanonicalFile();
			}
			catch (IOException e)
			{
				throw new RuntimeException("Unable to calculate canonical file for the path: " + file.getAbsolutePath());
			}
			canonicalFileMap.put(file, canonicalFile);
		} else {
			canonicalFile = canonicalFileMap.get(file);
		}
		return canonicalFile;
	}
	
}
