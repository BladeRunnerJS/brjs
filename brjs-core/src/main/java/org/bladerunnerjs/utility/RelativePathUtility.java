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
	
	
	public static String get(File base, File child, BRJS brjs) {
		try {
			String basePath = brjs.getFileInfo(base).canonicalPath();
			String childPath = brjs.getFileInfo(child).canonicalPath();
			String relativePath = RelativePath.getRelativePath(basePath, childPath );
			return relativePath;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
