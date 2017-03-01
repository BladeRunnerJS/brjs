package org.bladerunnerjs.plugin.bundlers.aliasing;


public class UnresolvableAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public UnresolvableAliasException(AliasesFile aliasesFile, String aliasName) {
		super("Alias '" + aliasName + "' has not been defined within '" + aliasesFile.getUnderlyingFile().getPath() + "' or any other files that it inherits from");
	}
}
