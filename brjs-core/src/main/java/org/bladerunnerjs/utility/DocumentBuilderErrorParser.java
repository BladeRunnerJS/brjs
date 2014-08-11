package org.bladerunnerjs.utility;

import org.bladerunnerjs.logging.Logger;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class DocumentBuilderErrorParser implements ErrorHandler {
	private final Logger logger;
	
	public DocumentBuilderErrorParser(Logger logger) {
		this.logger = logger;
	}
	
	@Override
	public void warning(SAXParseException exception) throws SAXException {
		logger.warn(exception.getMessage());
	}
	
	@Override
	public void error(SAXParseException exception) throws SAXException {
		logger.error(exception.getMessage());
	}
	
	@Override
	public void fatalError(SAXParseException exception) throws SAXException {
		logger.error(exception.getMessage());
	}
}
