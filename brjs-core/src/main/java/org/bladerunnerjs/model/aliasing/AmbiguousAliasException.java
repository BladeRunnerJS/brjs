package org.bladerunnerjs.model.aliasing;

import java.io.File;

public class AmbiguousAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public AmbiguousAliasException(File aliasesFile, AliasName aliasName, String scenarioName) {
		super("Alias '" + aliasName.getName() + "' for scenario '" + scenarioName + "' has multiple definitions for aliases file '" + aliasesFile.getPath() + "', or other files that it inherits from");
	}
}
