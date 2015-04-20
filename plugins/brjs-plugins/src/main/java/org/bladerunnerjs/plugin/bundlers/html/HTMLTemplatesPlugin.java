package org.bladerunnerjs.plugin.bundlers.html;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;

public class HTMLTemplatesPlugin extends AbstractTagHandlerPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public String getTagName() {
		return "html.bundle";
	}

	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		try {
			writer.write("<div id=\"templates\">\n");
			
			for(Reader reader : HTMLTemplateUtility.getReaders(bundleSet, version)) {
				IOUtils.copy(reader, writer);
			}
			
			writer.write("</div>\n");
		}
		catch(ContentProcessingException e) {
			throw new IOException(e);
		}
	}
}
