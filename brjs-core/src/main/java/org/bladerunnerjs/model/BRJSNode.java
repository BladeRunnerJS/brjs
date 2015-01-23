package org.bladerunnerjs.model;

import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;


public interface BRJSNode extends Node {
	BRJS root();
	void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
	String getTemplateName();
	void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException;
}
