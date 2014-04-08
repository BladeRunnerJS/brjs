package org.bladerunnerjs.jstestdriver;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.appserver.BRJSThreadSafeModelAccessor;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;

import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class BundlerInjector implements ResourcePreProcessor
{	
	protected Map<String,String> bundlerHandlerPaths = new HashMap<>();

	private static final String BUNDLE_PREFIX = "bundles/";
	
	public BundlerInjector() throws Exception
	{
		bundlerHandlerPaths.put("js.bundle", "js/dev/en_GB/combined/bundle.js");
		bundlerHandlerPaths.put("css.bundle", "css/common/bundle.css");
		bundlerHandlerPaths.put("i18n.bundle", "i18n/en_GB.js");
		bundlerHandlerPaths.put("xml.bundle", "bundle.xml");
		bundlerHandlerPaths.put("html.bundle", "bundle.html");
	}

	@Override
	public List<FileInfo> processDependencies(List<FileInfo> files)
	{
		List<FileInfo> returnedFileList = new ArrayList<FileInfo>();
		for (FileInfo currentFileInfo : files)
		{
			File currentFile = new File(currentFileInfo.getFilePath());
			
			if (!currentFile.getPath().contains(BUNDLE_PREFIX))
			{
				returnedFileList.add(currentFileInfo);
			}
			else
			{
				returnedFileList.add(currentFileInfo);
    			handleBundleRequest(currentFile);
			}
		}

		return returnedFileList;
	}

	private void handleBundleRequest(File bundlerFile)
	{
		String bundlerPath = StringUtils.substringAfter(bundlerFile.getPath(), BUNDLE_PREFIX);
		String brjsRequestPath;
		
		if (bundlerHandlerPaths.containsKey(bundlerPath))
		{
			brjsRequestPath = bundlerHandlerPaths.get(bundlerPath);
		}
		else
		{
			brjsRequestPath = bundlerPath;
		}
		
		createParentDirectory(bundlerFile);
		
		OutputStream outputStream = createBundleOutputStream(bundlerFile);
		
		BRJS brjs = null;
		try
		{
    		brjs = BRJSThreadSafeModelAccessor.aquireModel();
    		
    		BundlableNode bundlableNode = brjs.locateAncestorNodeOfClass(bundlerFile, BundlableNode.class);
    		if (bundlableNode == null)
    		{
    			throw new RuntimeException("Unable to calculate bundlable node for the bundler file: " + bundlerFile.getAbsolutePath());
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
			catch (IOException e)
			{
				e.printStackTrace();
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
			throw new RuntimeException("Unable to create or write to file: " + bundlerFile.getPath() + "\n", ex);
		}
		
		return outputStream;
	}
	
}
