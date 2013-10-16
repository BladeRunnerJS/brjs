package com.caplin.cutlass.bundler.js.aliasing;

import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public interface AliasingNodeReader 
{
	AliasingNode getCurrentNode() throws BundlerFileProcessingException, XMLStreamException, NamespaceException;
}
