package org.bladerunnerjs.utility.stax;

import static org.junit.Assert.*;

import java.io.StringReader;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.junit.Test;

import com.ctc.wstx.stax.WstxInputFactory;

public class XmlStreamCursorTest {
	private static final XMLInputFactory2 inputFactory = new WstxInputFactory();
	
	@Test
	public void singleRootNode() throws Exception {
		assertEquals("r:1", parseXml("<r a='1'/>"));
	}
	
	@Test
	public void singleChildNode() throws Exception {
		assertEquals("r:1, c:2", parseXml("<r a='1'><c a='2'/></r>"));
	}
	
	@Test
	public void multipleChildNodes() throws Exception {
		assertEquals("r:1, c:2, c:3, c:4", parseXml("<r a='1'><c a='2'/><c a='3'/><c a='4'/></r>"));
	}
	
	@Test
	public void singleGrandChildNode() throws Exception {
		assertEquals("r:1, c:2, gc:3", parseXml("<r a='1'><c a='2'><gc a='3'/></c></r>"));
	}
	
	@Test
	public void multipleGrandChildNodes() throws Exception {
		assertEquals("r:1, c:2, gc:3, gc:4, gc:5", parseXml("<r a='1'><c a='2'><gc a='3'/><gc a='4'/><gc a='5'/></c></r>"));
	}
	
	@Test
	public void childNodeAfterGrandChildNode() throws Exception {
		assertEquals("r:1, c:2, gc:3, c:4", parseXml("<r a='1'><c a='2'><gc a='3'/></c><c a='4'/></r>"));
	}
	
	@Test
	public void complexContent() throws Exception {
		assertEquals("r:1, c:2, gc:3, c:4, gc:5", parseXml("<r a='1'><c a='2'><gc a='3'/></c><c a='4'><gc a='5'/></c></r>"));
	}
	
	private String parseXml(String xml) throws XMLStreamException {
		StringBuffer stringBuffer = new StringBuffer();
		processDocument((XMLStreamReader2) inputFactory.createXMLStreamReader(new StringReader(xml)), stringBuffer);
		
		return stringBuffer.toString();
	}
	
	private void processDocument(XMLStreamReader2 xmlStreamReader, StringBuffer stringBuffer) throws XMLStreamException {
		xmlStreamReader.nextTag();
		processRootNode(xmlStreamReader, stringBuffer);
	}
	
	private void processRootNode(XMLStreamReader2 reader, StringBuffer stringBuffer) throws XMLStreamException {
		stringBuffer.append("r:" + reader.getAttributeValue(null, "a"));
		
		XmlStreamCursor cursor = new XmlStreamCursor(reader);
		while(cursor.isWithinInitialNode()) {
			if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(reader.getLocalName()) {
					case "c":
						processChildNode(reader, stringBuffer);
						break;
					
					default:
						throw new RuntimeException("should never get to this line");
				}
			}
			
			cursor.nextTag();
		}
	}
	
	private void processChildNode(XMLStreamReader2 reader, StringBuffer stringBuffer) throws XMLStreamException {
		stringBuffer.append(", c:" + reader.getAttributeValue(null, "a"));
		
		XmlStreamCursor cursor = new XmlStreamCursor(reader);
		while(cursor.isWithinInitialNode()) {
			if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {
				switch(reader.getLocalName()) {
					case "gc":
						processGrandChildNode(reader, stringBuffer);
						break;
					
					default:
						throw new RuntimeException("should never get to this line");
				}
			}
			
			cursor.nextTag();
		}
	}
	
	private void processGrandChildNode(XMLStreamReader2 reader, StringBuffer stringBuffer) throws XMLStreamException {
		stringBuffer.append(", gc:" + reader.getAttributeValue(null, "a"));
		
		XmlStreamCursor cursor = new XmlStreamCursor(reader);
		while(cursor.isWithinInitialNode()) {
			if(reader.getEventType() == XMLStreamReader.START_ELEMENT) {
				throw new RuntimeException("should never get to this line");
			}
			
			cursor.nextTag();
		}
	}
}
