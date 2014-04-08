package org.bladerunnerjs.jstestdriver;

import java.io.File;
import java.util.List;

public interface BundlerHandler
{

	public static final String BUNDLE_PREFIX = "bundles";

	public List<File> getBundledFiles(File rootDir, File testDir, File bundlerFile) throws Exception;
	
	public boolean serveOnly();

}
