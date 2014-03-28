package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

public class BRJSMemoizationFileAccessException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
	public BRJSMemoizationFileAccessException(File file, File[] scopeFiles) {
		super("The file '" + file.getAbsolutePath() + "' was not within any of the allowed scopes: " + getScopes(scopeFiles));
	}
	
	private static String getScopes(File[] scopeFiles) {
		List<String> filePaths = new ArrayList<>();
		
		for(File scopeFile : scopeFiles) {
			filePaths.add("'" + scopeFile.getAbsolutePath() + "'");
		}
		
		return Joiner.on(", ").join(filePaths);
	}
}
