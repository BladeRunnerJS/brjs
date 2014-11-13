package org.bladerunnerjs.plugin.plugins.bundlers.inline;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;
import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;

public class InlineTagHandlerPlugin extends AbstractTagHandlerPlugin {
	
	private static String FILE_ATTRIBUTE = "file";
	
	public static class Messages {
		public static final String FILE_NOT_FOUND = "\n\nError while trying to parse the <@inline file='%s' @/> tag.\nThe file was not found at '%s' relative to '%s'.\nPlease check that this file exists and is in the correct location. \n\n";
	}

	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getTagName() {
		return "inline";
	}
	
	@Override
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		try {
			writeTagContent(writer, bundleSet, tagAttributes);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		try {
			writeTagContent(writer, bundleSet, tagAttributes);
		}
		catch(Exception e) {
			throw new IOException(e);
		}
	}
	
	private void writeTagContent(Writer writer, BundleSet bundleSet, Map<String, String> tagAttributes) throws IOException {
		String filePath = tagAttributes.get(FILE_ATTRIBUTE);
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		try {
			File file = bundlableNode.file(filePath);
			writer.write( FileUtils.readFileToString(file) );
		}
		catch(IOException e) {
			throw new IOException( String.format(Messages.FILE_NOT_FOUND, filePath, filePath, bundlableNode.dir()));
		}
	}

}
