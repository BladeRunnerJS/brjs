package org.bladerunnerjs.utility;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.template.TemplateNotFoundException;
import org.bladerunnerjs.plugin.CommandPlugin;

public class TemplateVerificationUtility {

	public static boolean templateExists(BRJS brjs, BRJSNode node, String templateGroup, CommandPlugin command) throws CommandArgumentsException {
		if (!brjs.confTemplateGroup(templateGroup).exists() && !brjs.sdkTemplateGroup(templateGroup).exists()) {
			throw new CommandArgumentsException(new TemplateNotFoundException(("The '" + templateGroup + "' template group "
					+ "could not be found at '" + brjs.confTemplateGroup(templateGroup).dir() + "'.")), command);
		}
		if (!brjs.confTemplateGroup(templateGroup).template(node.getTemplateName()).dir().exists() &&
				!brjs.sdkTemplateGroup(templateGroup).template(node.getTemplateName()).dir().exists()) {
			throw new CommandArgumentsException(new TemplateNotFoundException("The '" + node.getTemplateName() + 
					"' template for the '" + templateGroup + "' template" + " group could not be found at '" 
					+ brjs.confTemplateGroup(templateGroup).template(node.getTemplateName()).dir() + "'."), command);
		}	
		return true;
	}
	
}
