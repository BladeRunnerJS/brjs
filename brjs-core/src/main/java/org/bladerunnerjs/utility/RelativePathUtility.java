package org.bladerunnerjs.utility;

import java.io.File;

import org.apache.tools.ant.util.FileUtils;
import org.bladerunnerjs.model.BRJS;

public class RelativePathUtility {
	public static String get(File basePath, File childPath) {
		try {
			String relativePath = FileUtils.getRelativePath(basePath, childPath  );
			return relativePath;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	
	public static String get(File basePath, File childPath, BRJS brjs) {
		try {
			String relativePath = RelativePath.getRelativePath(brjs.getFileInfo(basePath).canonicalPath(), brjs.getFileInfo(childPath).canonicalPath() );
			return relativePath;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
