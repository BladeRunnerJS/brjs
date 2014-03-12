package org.bladerunnerjs.aliasing;

import java.io.File;

public class IncompleteAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public IncompleteAliasException(File aliasesFile, String aliasName) {
		super("The partially defined '" + aliasesFile.getAbsolutePath() + "' alias has not been made concrete within '" + aliasName + "', even though this alias is in use within the bundle.");
	}
}
