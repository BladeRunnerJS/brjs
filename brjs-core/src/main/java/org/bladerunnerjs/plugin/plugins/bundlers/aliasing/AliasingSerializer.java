package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.aliases.AliasesFile;
import org.bladerunnerjs.model.BundleSet;

public class AliasingSerializer {
	public static String createJson(BundleSet bundleSet) {
		StringBuilder jsonData = new StringBuilder();
		boolean firstAlias = true;
		
		jsonData.append("{");
		
		for(AliasDefinition aliasDefinition : bundleSet.getActiveAliases())
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
				jsonData.append("'requirePath':'" + aliasDefinition.getRequirePath() + "','className':'" + aliasDefinition.getClassName() + "'");
				classesInfoWritten = true;
			}
			
			if(aliasDefinition.getInterfaceName() != null)
			{
				if(classesInfoWritten)
				{
					jsonData.append(",");
				}
				
				jsonData.append("'interface':" + aliasDefinition.getInterfaceName() + ",'interfaceName':'" + aliasDefinition.getInterfaceName() + "'");
			}
			
			jsonData.append("}");
		}
		
		jsonData.append("}");
		
		return jsonData.toString();
	}
}
