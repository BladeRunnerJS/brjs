package org.bladerunnerjs.model;

import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public interface BRJSNode extends Node {
	BRJS root();
	void populate() throws InvalidNameException, ModelUpdateException;
	long lastModified();
	String getTemplateName();
	void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException;
}
