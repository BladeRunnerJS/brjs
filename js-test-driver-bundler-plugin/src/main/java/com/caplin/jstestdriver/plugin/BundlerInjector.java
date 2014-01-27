package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.BRJS;

import com.caplin.cutlass.bundler.css.CssBundler;
import com.caplin.cutlass.bundler.html.HtmlBundler;
import com.caplin.cutlass.bundler.i18n.I18nBundler;
import com.caplin.cutlass.bundler.xml.XmlBundler;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class BundlerInjector implements ResourcePreProcessor
{
	protected List<BundlerHandler> bundlerHandlers;

	public BundlerInjector() throws Exception
	{		
		bundlerHandlers = new ArrayList<BundlerHandler>();
		bundlerHandlers.add(new WritingResourceBundlerHandler(null, "js.bundle", false));
		bundlerHandlers.add(new WritingResourceBundlerHandler(new CssBundler(), "css.bundle", true));
		bundlerHandlers.add(new WritingResourceBundlerHandler(new I18nBundler(), "i18n.bundle", false));
		bundlerHandlers.add(new WritingResourceBundlerHandler(new XmlBundler(), "xml.bundle", true));
		bundlerHandlers.add(new WritingResourceBundlerHandler(new HtmlBundler(), "html.bundle", true));
	}

	@Override
	public List<FileInfo> processDependencies(List<FileInfo> files)
	{
		List<FileInfo> returnedFileList = new ArrayList<FileInfo>();
		for (FileInfo currentFileInfo : files)
		{
			File currentFile = new File(currentFileInfo.getFilePath());
			List<BundlerHandler> validBundlerHandlers = getBundlerHandlersForExtension(currentFile);
			if (validBundlerHandlers.size() < 1)
			{
				// No bundler, so just return this as a resource.
				returnedFileList.add(currentFileInfo);
			}
			else
			{
				/* 	TODO This is a hack so that we only pass the .bundle file as a dependency as we just write to it.
					The writing resource handler will always return an empty list */
				try
				{
					returnedFileList.addAll(getBundledFilesForEachBundlerHandler(currentFile, validBundlerHandlers));
				}
				catch (RuntimeException ex)
				{
					// TODO: experimental logging added -- remove when finished
					ex.printStackTrace();
					
					throw ex;
				}
				catch (Exception ex)
				{
					// TODO: experimental logging added -- remove when finished
					ex.printStackTrace();
					
					throw new RuntimeException(ex);
				}
				returnedFileList.add(currentFileInfo); 
			}
		}

		return returnedFileList;
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

	protected void setBundlerHandlers(List<BundlerHandler> bundlerHandlers)
	{
		this.bundlerHandlers = bundlerHandlers;
	}

	private List<BundlerHandler> getBundlerHandlersForExtension(File thisFile)
	{
		List<BundlerHandler> validBundlerHandlers = new ArrayList<BundlerHandler>();
		String thisFileName = thisFile.getName();
		for (BundlerHandler handler : bundlerHandlers)
		{
			boolean isAbsoluteMatch = thisFileName.equals(handler.getAcceptedFileSuffix());
			boolean hasValidMatchingExtension = thisFileName.endsWith("_" + handler.getAcceptedFileSuffix());

			if (isAbsoluteMatch || hasValidMatchingExtension)
			{
				validBundlerHandlers.add(handler);
			}
		}
		return validBundlerHandlers;
	}

	private List<FileInfo> getBundledFilesForEachBundlerHandler(File bundleFile, List<BundlerHandler> bundlerHandlers) throws Exception
	{
		List<FileInfo> returnedFileList = new ArrayList<FileInfo>();
		File rootDir = getRootDirForBundleFile(bundleFile);
		File testDir = getTestDirForBundleFile(bundleFile);

		for (BundlerHandler handler : bundlerHandlers)
		{
			addBundledFilesToCurrentFileList(handler, returnedFileList, handler.getBundledFiles(rootDir, testDir, bundleFile));
		}
		return returnedFileList;
	}

	private File getRootDirForBundleFile(File bundleFile)
	{
		return CutlassDirectoryLocator.getScopePath(bundleFile);
	}

	private File getTestDirForBundleFile(File bundleFile)
	{
		return new File(StringUtils.substringBefore(bundleFile.getAbsolutePath(), BundlerHandler.BUNDLE_PREFIX).replace("\\", "/"));
	}

	private void addBundledFilesToCurrentFileList(BundlerHandler handler, List<FileInfo> currentFileList, List<File> bundledFiles)
	{
		for (File thisFile : bundledFiles)
		{
			currentFileList.add(new FileInfo(thisFile.getPath(), -1, -1, false, handler.serveOnly(), null, thisFile.getPath()));
		}
	}

}
