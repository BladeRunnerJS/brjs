package org.bladerunnerjs.model.utility;

import java.io.Writer;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.Mode;

public class IndexPageWriter {

	public static void write(String content, BundleSet bundleSet, Writer writer, Mode opMode, String locale) 
	{
		BRJS brjs = bundleSet.getBundlableNode().root();
//		List<TagHandlerPlugin> tagHandlerPlugins = brjs.getTagHandlers();
//		loop over
	//		writeDevTagContent()
	//		writeProdTagContent()
	}
}
