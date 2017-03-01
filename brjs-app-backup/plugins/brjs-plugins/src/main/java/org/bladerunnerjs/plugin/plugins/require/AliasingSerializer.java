package org.bladerunnerjs.plugin.plugins.require;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasDefinition;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasException;
import org.bladerunnerjs.plugin.bundlers.aliasing.AliasingUtility;
import org.bladerunnerjs.plugin.require.AliasCommonJsSourceModule;

public class AliasingSerializer {
	public static String createJson(BundleSet bundleSet) {
		List<AliasDefinition> aliasDefinitions = getAliasDefinitions(bundleSet);
		
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
			if(aliasDefinition.getClassName() != null && !aliasDefinition.getClassName().equals(AliasingUtility.BR_UNKNOWN_CLASS_NAME))
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
		
		List<AliasCommonJsSourceModule> aliasModules = bundleSet.sourceModules(AliasCommonJsSourceModule.class);
		
		for (AliasCommonJsSourceModule aliasSourceModule : aliasModules) {
			AliasDefinition aliasDefinition = aliasSourceModule.getAliasDefinition();
			try
			{
				aliasDefinition = AliasingUtility.resolveAlias(aliasDefinition.getName(), bundleSet.bundlableNode());
			}
			catch (AliasException e)
			{
				// use the alias definition we had already
			}
			catch (ContentFileProcessingException ex)
			{
				throw new RuntimeException(ex);
			}
			aliasDefinitions.add(aliasDefinition);
		}
		
		return aliasDefinitions;
	}
}
