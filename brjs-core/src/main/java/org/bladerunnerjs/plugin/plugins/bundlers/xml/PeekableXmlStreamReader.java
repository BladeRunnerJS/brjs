package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class PeekableXmlStreamReader implements XMLStreamReader
{
	private XMLStreamReader currentStreamReader;
	private XMLStreamReader peekedStreamReader = null;
	private int peekedNextTagResult;
	
	public PeekableXmlStreamReader(XMLStreamReader streamReader)
	{
		this.currentStreamReader = streamReader;
	}
	
	public XMLStreamReader peekToNextTag() throws XMLStreamException
	{
		if(peekedStreamReader == null)
		{
			peekedStreamReader = currentStreamReader;
			currentStreamReader = new CachedXmlStreamReader(currentStreamReader);
			
			do
			{
				peekedNextTagResult = peekedStreamReader.next();
			} while(!peekedStreamReader.isStartElement() && !peekedStreamReader.isEndElement() && peekedStreamReader.hasNext());
		}
		
		return peekedStreamReader;
	}
	
	public void close() throws XMLStreamException
	{
		currentStreamReader.close();
	}
	
	public int getAttributeCount()
	{
		return currentStreamReader.getAttributeCount();
	}
	
	public String getAttributeLocalName(int arg0)
	{
		return currentStreamReader.getAttributeLocalName(arg0);
	}
	
	public QName getAttributeName(int arg0)
	{
		return currentStreamReader.getAttributeName(arg0);
	}
	
	public String getAttributeNamespace(int arg0)
	{
		return currentStreamReader.getAttributeNamespace(arg0);
	}
	
	public String getAttributePrefix(int arg0)
	{
		return currentStreamReader.getAttributePrefix(arg0);
	}
	
	public String getAttributeType(int arg0)
	{
		return currentStreamReader.getAttributeType(arg0);
	}
	
	public String getAttributeValue(int arg0)
	{
		return currentStreamReader.getAttributeValue(arg0);
	}
	
	public String getAttributeValue(String arg0, String arg1)
	{
		return currentStreamReader.getAttributeValue(arg0, arg1);
	}
	
	public String getCharacterEncodingScheme()
	{
		return currentStreamReader.getCharacterEncodingScheme();
	}
	
	public String getElementText() throws XMLStreamException
	{
		return currentStreamReader.getElementText();
	}
	
	public String getEncoding()
	{
		return currentStreamReader.getEncoding();
	}
	
	public int getEventType()
	{
		return currentStreamReader.getEventType();
	}
	
	public String getLocalName()
	{
		return currentStreamReader.getLocalName();
	}
	
	public Location getLocation()
	{
		return currentStreamReader.getLocation();
	}
	
	public QName getName()
	{
		return currentStreamReader.getName();
	}
	
	public NamespaceContext getNamespaceContext()
	{
		return currentStreamReader.getNamespaceContext();
	}
	
	public int getNamespaceCount()
	{
		return currentStreamReader.getNamespaceCount();
	}
	
	public String getNamespacePrefix(int arg0)
	{
		return currentStreamReader.getNamespacePrefix(arg0);
	}
	
	public String getNamespaceURI()
	{
		return currentStreamReader.getNamespaceURI();
	}
	
	public String getNamespaceURI(String arg0)
	{
		return currentStreamReader.getNamespaceURI(arg0);
	}
	
	public String getNamespaceURI(int arg0)
	{
		return currentStreamReader.getNamespaceURI(arg0);
	}
	
	public String getPIData()
	{
		return currentStreamReader.getPIData();
	}
	
	public String getPITarget()
	{
		return currentStreamReader.getPITarget();
	}
	
	public String getPrefix()
	{
		return currentStreamReader.getPrefix();
	}
	
	public Object getProperty(String arg0) throws IllegalArgumentException
	{
		return currentStreamReader.getProperty(arg0);
	}
	
	public String getText()
	{
		return currentStreamReader.getText();
	}
	
	public char[] getTextCharacters()
	{
		return currentStreamReader.getTextCharacters();
	}
	
	public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException
	{
		return currentStreamReader.getTextCharacters(arg0, arg1, arg2, arg3);
	}
	
	public int getTextLength()
	{
		return currentStreamReader.getTextLength();
	}
	
	public int getTextStart()
	{
		return currentStreamReader.getTextStart();
	}
	
	public String getVersion()
	{
		return currentStreamReader.getVersion();
	}
	
	public boolean hasName()
	{
		return currentStreamReader.hasName();
	}
	
	public boolean hasNext() throws XMLStreamException
	{
		return currentStreamReader.hasNext();
	}
	
	public boolean hasText()
	{
		return currentStreamReader.hasText();
	}
	
	public boolean isAttributeSpecified(int arg0)
	{
		return currentStreamReader.isAttributeSpecified(arg0);
	}
	
	public boolean isCharacters()
	{
		return currentStreamReader.isCharacters();
	}
	
	public boolean isEndElement()
	{
		return currentStreamReader.isEndElement();
	}
	
	public boolean isStandalone()
	{
		return currentStreamReader.isStandalone();
	}
	
	public boolean isStartElement()
	{
		return currentStreamReader.isStartElement();
	}
	
	public boolean isWhiteSpace()
	{
		return currentStreamReader.isWhiteSpace();
	}
	
	public int next() throws XMLStreamException
	{
		if(peekedStreamReader == null)
		{
			if(currentStreamReader.hasNext())
			{
				return currentStreamReader.next();
			}
			else
			{
				throw new XMLStreamException("No more items.");
			}
		}
		else
		{
			// TODO: fobbing the user of with nextTag() when they invoked next() is a hack, but should work
			return nextTag();
		}
	}
	
	public int nextTag() throws XMLStreamException
	{
		if(peekedStreamReader == null)
		{
			return currentStreamReader.nextTag();
		}
		else
		{
			currentStreamReader = peekedStreamReader;
			peekedStreamReader = null;
			return peekedNextTagResult;
		}
	}
	
	public void require(int arg0, String arg1, String arg2) throws XMLStreamException
	{
		currentStreamReader.require(arg0, arg1, arg2);
	}
	
	public boolean standaloneSet()
	{
		return currentStreamReader.standaloneSet();
	}
	
	private class CachedXmlStreamReader implements XMLStreamReader
	{
		private final String UNIMPLEMENTED_METHOD_MESSAGE = "This method has not been implemented yet";
		private String localName;
		private boolean hasNext;
		private boolean hasName;
		
		public CachedXmlStreamReader(XMLStreamReader streamReader) throws XMLStreamException
		{
			localName = streamReader.getLocalName();
			hasNext = streamReader.hasNext();
			hasName = streamReader.hasName();
		}
		
		public void close() throws XMLStreamException
		{
			// do nothing
		}
		
		public int getAttributeCount()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getAttributeLocalName(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public QName getAttributeName(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getAttributeNamespace(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getAttributePrefix(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getAttributeType(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getAttributeValue(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getAttributeValue(String arg0, String arg1)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getCharacterEncodingScheme()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getElementText() throws XMLStreamException
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getEncoding()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int getEventType()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getLocalName()
		{
			return localName;
		}
		
		public Location getLocation()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public QName getName()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public NamespaceContext getNamespaceContext()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int getNamespaceCount()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getNamespacePrefix(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getNamespaceURI()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getNamespaceURI(String arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getNamespaceURI(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getPIData()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getPITarget()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getPrefix()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public Object getProperty(String arg0) throws IllegalArgumentException
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getText()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public char[] getTextCharacters()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int getTextCharacters(int arg0, char[] arg1, int arg2, int arg3) throws XMLStreamException
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int getTextLength()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int getTextStart()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public String getVersion()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean hasName()
		{
			return hasName;
		}
				
		public boolean hasNext() throws XMLStreamException
		{
			return hasNext;
		}
		
		public boolean hasText()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean isAttributeSpecified(int arg0)
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean isCharacters()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean isEndElement()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean isStandalone()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean isStartElement()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public boolean isWhiteSpace()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int next() throws XMLStreamException
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public int nextTag() throws XMLStreamException
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
		
		public void require(int arg0, String arg1, String arg2) throws XMLStreamException
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
			
		}
		
		public boolean standaloneSet()
		{
			throw new UnsupportedOperationException(UNIMPLEMENTED_METHOD_MESSAGE);
		}
	}
}
