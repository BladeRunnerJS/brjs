package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.memoization.MemoizedValue;

public class JsStyleAccessor {
	private final Map<String, MemoizedValue<String>> dirStyleCache = new HashMap<>();
	private final BRJS brjs;
	
	public static final String DEFAULT_JS_STYLE = "common-js";
	
	public JsStyleAccessor(BRJS brjs) {
		this.brjs = brjs;
	}
	
	public String getJsStyle(File dir) {
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
				if ( parent == null || parent.equals(brjs.dir().getUnderlyingFile().getParentFile()) ){
					jsStyle = DEFAULT_JS_STYLE;
				} else {
					jsStyle = getJsStyle(parent);
				}
			}
			return jsStyle;
		});
	}
	
	public void setJsStyle(File dir, String jsStyle) {
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
