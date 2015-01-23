package org.bladerunnerjs.api;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;

public interface JsLib extends AssetContainer, NamedNode, TestableNode {
	void populate(String libNamespace, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
}
