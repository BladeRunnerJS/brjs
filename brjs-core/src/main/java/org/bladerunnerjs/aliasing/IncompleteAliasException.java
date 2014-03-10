package org.bladerunnerjs.aliasing;

import java.io.File;

public class IncompleteAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public IncompleteAliasException(File aliasesFile, String aliasName) {
		super("Alias '" + aliasName + "' defined within '" + aliasesFile.getAbsolutePath() + "' is only partially defined as it does not define a class name.");
	}
}
