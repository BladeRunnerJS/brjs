package org.bladerunnerjs.model;

import java.io.File;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;

public class ImplicitTemplateHandlerUtility {

	public void populateOrCreate(BRJSNode node, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException {
		File confTemplateDir = node.root().confTemplateGroup(templateGroup).template(node.getTemplateName()).dir();
		File sdkTemplateDir = node.root().sdkTemplateGroup(templateGroup).template(node.getTemplateName()).dir();
		if (confTemplateDir.exists() || sdkTemplateDir.exists()) {
			node.populate(templateGroup);
		}
		else {
			node.create();
			node.ready();		
		}
	}
}
