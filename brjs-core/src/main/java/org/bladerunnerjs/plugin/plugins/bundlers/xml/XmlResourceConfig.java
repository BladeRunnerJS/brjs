package org.bladerunnerjs.plugin.plugins.bundlers.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class XmlResourceConfig
{
	private List<String> templateElements = new ArrayList<String>();
	private Map<String, String> mergeElementIdentifiers = new HashMap<String, String>();
	private Map<String, Boolean> mergeElements = new HashMap<String, Boolean>();
	
	XmlResourceConfig(final String rootElementStr, final String templateElementsStr, final String mergeElementsStr)
	{
		String[] mergeElementsArray = mergeElementsStr.split(" *, *");
		String[] templateElementsArray = templateElementsStr.split(" *, *");
		
		for(String mergeElement : mergeElementsArray)
		{
			if(!mergeElement.contains("@"))
			{
				mergeElements.put(mergeElement, Boolean.TRUE);
			}
			else
			{
				String[] parts = mergeElement.split("@");
				
				mergeElements.put(parts[0], Boolean.TRUE);
				mergeElementIdentifiers.put(parts[0], parts[1]);
			}
		}
		
		templateElements.add(rootElementStr);
		for(String templateElement : templateElementsArray)
		{
			templateElements.add(templateElement);
		}
	}
	
	public List<String> getTemplateElements()
	{
		return templateElements;
	}
	
	public String getMergeElementIdentifier(final String templateElement)
	{
		String identifier = mergeElementIdentifiers.get(templateElement);
		
		if(identifier == null)
		{
			identifier = "id";
		}
		
		return identifier;
	}
	
	public Map<String, Boolean> getMergeElements()
	{
		return mergeElements;
	}
}