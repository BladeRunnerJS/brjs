package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.filefilter.IOFileFilter;
import org.bladerunnerjs.model.engine.RootNode;


@SuppressWarnings("unused")
public class FileModificationRegistry
{
	private Map<String,FileVersion> lastModifiedMap = new ConcurrentHashMap<>();
	private File rootFile;
	private IOFileFilter globalFileFilter;

	public FileModificationRegistry(File rootFile, IOFileFilter globalFileFilter) {
		this.rootFile = rootFile;
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
		Set<String> lastModifiedMapKeySet = new HashSet<>( lastModifiedMap.keySet() ); // copy the set to prevent concurrent modified exceptions
		for (String path : lastModifiedMapKeySet) {
			if (path.startsWith(filePath)) {
				lastModifiedMap.get(path).incrememntValue();
			}
		}
	}
	
	public void incrementAllFileVersions() {
		for (FileVersion version : lastModifiedMap.values()) {
			version.incrememntValue();
		}
	}
	
	private void incrementFileAndParentVersion(File file) {
		while (file != null && !file.equals(rootFile)) {
			getOrCreateVersionValue(file).incrememntValue();
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
