package org.bladerunnerjs.aliasing;

import java.io.File;

public class IncompleteAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public IncompleteAliasException(File aliasesFile, String aliasName) {
		super("The partially defined '" + aliasName + "' alias has not been made concrete within '" + aliasesFile.getAbsolutePath() + "', even though this alias is in use within the bundle.");
	}
}
