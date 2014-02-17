package org.bladerunnerjs.utility;

import java.io.File;

public class RelativePathUtility {
	public static String get(File basePath, File childPath) {
//		String path =  basePath.toURI().relativize(childPath.toURI()).getPath();
		String path = getFast(basePath,childPath );
		return path;
	}



/*
 * This method is called a lot (20-30K times for the dashboard)
 * Changing this to string manipulation improves overall performance by 20-25%
 * File.toURI() seems to be very expensive
 */
private static String getFast(File basePath, File childPath) {
	String base = basePath.getAbsolutePath();
	String child = childPath.getAbsolutePath();

	String result = null;
	if(child.indexOf(base) == 0){
		if(child.equals(base)){
			return "";
		}
		result = child.substring( base.length() + 1);
	}else{
		result = child;
	}

	if(childPath.isDirectory()){
		result += File.separator;
	}
	return result;
}

}

