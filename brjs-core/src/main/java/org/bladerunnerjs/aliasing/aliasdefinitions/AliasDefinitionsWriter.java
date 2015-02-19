package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.aliasing.AliasDefinition;
import org.bladerunnerjs.api.aliasing.AliasOverride;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.utility.XmlBuilderSerializer;
import org.bladerunnerjs.utility.EncodedFileUtil;

import com.jamesmurty.utils.XMLBuilder;

public class AliasDefinitionsWriter {
	public static void write(BRJS brjs, AliasDefinitionsData data, MemoizedFile file, String defaultFileCharacterEncoding) throws IOException {
		try {
			EncodedFileUtil fileUtil = new EncodedFileUtil(brjs, defaultFileCharacterEncoding);
			XMLBuilder builder = XMLBuilder.create("aliasDefinitions").ns("http://schema.caplin.com/CaplinTrader/aliasDefinitions");
			
			for (AliasDefinition aliasDefinition : data.aliasDefinitions) {
				XMLBuilder aliasBuilder = builder.e("alias").a("name", aliasDefinition.getName());
				Map<String, AliasOverride> scenarioAliases = data.getScenarioAliases(aliasDefinition.getName());
				
				if(aliasDefinition.getClassName() != null) {
					aliasBuilder.a("defaultClass", aliasDefinition.getClassName());
				}
				
				if(aliasDefinition.getInterfaceName() != null) {
					aliasBuilder.a("interface", aliasDefinition.getInterfaceName());
				}
				
				for (String scenarioName : scenarioAliases.keySet()) {
					AliasOverride scenarioAlias = scenarioAliases.get(scenarioName);
					aliasBuilder.e("scenario").a("name", scenarioName).a("class", scenarioAlias.getClassName());
				}
			}
			
			for (String groupName : data.groupAliases.keySet()) {
				XMLBuilder groupBuilder = builder.e("group").a("name", groupName);
				
				for (AliasOverride groupAlias : data.groupAliases.get(groupName)) {
					groupBuilder.e("alias").a("name", groupAlias.getName()).a("class", groupAlias.getClassName());
				}
			}
			
			fileUtil.write(file, XmlBuilderSerializer.serialize(builder));
		}
		catch (IOException | ParserConfigurationException | FactoryConfigurationError | TransformerException e) {
			throw new IOException(e);
		}
	}
}
