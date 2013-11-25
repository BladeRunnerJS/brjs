package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import javax.xml.stream.XMLStreamException;

import org.bladerunnerjs.model.utility.stax.XmlStreamReader;
import org.codehaus.stax2.XMLInputFactory2;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.validation.XMLValidationSchema;

import com.ctc.wstx.stax.WstxInputFactory;

public class XmlStreamReaderFactory {
	private static final XMLInputFactory2 inputFactory = new WstxInputFactory();
	
	public static XmlStreamReader createReader(File xmlFile, XMLValidationSchema xmlSchema) throws FileNotFoundException, XMLStreamException {
		FileReader fileReader = new FileReader(xmlFile);
		XMLStreamReader2 streamReader = (XMLStreamReader2) inputFactory.createXMLStreamReader(fileReader);
		
		streamReader.validateAgainst(xmlSchema);
		
		return new XmlStreamReader(streamReader, fileReader);
	}
}
