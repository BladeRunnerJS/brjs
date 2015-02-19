package org.bladerunnerjs.plugin.plugins.require;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.require.AliasCommonJsSourceModule;


public class AliasingSerializer {
	public static String createJson(BundleSet bundleSet) {
		List<AliasDefinition> aliasDefinitions = getAliasDefinitions(bundleSet);
		aliasDefinitions.addAll(bundleSet.getActiveAliases()); // TODO: delete this line once aliasing has been removed from the model
		
		StringBuilder jsonData = new StringBuilder();
		boolean firstAlias = true;
		
		jsonData.append("{");
		
		for(AliasDefinition aliasDefinition : aliasDefinitions)
		{
			if(firstAlias)
			{
				firstAlias = false;
			}
			else
			{
				jsonData.append(",");
			}
			
			jsonData.append("'" + aliasDefinition.getName() + "':{");
			
			boolean classesInfoWritten = false;
			if(aliasDefinition.getClassName() != null && !aliasDefinition.getClassName().equals(AliasesFile.BR_UNKNOWN_CLASS_NAME))
			{
				jsonData.append("'class':'" + aliasDefinition.getRequirePath() + "','className':'" + aliasDefinition.getClassName() + "'");
				classesInfoWritten = true;
			}
			
			if(aliasDefinition.getInterfaceName() != null)
			{
				if(classesInfoWritten)
				{
					jsonData.append(",");
				}
				
				jsonData.append("'interface':'" + aliasDefinition.getInterfaceRequirePath() + "','interfaceName':'" + aliasDefinition.getInterfaceName() + "'");
			}
			
			jsonData.append("}");
		}
		
		jsonData.append("}");
		
		return jsonData.toString();
	}

	private static List<AliasDefinition> getAliasDefinitions(BundleSet bundleSet) {
		List<AliasDefinition> aliasDefinitions = new ArrayList<>();
		
		for(SourceModule sourceModule : bundleSet.getSourceModules()) {
			if(sourceModule instanceof AliasCommonJsSourceModule) {
				AliasCommonJsSourceModule aliasSourceModule = (AliasCommonJsSourceModule) sourceModule;
				
				aliasDefinitions.add(aliasSourceModule.getAliasDefinition());
			}
		}
		
		return aliasDefinitions;
	}
}
