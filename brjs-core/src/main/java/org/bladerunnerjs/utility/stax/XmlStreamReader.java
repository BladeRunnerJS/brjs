package org.bladerunnerjs.utility.stax;

import java.io.IOException;
import java.io.Reader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLStreamReader2;

public class XmlStreamReader implements AutoCloseable {
	private XMLStreamReader2 streamReader;
	private Reader reader;
	private int startDepth;
	
	public XmlStreamReader(XMLStreamReader2 streamReader, Reader reader) {
		this.streamReader = streamReader;
		this.reader = reader;
		startDepth = streamReader.getDepth() + 1;
	}
	
	public XmlStreamReader(XMLStreamReader2 streamReader) {
		this(streamReader, null);
	}
	
	@Override
	public void close() throws XMLStreamException {
		try {
			streamReader.close();
			
			if(reader != null) {
				reader.close();
			}
		}
		catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}
	
	public boolean hasNextTag() {
		return (streamReader.getEventType() == XMLStreamReader.START_DOCUMENT) || ((streamReader.getDepth() > startDepth ) || (streamReader.getEventType() == XMLStreamReader.START_ELEMENT));
	}
	
	public void nextTag() throws XMLStreamException {
		streamReader.nextTag();
	}
	
	public String getLocalName() {
		return streamReader.getLocalName();
	}
	
	public String getAttributeValue(String attributeName) {
		return streamReader.getAttributeValue(null, attributeName);
	}
	
	public XmlStreamReader getChildReader() {
		return new XmlStreamReader(streamReader);
	}
	
	// TODO: see if we can get rid of this method, so that we only ever get start tags
	public int getEventType() {
		return streamReader.getEventType();
	}
}
