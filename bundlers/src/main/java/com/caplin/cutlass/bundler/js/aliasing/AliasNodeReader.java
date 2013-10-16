package com.caplin.cutlass.bundler.js.aliasing;

import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamException;
import org.codehaus.stax2.XMLStreamReader2;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import com.caplin.cutlass.exception.NamespaceException;

public class AliasNodeReader implements AliasingNodeReader {

	private XMLStreamReader2 underlyingStreamReader;
	
	public AliasNodeReader(XMLStreamReader2 underlyingStreamReader) {
		this.underlyingStreamReader = underlyingStreamReader;
	}

	@Override
	public AliasingNode getCurrentNode() throws BundlerFileProcessingException, XMLStreamException, NamespaceException {
		String aliasName = underlyingStreamReader.getAttributeValue(null, "name");
		String defaultClassName = underlyingStreamReader.getAttributeValue(null, "defaultClass");
		String className = underlyingStreamReader.getAttributeValue(null, "class");
		String interfaceName = underlyingStreamReader.getAttributeValue(null, "interface");
		
		Map<String, String> scenarios = processScenarios(aliasName, interfaceName);
		scenarios.put(AliasRegistry.DEFAULT_SCENARIO, (defaultClassName != null) ? defaultClassName : className);
		
		return new AliasNode(aliasName, interfaceName, scenarios, className);
	}
	
	private Map<String, String> processScenarios( String alias, String interfaceName ) throws XMLStreamException, BundlerFileProcessingException
	{
		boolean withinAliasTag = true;
		Map<String, String> scenarios = new HashMap<String, String>();
		
		
		while(withinAliasTag)
		{
			underlyingStreamReader.nextTag();
			
			if(underlyingStreamReader.isStartElement() && underlyingStreamReader.getLocalName().toString().equals("scenario"))
			{
				String scenarioName = underlyingStreamReader.getAttributeValue(null, "name");
				String scenarioClass = underlyingStreamReader.getAttributeValue(null, "class");
				
				scenarios.put(scenarioName, scenarioClass);
				
				underlyingStreamReader.nextTag();
			}
			else
			{
				withinAliasTag = false;
			}
		}
	
		return scenarios;
	
	}
	

}
