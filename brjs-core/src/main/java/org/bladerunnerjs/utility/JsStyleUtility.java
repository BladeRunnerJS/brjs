package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;

public class JsStyleUtility {
		
	// TODO: stop recursing outside of the BRJS root dir
	private static Map<String, MemoizedValue<String>> dirStyleCache = new HashMap<>();
	
	public static String getJsStyle(BRJS brjs, File dir) {
		String path = dir.getAbsolutePath();
		MemoizedValue<String> jsStyleMemoizedValue = dirStyleCache.get(path);
		
		if (jsStyleMemoizedValue == null) {
			jsStyleMemoizedValue = new MemoizedValue<String>("JsStyle", brjs);
			dirStyleCache.put(path, jsStyleMemoizedValue);
		}
		
		return jsStyleMemoizedValue.value(() -> {
			String jsStyle = readJsStyleFile(dir);
			if(jsStyle == null){
				File parent = dir.getParentFile();
				if(parent == null){
					jsStyle = CommonJsSourceModule.JS_STYLE;
				}else{
					jsStyle = getJsStyle(brjs, parent);
				}
			}
			return jsStyle;
		});
	}
	
	public static void setJsStyle(BRJS brjs, File dir, String jsStyle) {
		try {
			File jsStyleFile = new File(dir, ".js-style");
			
			FileUtils.write(brjs, jsStyleFile, jsStyle, "UTF-8");
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
				jsStyle = org.apache.commons.io.FileUtils.readFileToString(jsStyleFile, "UTF-8").trim();
			}
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		return jsStyle;
	}
	
}
