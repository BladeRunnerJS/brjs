package com.caplin.cutlass.bundler.js.aliasing;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import com.caplin.cutlass.exception.NamespaceException;


public class AliasingStreamReader
{
	
	private XMLStreamReader2 underlyingStreamReader;
	private Map<String, AliasingNodeReader> nodeReaders;
	
	public AliasingStreamReader(XMLStreamReader2 underlyingStreamReader)
	{
		this.underlyingStreamReader = underlyingStreamReader;
		this.nodeReaders = new HashMap<String, AliasingNodeReader>();
		this.nodeReaders.put("alias", new AliasNodeReader(underlyingStreamReader));
		this.nodeReaders.put("aliases", new AliasesNodeReader(underlyingStreamReader));
		this.nodeReaders.put("group", new GroupNodeReader(underlyingStreamReader));
	}

	public AliasingNode getNextNode() throws XMLStreamException, NamespaceException, ContentFileProcessingException
	{
		underlyingStreamReader.next();
		
		AliasingNodeReader nodeReader = null;
		if(underlyingStreamReader.isStartElement())
		{
			String elementName = underlyingStreamReader.getLocalName().toString();
			nodeReader = this.nodeReaders.get(elementName);
		}
		if(nodeReader != null)
		{
			return nodeReader.getCurrentNode();
		}
		return new UnusedNode();
	}

	public boolean hasNext() throws XMLStreamException {
		return underlyingStreamReader.hasNext();
	}

	public int getColumnNumber() {
		return underlyingStreamReader.getLocation().getColumnNumber();
	}

	public int getLineNumber() {
		return underlyingStreamReader.getLocation().getLineNumber();
	}
	
}
