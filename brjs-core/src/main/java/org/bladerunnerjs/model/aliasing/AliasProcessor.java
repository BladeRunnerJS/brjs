package org.bladerunnerjs.model.aliasing;

import java.util.List;

public class AliasProcessor {
	public static AliasDefinition getAlias(AliasName aliasName, String scenarioName, AliasesFile aliasesFile, List<AliasDefinitionsFile> aliasDefinitionFiles) throws UnresolvableAliasException, AmbiguousAliasException {
		AliasDefinition aliasDefinition = aliasesFile.getAlias(aliasName, scenarioName);
		
		if(aliasDefinition == null) {
			List<String> groupNames = aliasesFile.getGroupNames();
			
			for(AliasDefinitionsFile aliasDefinitionsFile : aliasDefinitionFiles) {
				AliasDefinition nextAliasDefinition = aliasDefinitionsFile.getAlias(aliasName, scenarioName, groupNames);
				
				if(nextAliasDefinition != null) {
					throw new AmbiguousAliasException(aliasesFile, aliasName, scenarioName);
				}
			}
		}
		
		if(aliasDefinition == null) {
			throw new UnresolvableAliasException(aliasesFile, aliasName, scenarioName);
		}
		
		return aliasDefinition;
	}
}
