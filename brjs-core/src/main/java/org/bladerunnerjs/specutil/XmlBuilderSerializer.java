package org.bladerunnerjs.specutil;

import java.util.Properties;

import javax.xml.transform.TransformerException;

import com.jamesmurty.utils.XMLBuilder;

public class XmlBuilderSerializer {
	public static String serialize(XMLBuilder builder) throws TransformerException {
		Properties outputProperties = new Properties();
		outputProperties.put(javax.xml.transform.OutputKeys.INDENT, "yes");
		outputProperties.put("{http://xml.apache.org/xslt}indent-amount", "4");
		System.setProperty("javax.xml.transform.TransformerFactory", "com.sun.org.apache.xalan.internal.xsltc.trax.TransformerFactoryImpl");
		
		return builder.asString(outputProperties);
	}
}
