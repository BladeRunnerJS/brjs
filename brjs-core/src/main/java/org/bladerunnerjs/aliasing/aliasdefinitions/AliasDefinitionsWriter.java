package org.bladerunnerjs.aliasing.aliasdefinitions;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bladerunnerjs.aliasing.AliasDefinition;
import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.testing.specutility.XmlBuilderSerializer;
import org.bladerunnerjs.utility.FileUtil;

import com.jamesmurty.utils.XMLBuilder;

public class AliasDefinitionsWriter {
	private final AliasDefinitionsData data;
	private final File file;
	private final FileUtil fileUtil;
	
	public AliasDefinitionsWriter(AliasDefinitionsData data, File file, String defaultFileCharacterEncoding) {
		this.data = data;
		this.file = file;
		fileUtil = new FileUtil(defaultFileCharacterEncoding);
	}
	
	public void write() throws IOException {
		try {
			XMLBuilder builder = XMLBuilder.create("aliasDefinitions").ns("http://schema.caplin.com/CaplinTrader/aliasDefinitions");
			
			for (AliasDefinition aliasDefinition : data.aliasDefinitions) {
				XMLBuilder aliasBuilder = builder.e("alias").a("name", aliasDefinition.getName())
					.a("defaultClass", aliasDefinition.getClassName());
				Map<String, AliasOverride> scenarioAliases = data.getScenarioAliases(aliasDefinition.getName());
				
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
