package org.bladerunnerjs.model.aliasing;

public class UnresolvableAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public UnresolvableAliasException(AliasesFile aliasesFile, String aliasName) {
		super("Alias '" + aliasName + "' has multiple definitions for aliases file 'aliasesFile', or other " + aliasesFile.getUnderlyingFile().getPath() + " files that it inherits from");
	}
}
