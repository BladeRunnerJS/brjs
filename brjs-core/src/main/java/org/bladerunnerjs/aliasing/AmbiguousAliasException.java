package org.bladerunnerjs.aliasing;

import java.io.File;
import java.util.List;

import com.google.common.base.Joiner;

public class AmbiguousAliasException extends AliasException {
	private static final long serialVersionUID = 1L;
	
	public AmbiguousAliasException(File aliasesFile, String aliasName, String scenarioName) {
		super("Alias '" + aliasName + "' for scenario '" + scenarioName + "' has multiple definitions for aliases file '" + aliasesFile.getPath() + "', or other files that it inherits from");
	}
	
	public AmbiguousAliasException(File aliasesFile, String aliasName, List<String> groupNames) {
		super("Alias '" + aliasName + "' for groups '" + Joiner.on("', '").join(groupNames) + "' has multiple definitions for aliases file '" + aliasesFile.getPath() + "', or other files that it inherits from");
	}
}
