package org.bladerunnerjs.aliasing.aliases;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bladerunnerjs.aliasing.AliasOverride;
import org.bladerunnerjs.testing.specutility.XmlBuilderSerializer;
import org.bladerunnerjs.utility.FileUtil;

import com.esotericsoftware.yamlbeans.parser.Parser.ParserException;
import com.google.common.base.Joiner;
import com.jamesmurty.utils.XMLBuilder;

public class AliasesWriter {
	private final AliasesData data;
	private final File file;
	private final FileUtil fileUtil;
	
	public AliasesWriter(AliasesData data, File file, String defaultInputEncoding) {
		this.data = data;
		this.file = file;
		fileUtil = new FileUtil(defaultInputEncoding);
	}
	
	public void write() throws IOException {
		try {
			XMLBuilder builder = XMLBuilder.create("aliases").ns("http://schema.caplin.com/CaplinTrader/aliases");
			
			if (data.scenario != null) {
				builder.a("useScenario", data.scenario);
			}
			
			if (!data.groupNames.isEmpty()) {
				builder.a("useGroups", Joiner.on(" ").join(data.groupNames));
			}
			
			for (AliasOverride aliasOverride : data.aliasOverrides) {
				builder.e("alias").a("name", aliasOverride.getName()).a("class", aliasOverride.getClassName());
			}
			
			fileUtil.write(file, XmlBuilderSerializer.serialize(builder));
		} catch (ParserException | TransformerException | ParserConfigurationException | FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}
	
}
