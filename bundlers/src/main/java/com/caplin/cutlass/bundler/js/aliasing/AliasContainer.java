package com.caplin.cutlass.bundler.js.aliasing;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public interface AliasContainer {

	public void addClassAlias(AliasDefinition alias, String scenario)
			throws ContentProcessingException;

}