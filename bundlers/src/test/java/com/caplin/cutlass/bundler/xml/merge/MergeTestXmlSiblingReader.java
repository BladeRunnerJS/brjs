package com.caplin.cutlass.bundler.xml.merge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.junit.Test;

import com.caplin.cutlass.bundler.xml.reader.XmlReaderWriterPipeline;
import com.caplin.cutlass.bundler.xml.reader.XmlSiblingReader;
import com.caplin.cutlass.bundler.xml.utils.TeeOutputStream;

public class MergeTestXmlSiblingReader extends MergeTestRunnerTest
{
	private XMLInputFactory inputFactory = XMLInputFactory.newInstance();
	private XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
	
	public MergeTestXmlSiblingReader()
	{
		super(null);
	}
	
	@Test
	public void simpleSiblingReaderTest() throws Exception
	{
		testXml("simpleTest.xml");
	}
	
	@Test
	public void namespaceReaderTest() throws Exception
	{
		testXml("namespaceTest.xml");
	}
	
	private void testXml(String xmlName) throws Exception
	{
		File testXml = new File("src/test/resources/xml-bundler/reader/" + xmlName);
		ByteArrayOutputStream generatedXml = new ByteArrayOutputStream();
		XMLStreamReader xmlStreamReader = inputFactory.createXMLStreamReader(new FileReader(testXml));
		XMLStreamWriter writer = outputFactory.createXMLStreamWriter(new TeeOutputStream(generatedXml, new ByteArrayOutputStream()));
		XmlSiblingReader xmlSiblingReader = new XmlSiblingReader(xmlStreamReader);
		
		writer.writeStartDocument();

		XmlReaderWriterPipeline.cloneElements(xmlSiblingReader, writer, true);
		writer.writeEndDocument();
		writer.flush();
		
		compareXmls(testXml, generatedXml);
	}
}
