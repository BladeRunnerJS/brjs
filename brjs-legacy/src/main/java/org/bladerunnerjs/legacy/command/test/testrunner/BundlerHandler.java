package org.bladerunnerjs.legacy.command.test.testrunner;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.utility.BundleSetRequestHandler;


public class BundlerHandler
{	
	private App app;
	protected Map<String,String> legacyBundlerHandlerPaths = new LinkedHashMap<>();
	protected Map<String,String> logicalBundlerHandlerPaths = new LinkedHashMap<>();
	private BundlableNode bundlableNode;
	
	public BundlerHandler(BundlableNode bundlableNode)
	{
		// legacy paths - these are matched against the last part of the bundle path - e.g. js/js.bundle would match js.bundle
		legacyBundlerHandlerPaths.put("js.bundle", "js/dev/combined/bundle.js");
		legacyBundlerHandlerPaths.put("css.bundle", "css/common/bundle.css");
		legacyBundlerHandlerPaths.put("i18n.bundle", "i18n/en_GB.js");
		legacyBundlerHandlerPaths.put("(.*)_i18n.bundle", "i18n/$1.js"); // .* is a bad regex for a locale but since this is simply for legacy support we can get away with it
		legacyBundlerHandlerPaths.put("xml.bundle", "xml/bundle.xml");
		legacyBundlerHandlerPaths.put("html.bundle", "html/bundle.html");
		
		// logical/utility paths
		logicalBundlerHandlerPaths.put("bundle.js", "js/dev/combined/bundle.js");
		logicalBundlerHandlerPaths.put("bundle.css", "css/common/bundle.css");
		logicalBundlerHandlerPaths.put("bundle.i18n", "i18n/en_GB.js");
		logicalBundlerHandlerPaths.put("bundle.xml", "xml/bundle.xml");
		logicalBundlerHandlerPaths.put("bundle.html", "html/bundle.html");
		
		this.bundlableNode = bundlableNode;
	}

	
	public void createBundleFile(File bundleFile, String bundlePath, String version) throws IOException, MalformedRequestException, ResourceNotFoundException, ContentProcessingException, ModelOperationException
	{
		if (bundlePath.contains("\\"))
		{
			throw new IllegalArgumentException("Invalid bundlePath - it should not contain '\', only '/' as a seperator");
		}
		
		String modelRequestPath = getModelRequestPath(bundlePath);
		
		repeatedlyAttemptToCreateBundleFile(bundleFile);
		
		try (OutputStream bundleFileOutputStream = new FileOutputStream(bundleFile, false);
			ResponseContent content = BundleSetRequestHandler.handle(new JsTestDriverBundleSet(bundlableNode.getBundleSet()), modelRequestPath, new StaticContentAccessor(app), version); )
		{
			content.write( bundleFileOutputStream );
			bundleFileOutputStream.flush();
		}
		
	}
	
	// this is a workaround for the scenario where Windows indexing service or virus scanners etc can lock the file when we try to create it
	// see http://stackoverflow.com/a/10516563/2634854 for more info
	private void repeatedlyAttemptToCreateBundleFile(File bundleFile) throws IOException
	{
		if (bundleFile.exists()) {
			throw new IOException( String.format("The bundle file '%s' already exists and should have previously been deleted so new content can be written to it", bundleFile.getAbsolutePath()) );
		}
		bundleFile.getParentFile().mkdirs();
		for (int i = 0; i < 100; i++) {
			try {
    			bundleFile.createNewFile();
    			if (bundleFile.isFile()) {
    				return;
    			}
    			Thread.sleep(10);
			} catch (IOException | InterruptedException ex) {
				// ignore the exception from creating the file or thread interupted
			}
		}
		throw new IOException("Unable to create an empty bundle file at " + bundleFile.getAbsolutePath());
	}


	private String getModelRequestPath(String bundlerPath)
	{
		String brjsRequestPath = lookupRequestPathFromKnownBundlePaths(bundlerPath);
		
		if (brjsRequestPath == null)
		{
			brjsRequestPath = bundlerPath;
		}
		
		return brjsRequestPath;
	}
	
	private String lookupRequestPathFromKnownBundlePaths(String bundlerFilePath)
	{
		String legacyBundleKey = (bundlerFilePath.contains("/")) ? StringUtils.substringAfterLast(bundlerFilePath, "/") : bundlerFilePath;
		for (String keyRegex : legacyBundlerHandlerPaths.keySet())
		{
			if (legacyBundleKey.matches(keyRegex))
			{
				String bundlerConvertedPath = legacyBundlerHandlerPaths.get(keyRegex);
				String bundlerPath = legacyBundleKey.replaceAll(keyRegex, bundlerConvertedPath);
				return bundlerPath;
			}
		}
		
		if (logicalBundlerHandlerPaths.containsKey(bundlerFilePath))
		{
			return logicalBundlerHandlerPaths.get(bundlerFilePath);
		}
		
		return null;
	}
}
