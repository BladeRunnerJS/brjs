package org.bladerunnerjs.memoization;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.bladerunnerjs.model.engine.RootNode;


public class MemoizedFileAccessor
{

	private Map<String,MemoizedFile> memoizedFileMap = new TreeMap<>();
	private RootNode rootNode;
	
	public MemoizedFileAccessor(RootNode rootNode) {
		this.rootNode = rootNode;
	}
	
	public MemoizedFile getMemoizedFile(File file) {
		if (file == null) return null;
		
		MemoizedFile memoizedFile;
		if (file instanceof MemoizedFile) {
			memoizedFile = (MemoizedFile) file;
			if (!memoizedFileMap.containsKey(memoizedFile.getCanonicalPath())) {
				memoizedFileMap.put( memoizedFile.getCanonicalPath(), memoizedFile );
			}
		} else {
			String pathKey;
			try
			{
				pathKey = file.getCanonicalPath();
			}
			catch (IOException e)
			{
				pathKey = file.getAbsolutePath();
			}
			if (memoizedFileMap.containsKey(pathKey)) {
				memoizedFile = memoizedFileMap.get(pathKey);
			} else {
				memoizedFile = new MemoizedFile(rootNode, pathKey);
				memoizedFileMap.put(pathKey, memoizedFile);
			}
		}
		return memoizedFile;
	}
	
}
