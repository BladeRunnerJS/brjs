package org.bladerunnerjs.utility;

import java.io.Reader;

import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;

import com.ctc.wstx.stax.WstxInputFactory;

public class XmlStreamReaderFactory {
	private static final XMLInputFactory2 inputFactory = new WstxInputFactory();
	
	public static XMLStreamReader2 createReader(Reader fileReader, XMLValidationSchema xmlSchema) throws XMLStreamException {
		XMLStreamReader2 streamReader = (XMLStreamReader2) inputFactory.createXMLStreamReader(fileReader);
		streamReader.validateAgainst(xmlSchema);
		
		return streamReader;
	}
}
