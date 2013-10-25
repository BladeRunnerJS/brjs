package com.caplin.cutlass.bundler.js.aliasing;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public interface AliasingNode {

	public void register() throws BundlerProcessingException, NamespaceException;

	public void use() throws BundlerProcessingException;

	void setContext(AliasContext context);

}
