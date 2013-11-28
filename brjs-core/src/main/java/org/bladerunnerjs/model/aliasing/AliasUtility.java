package org.bladerunnerjs.model.aliasing;

import java.util.List;

import org.bladerunnerjs.model.aliasing.aliasdefinitions.AliasDefinitionsFile;
import org.bladerunnerjs.model.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;

public class AliasUtility {
	public static AliasDefinition getAlias(String aliasName, AliasesFile aliasesFile, List<AliasDefinitionsFile> aliasDefinitionFiles) throws UnresolvableAliasException, AmbiguousAliasException, BundlerFileProcessingException {
		// TODO: change how this method works, so that inheritance of the interface name works
		AliasOverride aliasOverride = aliasesFile.getAlias(aliasName);
		AliasDefinition aliasDefinition = (aliasOverride == null) ? null : new AliasDefinition(aliasOverride.getName(), aliasOverride.getClassName(), null);
		
		if(aliasDefinition == null) {
			String scenarioName = aliasesFile.scenarioName();
			List<String> groupNames = aliasesFile.groupNames();
			
			for(AliasDefinitionsFile aliasDefinitionsFile : aliasDefinitionFiles) {
				AliasDefinition nextAliasDefinition = aliasDefinitionsFile.getAlias(aliasName, scenarioName, groupNames);
				
				if(aliasDefinition != null) {
					throw new AmbiguousAliasException(aliasesFile.getUnderlyingFile(), aliasName, scenarioName);
				}
				
				aliasDefinition = nextAliasDefinition;
			}
		}
		
		if(aliasDefinition == null) {
			throw new UnresolvableAliasException(aliasesFile, aliasName);
		}
		
		return aliasDefinition;
	}
}
