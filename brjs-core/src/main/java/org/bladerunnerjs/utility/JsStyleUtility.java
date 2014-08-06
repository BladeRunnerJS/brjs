package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;

public class JsStyleUtility {
		
	private static Map<String, String> dirStyleCache = new HashMap<String, String>();
	
	
	public static String getJsStyle(File dir) {
		// TODO: if any .js-style files change (or are added/removed) the server must restart
		// We also recurse up out of BRJS root - should stop doing that.
		String path = dir.getAbsolutePath();
		String jsStyle = dirStyleCache.get(path);
		if(jsStyle == null){
			jsStyle = readJsStyleFile(dir);
			if(jsStyle == null){
				File parent = dir.getParentFile();
				if(parent == null){
					jsStyle = CommonJsSourceModule.JS_STYLE;
				}else{
					jsStyle = getJsStyle(parent);
				}
			}
			dirStyleCache.put(path, jsStyle);
		}
		return jsStyle;
	}
	public  static void clear(){
		dirStyleCache = new HashMap<String, String>();
	}
	
	public static void setJsStyle(File dir, String jsStyle) {
		try {
			File jsStyleFile = new File(dir, ".js-style");
			
			FileUtils.writeStringToFile(jsStyleFile, jsStyle, "UTF-8");
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		//TODO: Dont invalidate whole tree, just sub branches
		dirStyleCache = new HashMap<String, String>();
	}
	
	private static String readJsStyleFile(File dir) {
		String jsStyle = null;
		
		try {
			File jsStyleFile = new File(dir, ".js-style");
			
			if(jsStyleFile.exists()) {
				jsStyle = FileUtils.readFileToString(jsStyleFile, "UTF-8").trim();
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return jsStyle;
	}
	
}
