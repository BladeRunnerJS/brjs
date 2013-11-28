package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public interface JsLib extends AssetContainer, NamedNode
{
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException;
}
