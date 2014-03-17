package org.bladerunnerjs.utility;

import java.io.File;

import org.apache.tools.ant.util.FileUtils;

public class RelativePathUtility {
	public static String get(File basePath, File childPath) {
		try {
			return FileUtils.getRelativePath(basePath, childPath);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
