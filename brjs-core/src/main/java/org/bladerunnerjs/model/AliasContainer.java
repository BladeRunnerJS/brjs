package org.bladerunnerjs.model;

import org.bladerunnerjs.model.aliasing.AliasDefinition;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;

public interface AliasContainer {

	public void addClassAlias(AliasDefinition alias, String scenario)
			throws BundlerProcessingException;

}