package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.plugin.bundler.js.NodeJsBundlerPlugin;

public class JsStyleUtility {
		
	public static String getJsStyle(File dir) {
		String jsStyle = null;
		
		do {
			jsStyle = readJsStyleFile(dir);
			
			dir = dir.getParentFile();
		} while((jsStyle == null) && (dir != null));
		
		return (jsStyle != null) ? jsStyle : NodeJsBundlerPlugin.JS_STYLE;
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
				jsStyle = FileUtils.readFileToString(jsStyleFile).trim();
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return jsStyle;
	}
	
}
