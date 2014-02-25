package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;
import java.io.Reader;

import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.utility.stax.XmlStreamReader;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;

import com.ctc.wstx.stax.WstxInputFactory;

public class XmlStreamReaderFactory {
	private static final XMLInputFactory2 inputFactory = new WstxInputFactory();
	
	public static XmlStreamReader createReader(File xmlFile, String defaultFileCharacterEncoding, XMLValidationSchema xmlSchema) throws IOException, XMLStreamException {
		Reader fileReader = new UnicodeReader(xmlFile, defaultFileCharacterEncoding);
		XMLStreamReader2 streamReader = (XMLStreamReader2) inputFactory.createXMLStreamReader(fileReader);
		
		streamReader.validateAgainst(xmlSchema);
		
		return new XmlStreamReader(streamReader, fileReader);
	}
}
