package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;

public interface BundlerHandler
{

	public static final String BUNDLE_PREFIX = "bundles";

	public String getAcceptedFileSuffix();

	public List<File> getBundledFiles(File rootDir, File testDir, File bundlerFile) throws Exception;

	public LegacyFileBundlerPlugin getBundler();
	
	public boolean serveOnly();

}
