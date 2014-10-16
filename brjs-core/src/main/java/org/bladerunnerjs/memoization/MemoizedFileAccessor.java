package org.bladerunnerjs.memoization;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.FileUtility;


public class MemoizedFileAccessor
{

	private Map<String,MemoizedFile> memoizedFileMap = new TreeMap<>();
	private RootNode rootNode;
	
	public MemoizedFileAccessor(RootNode rootNode) {
		this.rootNode = rootNode;
	}
	
	public MemoizedFile getMemoizedFile(File file) {
		String canonicalPath = FileUtility.getCanonicalFileWhenPossible(file).getAbsolutePath();
		MemoizedFile memoizedFile;
		if (file instanceof MemoizedFile) {
			memoizedFile = (MemoizedFile) file;
			if (!memoizedFileMap.containsKey(canonicalPath)) {
				memoizedFileMap.put( canonicalPath, memoizedFile );
			}
		} else {
			if (memoizedFileMap.containsKey(canonicalPath)) {
				memoizedFile = memoizedFileMap.get(canonicalPath);
			} else {
				memoizedFile = new MemoizedFile(rootNode, file);
				memoizedFileMap.put(canonicalPath, memoizedFile);
			}
		}
		return memoizedFile;
	}
	
}
