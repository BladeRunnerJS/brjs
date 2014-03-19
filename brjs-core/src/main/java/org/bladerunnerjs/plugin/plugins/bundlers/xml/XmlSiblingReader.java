package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.File;

import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.exception.RequirePathException;

public class XmlSiblingReader
{
	private XmlSiblingReader parent = null;
	private PeekableXmlStreamReader streamReader;
	private XmlSiblingReader childReader = null;
	private int depth = 1;
	private File document;
	private Asset xmlAsset;
	
	public XmlSiblingReader(XMLStreamReader streamReader) throws XMLStreamException
	{
		streamReader.nextTag();
		this.streamReader = new PeekableXmlStreamReader(streamReader);
	}
	
	private XmlSiblingReader(XmlSiblingReader parent, PeekableXmlStreamReader streamReader)
	{
		this.parent = parent;
		this.streamReader = streamReader;
	}
	
	// TODO: quick hack to allow the unit tests to pass -- needs to be implemented properly tomorrow
	private XmlSiblingReader(XmlSiblingReader parent, PeekableXmlStreamReader streamReader, int depth)
	{
		this.depth = depth;
		this.parent = parent;
		this.streamReader = streamReader;
	}
	
	public String getElementName()
	{

		String prefix;
		StringBuilder nodeName = new StringBuilder();
		
		if (streamReader.hasName() == false) {
			return null;
		}
		
		prefix = streamReader.getPrefix();
		if (!prefix.equals("")) {
			nodeName.append(prefix).append(":");
		}
		
		nodeName.append(streamReader.getLocalName());
		
		return nodeName.toString();
	}
	
	public int getNamespaceCount()
	{
		return streamReader.getNamespaceCount();
	}
	
	public String getNamespacePrefix(int index)
	{
		return streamReader.getNamespacePrefix(index);
	}
	
	public String getNamespaceURI(int index)
	{
		return streamReader.getNamespaceURI(index);
	}

	public boolean hasNextSibling() throws XMLStreamException
	{
		boolean hasNextSibling = false;
		
		if(streamReader.hasNext())
		{
			if(depth > 0)
			{
				hasNextSibling = true;
			}
			else if(depth == 0)
			{
				XMLStreamReader peekedStreamReader = streamReader.peekToNextTag();
				
				if(peekedStreamReader.isStartElement())
				{
					hasNextSibling = true;
				}
			}
		}
		
		return hasNextSibling;
	}
	
	public NamespaceContext getNamespaceContext()
	{
		return streamReader.getNamespaceContext();
	}
	
	public String getCharacterData() throws XMLStreamException, XmlSiblingReaderException
	{
		return (streamReader.isCharacters() && !streamReader.isWhiteSpace()) ? streamReader.getText() : null;
	}
	
	public XmlSiblingReader getChildReader() throws XMLStreamException, XmlSiblingReaderException
	{
		childReader = null;
		
		do
		{
			iterateStreamReader();
			
			if(depth == 2)
			{
				childReader = new XmlSiblingReader(this, streamReader);
			}
			else if((depth == 1) && (getCharacterData() != null))
			{
				childReader = new XmlSiblingReader(this, streamReader, 0);
			}
			else if(depth == 0)
			{
				break;
			}
		} while(childReader == null);
		
		return childReader;
	}
	
	public boolean nextSibling() throws XMLStreamException, XmlSiblingReaderException
	{
		return getNextSibling(false, false);
	}
	
	public boolean nextSiblingOrCharacterData() throws XMLStreamException, XmlSiblingReaderException
	{
		return getNextSibling(false, true);
	}
	
	public boolean skipToNextSibling() throws XMLStreamException, XmlSiblingReaderException
	{
		boolean hasNextSibling = false;
		
		do
		{
			hasNextSibling = getNextSibling(true, false);
		} while(depth > 1);
		
		return hasNextSibling;
	}
	
	public void setAsset(Asset xmlAsset) {
		this.xmlAsset = xmlAsset;
	}
	
	public void assertIdentifierCorrectlyNamespaced(String identifier) throws NamespaceException, RequirePathException {
		if(xmlAsset == null) {
			parent.assertIdentifierCorrectlyNamespaced(identifier);
		}
		else {
			xmlAsset.assetLocation().assertIdentifierCorrectlyNamespaced(identifier);
		}
	}
	
	public void setXmlDocument(File document)
	{
		this.document = document;
	}
	
	public File getXmlDocument()
	{
		if(document == null)
		{
			return parent.getXmlDocument();
		}
		
		return document;
	}
	
	public Location getLocation()
	{
		return streamReader.getLocation();
	}
	
	private boolean getNextSibling(boolean ignoreUnprocessedChildren, boolean returnCharacterData) throws XMLStreamException, XmlSiblingReaderException
	{
		boolean hasNextSibling = false;
		
		if(depth >= 0)
		{
			boolean isExitElement = false;
			
			do
			{
				iterateStreamReader();
				
				if(depth >= 2)
				{
					if(ignoreUnprocessedChildren == false)
					{
						throw new XmlSiblingReaderException("getNextSibling() called before previous XmlSiblingReader was exhausted.");
					}
				}
				else if(depth < 0)
				{
					break;
				}
				
				isExitElement = (returnCharacterData) ? (streamReader.isStartElement() || (streamReader.isCharacters() && !streamReader.isWhiteSpace())) : streamReader.isStartElement();
			} while(!isExitElement && streamReader.hasNext());
			
			if(isExitElement)
			{
				hasNextSibling = true;
				childReader = null;
			}
		}
		
		return hasNextSibling;
	}
	
	private void iterateStreamReader() throws XMLStreamException
	{
		streamReader.next();
		
		if(streamReader.isStartElement())
		{
			incrementDepth();
		}
		else if(streamReader.isEndElement())
		{
			decrementDepth();
		}
	}
	
	public int getAttributeCount()
	{
		return streamReader.getAttributeCount();
	}
	
	public String getAttributeName(int index)
	{
		return streamReader.getAttributeLocalName(index);
	}
	
	public String getAttributeValue(int index)
	{
		return streamReader.getAttributeValue(index);
	}
	
	public String getAttributeValue(String attributeName)
	{
		return streamReader.getAttributeValue(null, attributeName);
	}
	
	public void close() throws XMLStreamException
	{
		streamReader.close();
	}
	
	private void incrementDepth()
	{
		depth++;
		
		if(parent != null)
		{
			parent.incrementDepth();
		}
	}
	
	private void decrementDepth()
	{
		depth--;
		
		if(parent != null)
		{
			parent.decrementDepth();
		}
	}
}
