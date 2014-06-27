package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.aliasing.NamespaceException;
import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class XmlBundleWriter
{
	private boolean outputContinuously = false;
	private XmlBundlerConfig xmlBundlerConfig = null;

	public XmlBundleWriter(XmlBundlerConfig xmlBundlerConfig) 
	{
		this.xmlBundlerConfig  = xmlBundlerConfig;
	}
	
	public void concatenateBundle(List<Asset> xmlAssets, final Writer writer) throws ContentProcessingException  {
		
		try {
			writer.write("<bundle>\n");
			for(Asset asset : xmlAssets){
				try (Reader reader = asset.getReader()) { IOUtils.copy(reader, writer); }
			}
			writer.write("</bundle>");
		} catch (IOException e) {
			throw new ContentProcessingException( e);
			
		}
	};
	
	public void writeBundle(List<Asset> xmlAssets, final Writer writer) throws ContentProcessingException, XMLStreamException {
		
		Map<String, List<XmlSiblingReader>> resourceReaders = null;
		
		try
		{
			resourceReaders = getResourceReaders(xmlAssets);
			XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xmlWriter = outputFactory.createXMLStreamWriter(writer);
			writeBundleInternal(resourceReaders, xmlWriter);
		}
		catch (XmlSiblingReaderException | XMLStreamException e) {
			throw new ContentProcessingException(e, "Error while bundling XML assets '" );
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
	
	private void writeBundleInternal(final Map<String, List<XmlSiblingReader>> resourceReaders, final XMLStreamWriter writer) 
			throws XMLStreamException, XmlSiblingReaderException, ContentProcessingException
	{
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

			Map<String, XmlResourceConfig> configMap = xmlBundlerConfig.getConfigMap();
			writeResource(readerSet, writer, configMap.get(resourceName));
			closeResourceReaders(readerSet);

			writer.writeEndElement();
			flush(writer);
		}

		// end document
		writer.writeEndElement();
		writer.writeEndDocument();
		writer.close();
	}


	
	/* returns a map of xml root elements to a list of xml sibling readers */
	private Map<String, List<XmlSiblingReader>> getResourceReaders(List<Asset> xmlAssets) throws  ContentProcessingException
	{
		Map<String, List<XmlSiblingReader>> resourceReaders = new HashMap<String, List<XmlSiblingReader>>();
		System.setProperty("javax.xml.stream.XMLInputFactory", "com.sun.xml.stream.ZephyrParserFactory");
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		
		for (Asset xmlAsset : xmlAssets)
		{
			String assetName = xmlAsset.getAssetName();
			if(assetName.equals("aliasDefinitions.xml") || assetName.equals("aliases.xml")  ){
				continue;
			}
			
			File document = new File(xmlAsset.dir(), xmlAsset.getAssetName());
			try{
				Reader bundlerFileReader = xmlAsset.getReader();
				XmlSiblingReader siblingReader = new XmlSiblingReader(inputFactory.createXMLStreamReader(bundlerFileReader));
				siblingReader.setAsset(xmlAsset);
				
				siblingReader.setXmlDocument(document);
				String rootElement = siblingReader.getElementName();
				
				Map<String, XmlResourceConfig> configMap = xmlBundlerConfig.getConfigMap();
				if (!configMap.containsKey(rootElement))
				{
					siblingReader.close();
					throw new ContentFileProcessingException(document, "Document contain unsupported root element: '" + rootElement + "'");
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
				
			}catch (  XMLStreamException | IOException e){
				throw new ContentFileProcessingException(document, e);
			}
		}
		
		return resourceReaders;
	}
	
	
	private void writeResource(List<XmlSiblingReader> readers, final XMLStreamWriter writer, final XmlResourceConfig resourceConfig) 
			throws XMLStreamException, XmlSiblingReaderException, ContentProcessingException
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

	private String getNextElement(final List<XmlSiblingReader> readers, final XmlResourceConfig resourceConfig) throws XMLStreamException, ContentProcessingException
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
				String errorMessage = "None of the available elements '" + StringUtils.join(availableElements.keySet(),
					", ") + "' matched any of the expected elements '" + StringUtils.join(resourceConfig.getTemplateElements(), ", ") + "'";
				throw new ContentProcessingException(errorMessage);
			}
		}

		return nextElement;
	}

	private void mergeElements(final List<XmlSiblingReader> readers, XMLStreamWriter writer, final XmlResourceConfig resourceConfig, final String elementName) 
			throws  ContentProcessingException
	{
		Map<String, File> processedIdentifers = new HashMap<String, File>();
		String identifierAttribute = resourceConfig.getMergeElementIdentifier(elementName);

		for (XmlSiblingReader reader : readers)
		{
			try
			{
				while (processXMLElement(writer, processedIdentifers, identifierAttribute, reader));
			}
			catch (Exception e)
			{
				throw new ContentProcessingException(e, "Error while bundling asset ");
			}
		}
	}

	private boolean processXMLElement(XMLStreamWriter writer, Map<String, File> processedIdentifers,	String identifierAttribute, XmlSiblingReader reader) 
			throws XMLStreamException, XmlSiblingReaderException, ContentProcessingException 
	{
		String identifier;
		try {
			identifier = getIdentifier(identifierAttribute, reader);
		} catch (NamespaceException e) {
			throw new ContentFileProcessingException(reader.getXmlDocument(), e);
		}
			
		if(identifier != null)
		{
			File fileWithTheSameIdForTheMergeElement = processedIdentifers.get(identifier);
			if(fileWithTheSameIdForTheMergeElement != null )
			{
				return reader.skipToNextSibling();
			}
			processedIdentifers.put(identifier, reader.getXmlDocument());
		}
		
		XmlReaderWriterPipeline.cloneElement(reader, writer, outputContinuously);
		return reader.nextSibling();
	}



	/* Throws an exception if the identifer is not in the default XML namespace configured for this reader. */
	private String getIdentifier(String identifierAttribute, XmlSiblingReader reader) throws NamespaceException
	{
		String identifier = reader.getAttributeValue(identifierAttribute);
		
		try {
			reader.assertIdentifierCorrectlyNamespaced(identifier);
		}
		catch(RequirePathException e) {
			throw new NamespaceException("Require path exception while attempting to validate correctly namespaced identifier", e);
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

	private List<XmlSiblingReader> getChildReaders(final List<XmlSiblingReader> readers, final String elementName) throws ContentFileProcessingException
	{
		List<XmlSiblingReader> childReaders = new ArrayList<XmlSiblingReader>();

		for (XmlSiblingReader reader : readers)
		{
			try{
				
				if (reader.getElementName().equals(elementName))
				{
					XmlSiblingReader childReader = reader.getChildReader();

					if (childReader != null)
					{
						childReaders.add(childReader);
					}
				}
			}catch(XMLStreamException | XmlSiblingReaderException e){
				throw new ContentFileProcessingException(reader.getXmlDocument(), e);
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
	
	public void outputContinuously()
	{
		outputContinuously = true;
	}
}
