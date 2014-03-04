package com.caplin.cutlass.bundler.js.aliasing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public class GroupNodeReader implements AliasingNodeReader {

	private XMLStreamReader2 underlyingStreamReader;
	
	public GroupNodeReader(XMLStreamReader2 underlyingStreamReader) {
		this.underlyingStreamReader = underlyingStreamReader;
	}

	@Override
	public AliasingNode getCurrentNode() throws ContentFileProcessingException, XMLStreamException, NamespaceException {
		String groupName = underlyingStreamReader.getAttributeValue(null, "name");
		
		List<AliasNode> aliasNodes = getAliasNodes();
		
		return new GroupNode(groupName, aliasNodes);
	}

	private List<AliasNode> getAliasNodes() throws ContentFileProcessingException, XMLStreamException, NamespaceException {
		
		List<AliasNode> aliasNodes = new ArrayList<AliasNode>();
		AliasingNodeReader aliasNodeReader = new AliasNodeReader(underlyingStreamReader);
		boolean insideGroup = true;
		
		while(underlyingStreamReader.hasNext() && insideGroup)
		{
			if(underlyingStreamReader.isStartElement() && underlyingStreamReader.getLocalName().toString().equals("alias"))
			{
				AliasNode aliasNode = (AliasNode)aliasNodeReader.getCurrentNode(); 
				aliasNodes.add(aliasNode);
				
			}
			else
			{
				if (underlyingStreamReader.isEndElement() && underlyingStreamReader.getLocalName().toString().equals("group"))
				{
					insideGroup = false;
				}
			
			}
			underlyingStreamReader.next();
			
		}
		
		return aliasNodes;
	}

}
