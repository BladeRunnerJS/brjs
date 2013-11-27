package org.bladerunnerjs.model.aliasing;

import java.util.List;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;

public class AliasUtility {
	public static AliasDefinition getAlias(AliasName aliasName, AliasesFile aliasesFile, List<AliasDefinitionsFile> aliasDefinitionFiles) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException {
		AliasDefinition aliasDefinition = aliasesFile.getAlias(aliasName);		
		
		if(aliasDefinition == null) {
			String scenarioName = aliasesFile.scenarioName();
			List<String> groupNames = aliasesFile.groupNames();
			
			for(AliasDefinitionsFile aliasDefinitionsFile : aliasDefinitionFiles) {
				AliasDefinition nextAliasDefinition = aliasDefinitionsFile.getAlias(aliasName, scenarioName, groupNames);
				
				if(aliasDefinition != null && nextAliasDefinition != null) {
					throw new AmbiguousAliasException(aliasesFile, aliasName, scenarioName);
				}
				
				if (nextAliasDefinition != null)
				{
					aliasDefinition = nextAliasDefinition;
				}
			}
		}
		
		if(aliasDefinition == null) {
			throw new UnresolvableAliasException(aliasesFile, aliasName);
		}
		
		return aliasDefinition;
	}
}
