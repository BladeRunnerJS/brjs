package com.caplin.cutlass.bundler.js.aliasing;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public class AliasesNodeReader implements AliasingNodeReader {

	private XMLStreamReader2 underlyingStreamReader;
	
	public AliasesNodeReader(XMLStreamReader2 underlyingStreamReader) {
		this.underlyingStreamReader = underlyingStreamReader;
	}
	
	@Override
	public AliasingNode getCurrentNode() throws BundlerFileProcessingException,	XMLStreamException, NamespaceException {
		
		String scenario = this.underlyingStreamReader.getAttributeValue(null, "useScenario");
		String groupName = this.underlyingStreamReader.getAttributeValue(null, "useGroups");
		
		return new AliasesNode(scenario, groupName);
	}

}
