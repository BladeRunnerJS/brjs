package org.bladerunnerjs.spec.brjs.appserver;
// TODO: this copy of MockTagHandler doesn't need to be copied back when the tests are moved back to 'brjs-core'

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.TagHandlerPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;


public class MockTagHandler extends AbstractTagHandlerPlugin implements TagHandlerPlugin
{
	
	String name;
	String devContent;
	String prodContent;
	boolean printLocales;
	
	public MockTagHandler(String name, String devContent, String prodContent)
	{
		this(name, devContent, prodContent, false);
	}
	
	public MockTagHandler(String name, String devContent, String prodContent, boolean printLocales)
	{
		this.name = name;
		this.devContent = devContent;
		this.prodContent = prodContent;
		this.printLocales = printLocales;
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}

	@Override
	public String getTagName()
	{
		return name;
	}
	
	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException
	{
		PrintWriter printWriter = new PrintWriter(writer);
		String content = (requestMode == RequestMode.Dev) ? devContent : prodContent;
		
		if (!content.equals(""))
		{
			printWriter.print(content);
			if (tagAttributes.keySet().size() > 0) {
				printWriter.println("");
			}
			for (String attributeKey : tagAttributes.keySet())
			{
				String attributeValue = tagAttributes.get(attributeKey);
				printWriter.println(attributeKey+"="+attributeValue);
			}
		}
		
		if (printLocales) {
			printWriter.println("- "+locale);
		}
	}

}
