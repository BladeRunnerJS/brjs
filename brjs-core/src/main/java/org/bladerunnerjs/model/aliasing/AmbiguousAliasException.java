package org.bladerunnerjs.model.aliasing;

public class AmbiguousAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public AmbiguousAliasException(AliasesFile aliasesFile, AliasName aliasName, String scenarioName) {
		super("Alias '" + aliasName.getName() + "' for scenario '" + scenarioName + "' has multiple definitions for aliases file 'aliasesFile', or other " + aliasesFile.getPath() + " files that it inherits from");
	}
}
