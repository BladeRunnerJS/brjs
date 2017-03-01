package org.bladerunnerjs.plugin.bundlers.inline;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.model.RequestMode;

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
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		String filePath = tagAttributes.get(FILE_ATTRIBUTE);
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		try {
			File file = bundlableNode.file(filePath);
			writer.write( FileUtils.readFileToString(file) );
		}
		catch(IOException e) {
			throw new IOException( String.format(Messages.FILE_NOT_FOUND, filePath, filePath, bundlableNode.dir()));
		}
	}

}
