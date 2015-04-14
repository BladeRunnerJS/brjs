package org.bladerunnerjs.api;

import java.io.Writer;

import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.model.RequestMode;

public interface BrowsableNode extends BundlableNode {
	public void filterIndexPage(String indexPage, Locale locale, String version, Writer writer, RequestMode requestMode) throws ModelOperationException;
}
