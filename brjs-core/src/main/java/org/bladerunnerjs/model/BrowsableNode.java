package org.bladerunnerjs.model;

import java.io.Writer;

import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.plugin.Locale;

public interface BrowsableNode extends BundlableNode {
	public void filterIndexPage(String indexPage, Locale locale, String version, Writer writer, RequestMode requestMode) throws ModelOperationException;
}
