package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class WebXmlCompiler {
	public static void compile(File webXmlFile) throws IOException, ParseException {
		FileWriter webXmlFileWriter = null;
		Boolean withinDevBlock = false;
		
		try {
			DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document webXml = documentBuilder.parse(webXmlFile);
			
			processWebXmlNode(webXml.getDocumentElement().getChildNodes(), withinDevBlock);
			
			StringWriter buffer = new StringWriter();
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			webXmlFileWriter = new FileWriter(webXmlFile, false);
			transformer.transform(new DOMSource(webXml.getDocumentElement()), new StreamResult(buffer));
			webXmlFileWriter.write(buffer.toString());
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
		finally {
			if (webXmlFileWriter != null) {
				webXmlFileWriter.close();
			}
		}
	}
	
	private static void processWebXmlNode(NodeList childNodes, Boolean withinDevBlock) throws ParseException, ParserConfigurationException, SAXException, IOException {
		for (int i = 0; i < childNodes.getLength(); ++i) {
			Node node = childNodes.item(i);
			
			if (node.getNodeType() == Node.COMMENT_NODE) {
				String commentText = node.getTextContent();
				
				if (commentText.matches("(?s).*\\s*start-env:\\s*dev.*")) {
					withinDevBlock = true;
					
					node.getParentNode().removeChild(node);
					i--;
				}
				else if (commentText.matches("(?s).*\\s*start-env:\\s*prod.*")) {
					DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					String prodXmlText = commentText.replaceAll("^.*start-env:\\s*prod\\s*", "").replaceAll("\\s*end-env\\s*", "");
					Document prodXml = documentBuilder.parse(new InputSource(new StringReader("<root>" + prodXmlText + "</root>")));
					NodeList prodChildNodes = prodXml.getDocumentElement().getChildNodes();
					
					processWebXmlNode(prodChildNodes, withinDevBlock);
					importNodes(node.getParentNode(), node, prodXml.getDocumentElement());
					node.getParentNode().removeChild(node);
					
					i += (prodChildNodes.getLength() - 1);
				}
				else if (commentText.matches("(?s).*end-env.*")) {
					withinDevBlock = false;
					
					node.getParentNode().removeChild(node);
					i--;
				}
			} else if (node.getNodeType() == Node.ELEMENT_NODE) {
				if (withinDevBlock) {
					node.getParentNode().removeChild(node);
					i--;
				}
				else {
					if (node.getNodeName().equals("env-entry-value") && (node.getFirstChild() != null)
						&& node.getFirstChild().getNodeValue().equals("%@APP.VERSION@%")) {
						Timestamp timestamp = new Timestamp(System.currentTimeMillis());
						node.getFirstChild().setNodeValue("v_" + timestamp.getTime());
					} else if (node.hasChildNodes()) {
						processWebXmlNode(node.getChildNodes(), withinDevBlock);
					}
				}
			}
			else if (node.getNodeType() == Node.TEXT_NODE) {
				if (withinDevBlock) {
					node.getParentNode().removeChild(node);
					i--;
				}
			}
		}
	}
	
	private static void importNodes(Node node, Node refChild, Element documentElement) {
		Node importedNode = node.getOwnerDocument().importNode(documentElement, true);
		
		for (int i = 0, l = importedNode.getChildNodes().getLength(); i < l; ++i) {
			node.insertBefore(importedNode.getFirstChild(), refChild);
		}
	}
}