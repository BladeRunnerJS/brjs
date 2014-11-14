package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.filefilter.TrueFileFilter;
import org.bladerunnerjs.model.engine.RootNode;


public class FileModificationRegistry
{
	private Map<String,FileVersion> lastModifiedMap = new HashMap<String,FileVersion>();
	private File rootFile;
	private Map<File, File> canonicalFileMap = new HashMap<>();
	private RootNode rootNode;

	public FileModificationRegistry(RootNode rootNode, File rootFile) { 
		this.rootFile = getCanonicalFile(rootFile);
		this.rootNode = rootNode;
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
	
	private synchronized FileVersion getOrCreateVersionValue(String canonicalFilePath)
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
				canonicalFile = file.getAbsoluteFile();
				rootNode.logger(this.getClass()).warn("Unable to calculate canonical file for the path: " + file.getAbsolutePath());
			}
			canonicalFileMap.put(file, canonicalFile);
		} else {
			canonicalFile = canonicalFileMap.get(file);
		}
		return canonicalFile;
	}
	
}
