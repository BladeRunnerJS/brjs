package com.caplin.cutlass.bundler.xml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.bladerunnerjs.core.log.Logger;
import org.bladerunnerjs.core.log.LoggerType;
import com.caplin.cutlass.BRJSAccessor;
import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.bundler.exception.UnknownBundlerException;
import com.caplin.cutlass.bundler.io.BundlerFileReaderFactory;
import com.caplin.cutlass.bundler.xml.reader.XmlReaderWriterPipeline;
import com.caplin.cutlass.bundler.xml.reader.XmlSiblingReader;
import com.caplin.cutlass.bundler.xml.reader.XmlSiblingReaderException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.caplin.cutlass.structure.NamespaceCalculator;
import com.caplin.cutlass.structure.ScopeLevel;

public class XmlBundleWriter
{
	private static final String ALIAS_DEFINITIONS_XML = "aliasDefinitions.xml";
	private static final String ALIASES_XML = "aliases.xml";
	private Map<String, XmlResourceConfig> bundlerConfig = new HashMap<String, XmlResourceConfig>();
	private boolean outputContinuously = false;
	private Logger logger = BRJSAccessor.root.logger(LoggerType.UTIL, XmlBundleWriter.class);

	public XmlBundleWriter() throws ParserConfigurationException, SAXException, IOException
	{
		try (InputStream is = XmlBundleWriter.class.getClassLoader().getResourceAsStream("bundleConfig.xml"))
		{
			processBundlerConfig(is);
		}
	}
	
	/// Initialisation code

	private void processBundlerConfig(final InputStream configInputStream) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document bundlerConfigDoc = documentBuilder.parse(configInputStream);
		NodeList resourceNodeList = bundlerConfigDoc.getElementsByTagName("resource");

		for (int ri = 0, rl = resourceNodeList.getLength(); ri < rl; ++ri)
		{
			Element resource = (Element) resourceNodeList.item(ri);
			XmlResourceConfig resourceConfig = new XmlResourceConfig(resource.getAttribute("rootElement"), resource.getAttribute("templateElements"), resource.getAttribute("mergeElements"));

			bundlerConfig.put(resource.getAttribute("rootElement"), resourceConfig);
		}
	}

	/// Public API
	
	public void writeBundle(final List<File> documents, final Writer writer) throws XmlSiblingReaderException, XMLStreamException, IOException, BundlerProcessingException
	{
		Map<String, List<XmlSiblingReader>> resourceReaders = null;
		
		try
		{
			resourceReaders = getResourceReaders(documents);
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(writer);
			
			writeBundle(resourceReaders, xmlWriter);
		}
		finally
		{
			if (resourceReaders != null)
			{
				for(List<XmlSiblingReader> siblingReaders : resourceReaders.values())
				{
					for(XmlSiblingReader siblingReader : siblingReaders)
					{
						siblingReader.close();
					}
				}
			}
		}
	}

	public void outputContinuously()
	{
		outputContinuously = true;
	}
	
	
	/// Private helper methods

	/* returns a map of xml root elements to a list of xml sibling readers */
	private Map<String, List<XmlSiblingReader>> getResourceReaders(final List<File> documents) throws XMLStreamException, BundlerFileProcessingException, IOException
	{
		Map<String, List<XmlSiblingReader>> resourceReaders = new HashMap<String, List<XmlSiblingReader>>();
		System.setProperty("javax.xml.stream.XMLInputFactory", "com.sun.xml.stream.ZephyrParserFactory");
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		
		for (File document : documents)
		{
			String fileName = document.getName();
			if(fileName.equals(ALIASES_XML) || fileName.equals(ALIAS_DEFINITIONS_XML))
			{
				continue;
			}
			try
			{
				Reader bundlerFileReader = BundlerFileReaderFactory.getBundlerFileReader(document);
				XmlSiblingReader siblingReader = new XmlSiblingReader(inputFactory.createXMLStreamReader(bundlerFileReader));
				String packageNamespaceForResource = null;
				
				try
				{
					packageNamespaceForResource = NamespaceCalculator.getPackageNamespaceForBladeLevelResources(document);
				}
				catch (Exception ex)
				{
					throw new BundlerFileProcessingException(document, ex, "Error determining namespace for file.");
				}
				
				siblingReader.setXmlDocumentNamespace(packageNamespaceForResource);
				siblingReader.setXmlDocument(document);
				String rootElement = siblingReader.getElementName();
				
				if (!bundlerConfig.containsKey(rootElement))
				{
					logger.error("ERROR: Not bundling xml file '" + document.getAbsolutePath() + "'\n" +
								" contains unsupported rootElement '" + rootElement + "'");
					siblingReader.close();
				}
				else
				{
					if (!resourceReaders.containsKey(rootElement))
					{
						resourceReaders.put(rootElement, new ArrayList<XmlSiblingReader>());
					}
					
					List<XmlSiblingReader> readerSet = resourceReaders.get(rootElement);
					readerSet.add(siblingReader);
				}
				
			}
			catch (FileNotFoundException e)
			{
				throw new BundlerFileProcessingException(document, e, "Error bundling file.");
			}
			catch (XMLStreamException xse)
			{
				logger.error("Error reading file: " + document.getPath());
				logger.error(xse.getMessage());
			}
		}
		
		return resourceReaders;
	}
	
	private void writeBundle(final Map<String, List<XmlSiblingReader>> resourceReaders, final XMLStreamWriter writer) 
			throws XMLStreamException, XmlSiblingReaderException, BundlerProcessingException
	{
		// start document
		writer.setDefaultNamespace("http://schema.caplin.com/CaplinTrader/bundle");
		writer.writeStartDocument();
		writer.writeStartElement("bundle");
		flush(writer);

		for (Map.Entry<String, List<XmlSiblingReader>> readerSetEntry : resourceReaders.entrySet())
		{
			String resourceName = readerSetEntry.getKey();
			List<XmlSiblingReader> readerSet = readerSetEntry.getValue();

			writer.writeStartElement("resource");
			writer.writeAttribute("name", resourceName);
			flush(writer);

			writeResource(readerSet, writer, bundlerConfig.get(resourceName));
			closeResourceReaders(readerSet);

			writer.writeEndElement();
			flush(writer);
		}

		// end document
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
	}

	private void writeResource(List<XmlSiblingReader> readers, final XMLStreamWriter writer, final XmlResourceConfig resourceConfig) 
			throws XMLStreamException, XmlSiblingReaderException, BundlerProcessingException
	{
		String elementName;
		do
		{
			elementName = getNextElement(readers, resourceConfig);
			if (elementName != null)
			{
				if (resourceConfig.getMergeElements().containsKey(elementName))
				{
					mergeElements(readers, writer, resourceConfig, elementName);
				}
				else
				{
					writer.writeStartElement(elementName);

					Map<String, String> namespaceDeclarations = getNamespaceDeclarations(readers, elementName);
					
					if(!namespaceDeclarations.isEmpty())
					{
						for(String ns : namespaceDeclarations.keySet())
						{
							writer.writeNamespace(ns, namespaceDeclarations.get(ns));
						}
					}
					flush(writer);

					List<XmlSiblingReader> childReaders = getChildReaders(readers, elementName);
					writeResource(childReaders, writer, resourceConfig);
					proceedToNextSibling(childReaders);

					iteratePastTemplateElement(readers, elementName);

					writer.writeEndElement();
					flush(writer);
				}
			}
		}
		while (elementName != null);
	}
	
	private Map<String, String> getNamespaceDeclarations(List<XmlSiblingReader> readers, String elementName)
	{

		Map<String, String> namespaceDeclarations = new HashMap<String, String>();
		
		for (XmlSiblingReader reader : readers)
		{
			String srcElementName = reader.getElementName();
			if ((srcElementName != null) && (srcElementName.equals(elementName)))
			{
				for(int i = 0, l = reader.getNamespaceCount(); i < l; ++i)
				{
					namespaceDeclarations.put(reader.getNamespacePrefix(i), reader.getNamespaceURI(i));
				}
			}
		}		

		return namespaceDeclarations;
	}

	private void iteratePastTemplateElement(List<XmlSiblingReader> readers, String elementName) throws XMLStreamException, XmlSiblingReaderException
	{
		for (XmlSiblingReader reader : readers)
		{
			if (reader.getElementName().equals(elementName))
			{
				reader.nextSibling();
			}
		}
	}

	private String getNextElement(final List<XmlSiblingReader> readers, final XmlResourceConfig resourceConfig) throws XMLStreamException, BundlerProcessingException
	{
		Map<String, Boolean> availableElements = new HashMap<String, Boolean>();
		String nextElement = null;

		for (XmlSiblingReader reader : readers)
		{
			if ((reader.getElementName() != null) && (reader.hasNextSibling()))
			{
				availableElements.put(reader.getElementName(), Boolean.TRUE);
			}
		}

		if (availableElements.size() > 0)
		{
			// if a merge element is available, then process that
			Map<String, Boolean> mergeElements = resourceConfig.getMergeElements();
			for (String availableElement : availableElements.keySet())
			{
				if (mergeElements.containsKey(availableElement))
				{
					nextElement = availableElement;
					break;
				}
			}

			// otherwise, this must be a template element
			if (nextElement == null)
			{
				// try matching each of the template elements, one at time, until we find the first one that matches what one of the readers currently has
				for (String templateElement : resourceConfig.getTemplateElements())
				{
					if (availableElements.containsKey(templateElement))
					{
						nextElement = templateElement;
						break;
					}
				}
			}

			if (nextElement == null)
			{
				String errorMessage = "None of the available elements (" + StringUtils.join(availableElements.keySet(),
					", ") + ") matched any of the expected elements (" + StringUtils.join(resourceConfig.getTemplateElements(), ", ") + ")";
				throw new BundlerProcessingException(errorMessage);
			}
		}

		return nextElement;
	}

	private void mergeElements(final List<XmlSiblingReader> readers, XMLStreamWriter writer, final XmlResourceConfig resourceConfig, final String elementName) 
			throws XMLStreamException, XmlSiblingReaderException, BundlerFileProcessingException
	{
		Map<String, File> processedIdentifers = new HashMap<String, File>();
		String identifierAttribute = resourceConfig.getMergeElementIdentifier(elementName);

		for (XmlSiblingReader reader : readers)
		{
			try
			{
				while (processXMLElement(writer, processedIdentifers, identifierAttribute, reader));
			}
			catch (XMLStreamException e)
			{
				throw new UnknownBundlerException(e);
			}
			catch (IllegalStateException e)
			{
				throw new UnknownBundlerException(e);
			}
		}
	}

	private boolean processXMLElement(XMLStreamWriter writer, Map<String, File> processedIdentifers,	String identifierAttribute, XmlSiblingReader reader) 
			throws XMLStreamException, XmlSiblingReaderException, BundlerFileProcessingException 
	{
		String identifier = getIdentifier(identifierAttribute, reader);
		
		if(identifier != null)
		{
			File fileWithTheSameIdForTheMergeElement = processedIdentifers.get(identifier);
			if(fileWithTheSameIdForTheMergeElement != null )
			{
				if(checkFilesAreInTheSameBladeset(fileWithTheSameIdForTheMergeElement, reader.getXmlDocument()))
				{
					throw new BundlerFileProcessingException(reader.getXmlDocument(), " duplicate identifier '" + 
						identifier + "', first seen in the file:\n" + fileWithTheSameIdForTheMergeElement.getAbsolutePath());
				}
				else
				{
					return reader.skipToNextSibling();
				}
			}
			processedIdentifers.put(identifier, reader.getXmlDocument());
		}
		
		XmlReaderWriterPipeline.cloneElement(reader, writer, outputContinuously);
		return reader.nextSibling();
	}

	private boolean checkFilesAreInTheSameBladeset(File file1, File file2)
	{
		ScopeLevel file1Scope = CutlassDirectoryLocator.getScope(file1);
		ScopeLevel file2Scope = CutlassDirectoryLocator.getScope(file2);
		
		if((file1Scope == ScopeLevel.BLADE_SCOPE || file1Scope == ScopeLevel.BLADESET_SCOPE)
			&& (file2Scope == ScopeLevel.BLADE_SCOPE || file2Scope == ScopeLevel.BLADESET_SCOPE))
		{
			File file1Bladeset = CutlassDirectoryLocator.getParentBladeset(file1);
			File file2Bladeset = CutlassDirectoryLocator.getParentBladeset(file2);
			
			if(file1Bladeset.equals(file2Bladeset))
			{
				return true;
			}
		}
		return false;
	}

	/* Throws an exception if the identifer is not in the default XML namespace configured for this reader. */
	private String getIdentifier(String identifierAttribute, XmlSiblingReader reader) throws BundlerFileProcessingException
	{
		String namespace = reader.getXmlDocumentNamespace();
		String identifier = reader.getAttributeValue(identifierAttribute);
		
		if(identifier != null && namespace.length() > 0 )
		{
			if(identifier.startsWith(namespace) == false)
			{
				Location location = reader.getLocation();
				throw new BundlerFileProcessingException(reader.getXmlDocument(), location.getLineNumber(), location.getColumnNumber(),
					"The identifier '" + identifier + "' is not correctly namespaced.\nNamespace '" + namespace + "*' was expected.");
			}
		}
		
		return identifier;
	}

	private void proceedToNextSibling(final List<XmlSiblingReader> readers) throws XMLStreamException, XmlSiblingReaderException
	{
		for (XmlSiblingReader reader : readers)
		{
			reader.nextSibling();
		}
	}

	private List<XmlSiblingReader> getChildReaders(final List<XmlSiblingReader> readers, final String elementName) throws XMLStreamException, XmlSiblingReaderException
	{
		List<XmlSiblingReader> childReaders = new ArrayList<XmlSiblingReader>();

		for (XmlSiblingReader reader : readers)
		{
			if (reader.getElementName().equals(elementName))
			{
				XmlSiblingReader childReader = reader.getChildReader();

				if (childReader != null)
				{
					childReaders.add(childReader);
				}
			}
		}

		return childReaders;
	}

	private void closeResourceReaders(final List<XmlSiblingReader> readers) throws XMLStreamException
	{
		for (XmlSiblingReader reader : readers)
		{
			reader.close();
		}
	}

	/**
	 * This method is only here to make debugging easier, and it's use is
	 * enabled within the unit tests
	 * 
	 * @throws XMLStreamException
	 */
	private void flush(XMLStreamWriter writer) throws XMLStreamException
	{
		if (outputContinuously)
		{
			writer.flush();
		}
	}
}
