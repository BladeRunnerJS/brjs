package org.bladerunnerjs.model;

import java.io.File;


public class BRJSMemoizationFileAccessException extends SecurityException {
	private static final long serialVersionUID = 1L;
	
	public BRJSMemoizationFileAccessException(File file, File[] scopeFiles) {
		super("The file '" + file.getAbsolutePath() + "' was not within any of the allowed scopes: " + getScopes(scopeFiles));
	}
	
	private static String getScopes(File[] scopeFiles) {
		StringBuffer stringBuffer = new StringBuffer();
		boolean firstFile = true;
		
		for(File scopeFile : scopeFiles) {
			if(firstFile) {
				firstFile = false;
			}
			else {
				stringBuffer.append(", ");
			}
			
			stringBuffer.append("'" + scopeFile.getAbsolutePath() + "'");
		}
		
		return stringBuffer.toString();
	}
}
