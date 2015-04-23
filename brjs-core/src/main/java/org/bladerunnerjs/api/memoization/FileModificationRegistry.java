package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.model.engine.RootNode;

/**
 * The {@link FileModificationRegistry} tracks the 'version' of files. It's used by {@link MemoizedFile} and {@link MemoizedValue} to determine whether
 * or not a value needs to be re-calculated based on if any dependent files need to change.
 *
 */
@SuppressWarnings("unused")
public class FileModificationRegistry
{
	private Map<String,FileVersion> lastModifiedMap = new ConcurrentHashMap<>();
	private List<File> rootFiles;
	private IOFileFilter globalFileFilter;

	public FileModificationRegistry(IOFileFilter globalFileFilter, File... rootFiles) {
		this.rootFiles = Arrays.asList(rootFiles);
		this.globalFileFilter = globalFileFilter;
	}
	
	public long getFileVersion(File file) {
		return getFileVersionObject(file).getValue();
	}
	
	public FileVersion getFileVersionObject(File file) {
		return getOrCreateVersionValue(file);
	}
	
	public void incrementFileVersion(File file) {
		if (globalFileFilter.accept(file)) {
			incrementAllFileVersions();
		} else {
			incrementFileAndParentVersion(file);
		}
	}
	
	public void incrementChildFileVersions(File file) {
		if (file instanceof MemoizedFile) {
			file = new File(file.getAbsolutePath()); // create a standard file so listFiles() isnt cached
		}
		
		incrementFileVersion(file);
		
		String filePath = file.getAbsolutePath();
		Set<String> lastModifiedMapKeySet = new LinkedHashSet<>( lastModifiedMap.keySet() ); // copy the set to prevent concurrent modified exceptions
		for (String path : lastModifiedMapKeySet) {
			if (path.startsWith(filePath)) {
				lastModifiedMap.get(path).incrementValue();
			}
		}
	}
	
	public void incrementAllFileVersions() {
		for (FileVersion version : lastModifiedMap.values()) {
			version.incrementValue();
		}
	}
	
	private void incrementFileAndParentVersion(File file) {
		while (file != null && !rootFiles.contains(file)) {
			getOrCreateVersionValue(file).incrementValue();
			file = file.getParentFile();
		}
	}
	
	private FileVersion getOrCreateVersionValue(File file)
	{
		if (file instanceof MemoizedFile) {
			return getOrCreateVersionValue( ((MemoizedFile) file).getAbsolutePath() );
		}
		return getOrCreateVersionValue( file.getAbsolutePath() );
	}
	
	private synchronized FileVersion getOrCreateVersionValue(String filePath)
	{
		FileVersion version;
		if (!lastModifiedMap.containsKey(filePath)) {
			version = new FileVersion();
			lastModifiedMap.put(filePath, version);
		} else {
			version = lastModifiedMap.get(filePath);
		}
		return version;
	}
	
}
