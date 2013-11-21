package org.bladerunnerjs.model.utility;

import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;

import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

import org.codehaus.stax2.AttributeInfo;
import org.codehaus.stax2.DTDInfo;
import org.codehaus.stax2.LocationInfo;
import org.codehaus.stax2.XMLStreamReader2;
import org.codehaus.stax2.typed.Base64Variant;
import org.codehaus.stax2.typed.TypedArrayDecoder;
import org.codehaus.stax2.typed.TypedValueDecoder;
import org.codehaus.stax2.validation.ValidationProblemHandler;
import org.codehaus.stax2.validation.XMLValidationSchema;
import org.codehaus.stax2.validation.XMLValidator;

public class XmlStreamReader implements XMLStreamReader2, AutoCloseable {
	private final XMLStreamReader2 streamReader;
	private FileReader fileReader;
	
	public XmlStreamReader(XMLStreamReader2 streamReader, FileReader fileReader) {
		this.streamReader = streamReader;
		this.fileReader = fileReader;
	}
	
	@Override
	public void close() throws XMLStreamException {
		streamReader.close();
		
		try {
			fileReader.close();
		}
		catch (IOException e) {
			throw new XMLStreamException(e);
		}
	}
	
	@Override
	public AttributeInfo getAttributeInfo() throws XMLStreamException {
		return streamReader.getAttributeInfo();
	}
	
	@Override
	public void closeCompletely() throws XMLStreamException {
		streamReader.closeCompletely();
	}
	
	@Override
	public int getAttributeIndex(String namespaceURI, String localName) {
		return streamReader.getAttributeIndex(namespaceURI, localName);
	}
	
	@Override
	public boolean getAttributeAsBoolean(int index) throws XMLStreamException {
		return streamReader.getAttributeAsBoolean(index);
	}
	
	@Override
	public int getAttributeAsInt(int index) throws XMLStreamException {
		return streamReader.getAttributeAsInt(index);
	}
	
	@Override
	public long getAttributeAsLong(int index) throws XMLStreamException {
		return streamReader.getAttributeAsLong(index);
	}
	
	@Override
	public float getAttributeAsFloat(int index) throws XMLStreamException {
		return streamReader.getAttributeAsFloat(index);
	}
	
	@Override
	public double getAttributeAsDouble(int index) throws XMLStreamException {
		return streamReader.getAttributeAsDouble(index);
	}

	@Override
	public BigInteger getAttributeAsInteger(int index) throws XMLStreamException {
		return streamReader.getAttributeAsInteger(index);
	}
	
	public BigDecimal getAttributeAsDecimal(int index) throws XMLStreamException {
		return streamReader.getAttributeAsDecimal(index);
	}

	@Override
	public QName getAttributeAsQName(int index) throws XMLStreamException {
		return streamReader.getAttributeAsQName(index);
	}

	@Override
	public void getAttributeAs(int index, TypedValueDecoder tvd) throws XMLStreamException {
		streamReader.getAttributeAs(index, tvd);
	}

	@Override
	public byte[] getAttributeAsBinary(int index) throws XMLStreamException {
		return streamReader.getAttributeAsBinary(index);
	}

	@Override
	public byte[] getAttributeAsBinary(int index, Base64Variant v) throws XMLStreamException {
		return streamReader.getAttributeAsBinary(index, v);
	}

	@Override
	public int[] getAttributeAsIntArray(int index) throws XMLStreamException {
		return streamReader.getAttributeAsIntArray(index);
	}

	@Override
	public long[] getAttributeAsLongArray(int index) throws XMLStreamException {
		return streamReader.getAttributeAsLongArray(index);
	}

	@Override
	public float[] getAttributeAsFloatArray(int index) throws XMLStreamException {
		return streamReader.getAttributeAsFloatArray(index);
	}

	@Override
	public double[] getAttributeAsDoubleArray(int index) throws XMLStreamException {
		return streamReader.getAttributeAsDoubleArray(index);
	}

	@Override
	public int getAttributeAsArray(int index, TypedArrayDecoder tad) throws XMLStreamException {
		return streamReader.getAttributeAsArray(index, tad);
	}

	@Override
	public int getAttributeCount() {
		return streamReader.getAttributeCount();
	}

	@Override
	public String getAttributeLocalName(int index) {
		return streamReader.getAttributeLocalName(index);
	}

	@Override
	public QName getAttributeName(int index) {
		return streamReader.getAttributeName(index);
	}

	@Override
	public String getAttributeNamespace(int index) {
		return streamReader.getAttributeNamespace(index);
	}

	@Override
	public String getAttributePrefix(int index) {
		return streamReader.getAttributePrefix(index);
	}

	@Override
	public String getAttributeType(int index) {
		return streamReader.getAttributeType(index);
	}

	@Override
	public String getAttributeValue(int index) {
		return streamReader.getAttributeValue(index);
	}

	@Override
	public String getAttributeValue(String namespaceURI, String localName) {
		return streamReader.getAttributeValue(namespaceURI, localName);
	}

	@Override
	public String getCharacterEncodingScheme() {
		return streamReader.getCharacterEncodingScheme();
	}

	@Override
	public boolean getElementAsBoolean() throws XMLStreamException {
		return streamReader.getElementAsBoolean();
	}

	@Override
	public int getElementAsInt() throws XMLStreamException {
		return streamReader.getElementAsInt();
	}

	@Override
	public long getElementAsLong() throws XMLStreamException {
		return streamReader.getElementAsLong();
	}

	@Override
	@Deprecated
	public Object getFeature(String name) {
		return streamReader.getFeature(name);
	}

	@Override
	public float getElementAsFloat() throws XMLStreamException {
		return streamReader.getElementAsFloat();
	}

	@Override
	public DTDInfo getDTDInfo() throws XMLStreamException {
		return streamReader.getDTDInfo();
	}

	@Override
	public double getElementAsDouble() throws XMLStreamException {
		return streamReader.getElementAsDouble();
	}

	@Override
	public LocationInfo getLocationInfo() {
		return streamReader.getLocationInfo();
	}

	@Override
	public BigInteger getElementAsInteger() throws XMLStreamException {
		return streamReader.getElementAsInteger();
	}

	@Override
	public BigDecimal getElementAsDecimal() throws XMLStreamException {
		return streamReader.getElementAsDecimal();
	}

	@Override
	public QName getElementAsQName() throws XMLStreamException {
		return streamReader.getElementAsQName();
	}

	@Override
	public byte[] getElementAsBinary() throws XMLStreamException {
		return streamReader.getElementAsBinary();
	}

	@Override
	public byte[] getElementAsBinary(Base64Variant variant) throws XMLStreamException {
		return streamReader.getElementAsBinary(variant);
	}

	@Override
	public void getElementAs(TypedValueDecoder tvd) throws XMLStreamException {
		streamReader.getElementAs(tvd);
	}

	@Override
	public int getDepth() {
		return streamReader.getDepth();
	}

	@Override
	public String getElementText() throws XMLStreamException {
		return streamReader.getElementText();
	}

	@Override
	public String getEncoding() {
		return streamReader.getEncoding();
	}

	@Override
	public int getEventType() {
		return streamReader.getEventType();
	}

	@Override
	public String getLocalName() {
		return streamReader.getLocalName();
	}

	@Override
	public Location getLocation() {
		return streamReader.getLocation();
	}

	@Override
	public QName getName() {
		return streamReader.getName();
	}

	@Override
	public NamespaceContext getNamespaceContext() {
		return streamReader.getNamespaceContext();
	}

	@Override
	public int getNamespaceCount() {
		return streamReader.getNamespaceCount();
	}

	@Override
	public String getNamespacePrefix(int index) {
		return streamReader.getNamespacePrefix(index);
	}

	@Override
	public String getNamespaceURI() {
		return streamReader.getNamespaceURI();
	}

	@Override
	public String getNamespaceURI(int index) {
		return streamReader.getNamespaceURI(index);
	}

	@Override
	public String getNamespaceURI(String prefix) {
		return streamReader.getNamespaceURI(prefix);
	}

	@Override
	public NamespaceContext getNonTransientNamespaceContext() {
		return streamReader.getNonTransientNamespaceContext();
	}

	@Override
	public String getPIData() {
		return streamReader.getPIData();
	}

	@Override
	public String getPITarget() {
		return streamReader.getPITarget();
	}

	@Override
	public String getPrefix() {
		return streamReader.getPrefix();
	}

	@Override
	public String getPrefixedName() {
		return streamReader.getPrefixedName();
	}

	@Override
	public Object getProperty(String name) throws IllegalArgumentException {
		return streamReader.getProperty(name);
	}

	@Override
	public String getText() {
		return streamReader.getText();
	}

	@Override
	public int getText(Writer w, boolean preserveContents) throws IOException, XMLStreamException {
		return streamReader.getText(w, preserveContents);
	}

	@Override
	public char[] getTextCharacters() {
		return streamReader.getTextCharacters();
	}

	@Override
	public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
		return streamReader.getTextCharacters(sourceStart, target, targetStart, length);
	}

	@Override
	public int getTextLength() {
		return streamReader.getTextLength();
	}

	@Override
	public int getTextStart() {
		return streamReader.getTextStart();
	}

	@Override
	public String getVersion() {
		return streamReader.getVersion();
	}

	@Override
	public boolean hasName() {
		return streamReader.hasName();
	}

	@Override
	public boolean hasNext() throws XMLStreamException {
		return streamReader.hasNext();
	}

	@Override
	public boolean hasText() {
		return streamReader.hasText();
	}

	@Override
	public boolean isAttributeSpecified(int index) {
		return streamReader.isAttributeSpecified(index);
	}

	@Override
	public boolean isCharacters() {
		return streamReader.isCharacters();
	}

	@Override
	public boolean isEmptyElement() throws XMLStreamException {
		return streamReader.isEmptyElement();
	}

	@Override
	public boolean isEndElement() {
		return streamReader.isEndElement();
	}

	@Override
	public boolean isPropertySupported(String name) {
		return streamReader.isPropertySupported(name);
	}

	@Override
	public boolean isStandalone() {
		return streamReader.isStandalone();
	}

	@Override
	public boolean isStartElement() {
		return streamReader.isStartElement();
	}

	@Override
	public boolean isWhiteSpace() {
		return streamReader.isWhiteSpace();
	}

	@Override
	public int next() throws XMLStreamException {
		return streamReader.next();
	}

	@Override
	public int nextTag() throws XMLStreamException {
		return streamReader.nextTag();
	}

	@Override
	public XMLValidator validateAgainst(XMLValidationSchema schema) throws XMLStreamException {
		return streamReader.validateAgainst(schema);
	}

	@Override
	public XMLValidator stopValidatingAgainst(XMLValidationSchema schema) throws XMLStreamException {
		return streamReader.stopValidatingAgainst(schema);
	}

	@Override
	public XMLValidator stopValidatingAgainst(XMLValidator validator) throws XMLStreamException {
		return streamReader.stopValidatingAgainst(validator);
	}

	@Override
	public ValidationProblemHandler setValidationProblemHandler(ValidationProblemHandler h) {
		return streamReader.setValidationProblemHandler(h);
	}

	@Override
	public boolean setProperty(String name, Object value) {
		return streamReader.setProperty(name, value);
	}

	@Override
	@Deprecated
	public void setFeature(String name, Object value) {
		streamReader.setFeature(name, value);
	}

	@Override
	public void skipElement() throws XMLStreamException {
		streamReader.skipElement();
	}

	@Override
	public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength, Base64Variant variant) throws XMLStreamException {
		return streamReader.readElementAsBinary(resultBuffer, offset, maxLength, variant);
	}

	@Override
	public int readElementAsBinary(byte[] resultBuffer, int offset, int maxLength) throws XMLStreamException {
		return streamReader.readElementAsBinary(resultBuffer, offset, maxLength);
	}

	@Override
	public int readElementAsIntArray(int[] resultBuffer, int offset, int length) throws XMLStreamException {
		return streamReader.readElementAsIntArray(resultBuffer, offset, length);
	}

	@Override
	public int readElementAsLongArray(long[] resultBuffer, int offset, int length) throws XMLStreamException {
		return streamReader.readElementAsLongArray(resultBuffer, offset, length);
	}

	@Override
	public int readElementAsFloatArray(float[] resultBuffer, int offset, int length) throws XMLStreamException {
		return streamReader.readElementAsFloatArray(resultBuffer, offset, length);
	}

	@Override
	public int readElementAsDoubleArray(double[] resultBuffer, int offset, int length) throws XMLStreamException {
		return streamReader.readElementAsDoubleArray(resultBuffer, offset, length);
	}

	@Override
	public int readElementAsArray(TypedArrayDecoder tad) throws XMLStreamException {
		return streamReader.readElementAsArray(tad);
	}

	@Override
	public void require(int type, String namespaceURI, String localName) throws XMLStreamException {
		streamReader.require(type, namespaceURI, localName);
	}

	@Override
	public boolean standaloneSet() {
		return streamReader.standaloneSet();
	}
}
