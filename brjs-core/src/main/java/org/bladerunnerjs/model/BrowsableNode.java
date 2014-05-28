package org.bladerunnerjs.model;

import java.io.Writer;

import org.bladerunnerjs.model.exception.ModelOperationException;

public interface BrowsableNode extends BundlableNode {
	public void filterIndexPage(String indexPage, String locale, String version, Writer writer, RequestMode requestMode) throws ModelOperationException;
}
