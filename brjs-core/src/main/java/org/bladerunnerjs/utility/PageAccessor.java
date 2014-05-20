package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

public interface PageAccessor {
	String getIndexPage(File indexPage) throws IOException;
}
