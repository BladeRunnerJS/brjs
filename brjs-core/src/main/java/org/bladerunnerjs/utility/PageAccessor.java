package org.bladerunnerjs.utility;

import java.io.IOException;

import org.bladerunnerjs.model.BrowsableNode;

public interface PageAccessor {
	void serveIndexPage(BrowsableNode browsableNode, String locale) throws IOException;
}
