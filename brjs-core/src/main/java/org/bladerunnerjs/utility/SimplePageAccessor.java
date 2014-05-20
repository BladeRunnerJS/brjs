package org.bladerunnerjs.utility;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class SimplePageAccessor implements PageAccessor {
	@Override
	public String getIndexPage(File indexPage) throws IOException {
		return FileUtils.readFileToString(indexPage);
	}
}
