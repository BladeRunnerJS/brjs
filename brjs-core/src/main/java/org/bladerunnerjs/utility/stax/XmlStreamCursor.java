package org.bladerunnerjs.utility.stax;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLStreamReader2;

public class XmlStreamCursor {
	private final XMLStreamReader2 xmlStreamReader;
	private final int nodeDepth;
	
	public XmlStreamCursor(XMLStreamReader2 xmlStreamReader) throws XMLStreamException {
		if(xmlStreamReader.getEventType() == XMLStreamConstants.START_DOCUMENT) {
			xmlStreamReader.nextTag();
		}
		
		this.xmlStreamReader = xmlStreamReader;
		nodeDepth = xmlStreamReader.getDepth();
		
		xmlStreamReader.nextTag();
	}
	
	public void nextTag() throws XMLStreamException {
		xmlStreamReader.nextTag();
	}
	
	public boolean isWithinInitialNode() {
		return xmlStreamReader.getDepth() > nodeDepth;
	}
}