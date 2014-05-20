package org.bladerunnerjs.utility;

import java.io.IOException;
import java.io.OutputStream;

import org.bladerunnerjs.model.BrowsableNode;

public interface PageAccessor {
	String getIndexPage(BrowsableNode browsableNode, String locale, OutputStream os) throws IOException;
}
