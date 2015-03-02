package org.bladerunnerjs.api.memoization;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FilenameUtils;
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
			if (!memoizedFileMap.containsKey(memoizedFile.getAbsolutePath())) {
				memoizedFileMap.put( memoizedFile.getAbsolutePath(), memoizedFile );
			}
		} else {
			String pathKey = FilenameUtils.normalize( file.getAbsolutePath() );
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
