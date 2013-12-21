package org.bladerunnerjs.utility;

import java.io.File;

public class RelativePathUtility {
	public static String get(File basePath, File childPath) {
		return basePath.toURI().relativize(childPath.toURI()).getPath();
	}
}
