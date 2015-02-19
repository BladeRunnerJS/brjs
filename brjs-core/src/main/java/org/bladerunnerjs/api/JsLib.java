package org.bladerunnerjs.api;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.engine.NamedNode;

public interface JsLib extends AssetContainer, NamedNode, TestableNode {
	void populate(String libNamespace, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException;
}
