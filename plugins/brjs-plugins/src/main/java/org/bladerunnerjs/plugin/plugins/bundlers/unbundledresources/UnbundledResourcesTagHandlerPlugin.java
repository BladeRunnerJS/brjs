package org.bladerunnerjs.plugin.plugins.bundlers.unbundledresources;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;

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
			App app = bundleSet.bundlableNode().app();
			writer.write(app.requestHandler().createRelativeBundleRequest("unbundled-resources", version));
		}
		catch (MalformedTokenException e) {
			throw new IOException(e);
		}
	}
}
