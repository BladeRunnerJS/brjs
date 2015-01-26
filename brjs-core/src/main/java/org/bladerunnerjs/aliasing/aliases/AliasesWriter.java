package org.bladerunnerjs.aliasing.aliases;

import java.io.IOException;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.aliasing.AliasOverride;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.spec.utility.XmlBuilderSerializer;
import org.bladerunnerjs.utility.EncodedFileUtil;

import com.esotericsoftware.yamlbeans.parser.Parser.ParserException;
import com.google.common.base.Joiner;
import com.jamesmurty.utils.XMLBuilder;

public class AliasesWriter {
	public static void write(BRJS brjs, AliasesData data, MemoizedFile file, String defaultFileCharacterEncoding) throws IOException {
		try {
			EncodedFileUtil fileUtil = new EncodedFileUtil(brjs, defaultFileCharacterEncoding);
			XMLBuilder builder = XMLBuilder.create("aliases").ns("http://schema.caplin.com/CaplinTrader/aliases");
			
			if (data.scenario != null) {
				builder.a("useScenario", data.scenario);
			}
			
			if (!data.groupNames.isEmpty()) {
				builder.a("useGroups", Joiner.on(" ").join(data.groupNames));
			}
			
			for (AliasOverride aliasOverride : data.aliasOverrides) {
				XMLBuilder element = builder.e("alias").a("name", aliasOverride.getName());
				
				if(aliasOverride.getClassName() != null) {
					element.a("class", aliasOverride.getClassName());
				}
			}
			
			fileUtil.write(file, XmlBuilderSerializer.serialize(builder));
		} catch (ParserException | TransformerException | ParserConfigurationException | FactoryConfigurationError e) {
			throw new IOException(e);
		}
	}
}
