package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


//TODO check how the DocumentBuilder handles file encoding and UTF8/BOM issues
public class XmlBundlerConfig {

	public static final String CONFIG_FILE_NAME = "bundleConfig.xml";
	private Map<String, XmlResourceConfig> configMap = null;
	private BRJS brjs;

	public XmlBundlerConfig(BRJS brjs) {
		this.brjs = brjs;
	}
	
	
	public boolean isbundleConigAvailable(){
		File file = brjs.configuration(CONFIG_FILE_NAME);
		return file.exists();
	}

	public Map<String, XmlResourceConfig> getConfigMap() throws ContentProcessingException {
		if(configMap == null){
			configMap = createConfigMap();
		}
		return configMap;
	}

	private Map<String, XmlResourceConfig>  createConfigMap() throws ContentProcessingException
	{
		
		File file = brjs.configuration(CONFIG_FILE_NAME);
		Map<String, XmlResourceConfig> result = new HashMap<String, XmlResourceConfig>();
		try 
		{
			InputStream is = new FileInputStream(file);
			result = processBundlerConfig(is);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			throw new ContentProcessingException(e);
		}
		return result;
	}

	private Map<String, XmlResourceConfig> processBundlerConfig(
			final InputStream configInputStream)
			throws ParserConfigurationException, SAXException, IOException {
		
		Map<String, XmlResourceConfig> result = new HashMap<String, XmlResourceConfig>();
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		Document bundlerConfigDoc = documentBuilder.parse(configInputStream);
		NodeList resourceNodeList = bundlerConfigDoc.getElementsByTagName("resource");

		for (int ri = 0, rl = resourceNodeList.getLength(); ri < rl; ++ri) {
			Element resource = (Element) resourceNodeList.item(ri);
			XmlResourceConfig resourceConfig = new XmlResourceConfig(
					resource.getAttribute("rootElement"),
					resource.getAttribute("templateElements"),
					resource.getAttribute("mergeElements"));

			result.put(resource.getAttribute("rootElement"), resourceConfig);
		}
		return result;
	}
}
