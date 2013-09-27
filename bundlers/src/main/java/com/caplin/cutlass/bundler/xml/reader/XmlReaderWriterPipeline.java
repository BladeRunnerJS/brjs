package com.caplin.cutlass.bundler.xml.reader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


public class XmlReaderWriterPipeline
{
	public static void cloneElements(final XmlSiblingReader reader, final XMLStreamWriter writer, boolean outputContinuously)
		throws XMLStreamException, XmlSiblingReaderException
	{
		boolean hasNextSibling = false;
		
		do
		{
			if(reader.getCharacterData() != null)
			{
				writer.writeCharacters(reader.getCharacterData());
			}
			else
			{
				cloneElement(reader, writer, outputContinuously);
			}
			hasNextSibling = reader.nextSiblingOrCharacterData();
		} while(hasNextSibling == true);
	}
	
	public static void cloneElement(final XmlSiblingReader reader, final XMLStreamWriter writer, boolean outputContinuously)
		throws XMLStreamException, XmlSiblingReaderException
	{
		writer.writeStartElement(reader.getElementName());
		
		for(int i = 0, l = reader.getNamespaceCount(); i < l; ++i)
		{
			writer.writeNamespace(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
		}
		flush(writer, outputContinuously);
		
		for(int i = 0, l = reader.getAttributeCount(); i < l; ++i)
		{
			writer.writeAttribute(reader.getAttributeName(i), reader.getAttributeValue(i));
		}
		flush(writer, outputContinuously);
		
		XmlSiblingReader childReader = reader.getChildReader();
		if(childReader != null)
		{
			cloneElements(childReader, writer, outputContinuously);
		}
		
		writer.writeEndElement();
		flush(writer, outputContinuously);
	}
	
	/**
	 * This method is only here to make debugging easier, and it's use is enabled within the unit tests
	 * @throws XMLStreamException 
	 */
	private static void flush(XMLStreamWriter writer, boolean outputContinuously) throws XMLStreamException
	{
		if(outputContinuously)
		{
			writer.flush();
		}
	}
}
