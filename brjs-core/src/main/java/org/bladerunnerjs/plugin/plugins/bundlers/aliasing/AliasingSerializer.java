package org.bladerunnerjs.plugin.plugins.bundlers.aliasing;

import org.bladerunnerjs.aliasing.AliasDefinition;
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
			
			if(aliasDefinition.getClassName() != null)
			{
				jsonData.append("'class':require('" + aliasDefinition.getRequirePath() + "'),'className':'" + aliasDefinition.getClassName() + "'");
			}
			
			if(aliasDefinition.getInterfaceName() != null)
			{
				if(aliasDefinition.getClassName() != null)
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
