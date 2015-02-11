package org.bladerunnerjs.plugin.plugins.bundlers.unbundledresources;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;

public class UnbundledResourcesTagHandlerPlugin extends AbstractTagHandlerPlugin {
	@Override
	public void setBRJS(BRJS brjs) {
		// do nothing
	}
	
	@Override
	public String getTagName()
	{
		return "unbundled-resources";
	}

	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		try {
			App app = bundleSet.getBundlableNode().app();
			writer.write(app.requestHandler().createRelativeBundleRequest("unbundled-resources", version));
		}
		catch (MalformedTokenException e) {
			throw new IOException(e);
		}
	}
}
