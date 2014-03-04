package com.caplin.cutlass.bundler.js.aliasing;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public interface AliasingNode {

	public void register() throws ContentProcessingException, NamespaceException;

	public void use() throws ContentProcessingException;

	void setContext(AliasContext context);

}
