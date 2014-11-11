package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.engine.RootNode;


@SuppressWarnings("unused")
public class FileModificationRegistry
{
	private Map<String,FileVersion> lastModifiedMap = new HashMap<String,FileVersion>();
	private File rootFile;

	public FileModificationRegistry(File rootFile) { 
		this.rootFile = rootFile;
	}
	
	public long getFileVersion(File file) {
		return getFileVersionObject(file).getValue();
	}
	
	public FileVersion getFileVersionObject(File file) {
		return getOrCreateVersionValue(file);
	}

	public void incrementFileVersion(File file) {
		while (file != null && !file.equals(rootFile)) {
			getOrCreateVersionValue(file).incrememntValue();
			file = file.getParentFile();
		}
	}
	
	public void incrementChildFileVersions(File file) {
		if (file instanceof MemoizedFile) {
			file = new File(file.getAbsolutePath()); // create a standard file so listFiles() isnt cached
		}
		
		incrementFileVersion(file);
		
		List<String> incrementedPaths = new ArrayList<>();
		
		if (file.isDirectory()) {
			for (File child : org.apache.commons.io.FileUtils.listFilesAndDirs(file, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
				incrementedPaths.add( file.getAbsolutePath() );
				getOrCreateVersionValue(child).incrememntValue();
			}
		}
		
		String filePath = file.getAbsolutePath();
		for (String path : lastModifiedMap.keySet()) {
			if (filePath.startsWith(path) && !incrementedPaths.contains(filePath)) {
				lastModifiedMap.get(path).incrememntValue();
			}
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
