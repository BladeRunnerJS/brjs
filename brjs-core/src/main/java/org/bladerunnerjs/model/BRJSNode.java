package org.bladerunnerjs.model;

import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.engine.Node;


public interface BRJSNode extends Node {
	BRJS root();
	void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
	String getTemplateName();
	void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException;
}
