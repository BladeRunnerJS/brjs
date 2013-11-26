package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class JsStyleUtility {
	public static String getJsStyle(File dir) {
		String jsStyle = null;
		
		do {
			jsStyle = readJsStyleFile(dir);
			
			dir = dir.getParentFile();
		} while((jsStyle == null) && (dir != null));
		
		return (jsStyle != null) ? jsStyle : "node.js";
	}
	
	public static void setJsStyle(File dir, String jsStyle) {
		try {
			File jsStyleFile = new File(dir, ".js-style");
			
			FileUtils.writeStringToFile(jsStyleFile, jsStyle);
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String readJsStyleFile(File dir) {
		String jsStyle = null;
		
		try {
			File jsStyleFile = new File(dir, ".js-style");
			
			if(jsStyleFile.exists()) {
				jsStyle = FileUtils.readFileToString(jsStyleFile);
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return jsStyle;
	}
}
