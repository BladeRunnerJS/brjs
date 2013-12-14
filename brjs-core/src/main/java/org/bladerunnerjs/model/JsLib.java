package org.bladerunnerjs.model;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;

public interface JsLib extends AssetContainer, NamedNode {
	// TODO: can this method be deleted now?
	AssetLocation getRootAssetLocation();
	JsLibConf libConf() throws ConfigException;
	void populate(String libNamespace) throws InvalidNameException, ModelUpdateException;
}
