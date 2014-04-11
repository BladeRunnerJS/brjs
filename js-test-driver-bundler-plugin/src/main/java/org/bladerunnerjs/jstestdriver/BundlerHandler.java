package org.bladerunnerjs.jstestdriver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;

import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class BundlerHandler implements ResourcePreProcessor
{	
	protected Map<String,String> legacyBundlerHandlerPaths = new HashMap<>();
	protected Map<String,String> logicalBundlerHandlerPaths = new HashMap<>();

	private static final String BUNDLE_PREFIX = "bundles" + File.separator;
	
	public BundlerHandler() throws Exception
	{
		// legacy paths - these are matched against the last part of the bundle path - e.g. js/js.bundle would match js.bundle
		legacyBundlerHandlerPaths.put("js.bundle", "js/dev/en_GB/combined/bundle.js");
		legacyBundlerHandlerPaths.put("css.bundle", "css/common/bundle.css");
		legacyBundlerHandlerPaths.put("i18n.bundle", "i18n/en_GB.js");
		legacyBundlerHandlerPaths.put("(.*)_i18n.bundle", "i18n/$1.js"); // .* is a bad regex for a locale but since this is simply for legacy support we can get away with it
		legacyBundlerHandlerPaths.put("xml.bundle", "bundle.xml");
		legacyBundlerHandlerPaths.put("html.bundle", "bundle.html");
		
		// logical/utility paths
		logicalBundlerHandlerPaths.put("bundle.js", "js/dev/en_GB/combined/bundle.js");
		logicalBundlerHandlerPaths.put("bundle.css", "css/common/bundle.css");
		logicalBundlerHandlerPaths.put("bundle.i18n", "i18n/en_GB.js");
		logicalBundlerHandlerPaths.put("bundle.xml", "bundle.xml");
		logicalBundlerHandlerPaths.put("bundle.html", "bundle.html");
	}

	@Override
	public List<FileInfo> processDependencies(List<FileInfo> files)
	{
		for (FileInfo currentFileInfo : files)
		{
			File currentFile = new File(currentFileInfo.getFilePath());
			if (currentFile.getAbsolutePath().contains(BUNDLE_PREFIX))
			{
				String modelRequestPath = getModelRequestPath(currentFile);
				createParentDirectory(currentFile);
				OutputStream outputStream = createBundleOutputStream(currentFile);
				handleBundleRequest(currentFile, modelRequestPath, outputStream);
			}
		}

		return files;
	}

	private String getModelRequestPath(File bundleFile)
	{
		String bundlerPath = StringUtils.substringAfter(bundleFile.getAbsolutePath(), BUNDLE_PREFIX)
				.replace(File.separator, "/");
		
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
		
//		if (logicalBundlerHandlerPaths.containsKey(bundlerFilePath))
//		{
//			return logicalBundlerHandlerPaths.get(bundlerFilePath);
//		}
		
		return null;
	}

	private void handleBundleRequest(File bundleFile, String brjsRequestPath, OutputStream outputStream)
	{
		BRJS brjs = null;
		try
		{
    		brjs = BRJSThreadSafeModelAccessor.aquireModel();
    		
    		BundlableNode bundlableNode = brjs.locateAncestorNodeOfClass(bundleFile, BundlableNode.class);
    		if (bundlableNode == null)
    		{
    			throw new RuntimeException("Unable to calculate bundlable node for the bundler file: " + bundleFile.getAbsolutePath());
    		}
    		
    		bundlableNode.handleLogicalRequest(brjsRequestPath, outputStream);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("There was an error while bundling.", ex);
		}
		finally 
		{
			if (brjs != null)
			{
				BRJSThreadSafeModelAccessor.releaseModel();
			}
			try
			{
				outputStream.close();
			}
			catch (IOException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		
	}

	@Override
	public List<FileInfo> processPlugins(List<FileInfo> files)
	{
		return files;
	}

	@Override
	public List<FileInfo> processTests(List<FileInfo> files)
	{
		return files;
	}
	
	private void createParentDirectory(File bundlerFile)
	{
		boolean parentDirCreationFailed = false;
		boolean filesCreated = false;

		Exception failureException = null;

		try
		{
			filesCreated = bundlerFile.getParentFile().mkdirs();
		}
		catch (Exception ex)
		{
			parentDirCreationFailed = true;
			failureException = ex;
		}
		if ((!filesCreated && !bundlerFile.getParentFile().exists()) || parentDirCreationFailed)
		{
			throw new RuntimeException("Unable to create parent directory: " + bundlerFile.getParentFile() + ((failureException != null) ? "\n" + failureException.toString() : ""));
		}
	}
	
	private OutputStream createBundleOutputStream(File bundlerFile)
	{
		OutputStream outputStream = null;
		
		try
		{
			if (bundlerFile.exists())
			{
				bundlerFile.delete();
			}
			bundlerFile.createNewFile();
			outputStream = new BufferedOutputStream(new FileOutputStream(bundlerFile));
		}
		catch (Exception ex)
		{
			throw new RuntimeException("Unable to create or write to file: " + bundlerFile.getAbsolutePath() + "\n", ex);
		}
		
		return outputStream;
	}
	
}
