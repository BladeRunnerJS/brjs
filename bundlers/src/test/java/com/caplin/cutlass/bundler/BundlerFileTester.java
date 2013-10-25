package com.caplin.cutlass.bundler;

import static com.caplin.cutlass.bundler.BundlerTestUtils.convertToArray;
import static org.junit.Assert.assertArrayEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.core.plugin.bundler.LegacyFileBundlerPlugin;
import com.caplin.cutlass.BRJSAccessor;
import com.caplin.cutlass.testing.BRJSTestFactory;

import org.bladerunnerjs.model.exception.request.RequestHandlingException;

public class BundlerFileTester
{
	private LegacyFileBundlerPlugin bundler;
	private String pathPrefix;
	private File bundleDir;
	private File bundleTestDir;
	private String bundleRequest;
	
	public BundlerFileTester(LegacyFileBundlerPlugin bundler, String pathPrefix)
	{
		this.bundler = bundler;
		this.pathPrefix = pathPrefix.replaceAll("\\\\", "/");
		
		BRJSAccessor.initialize(BRJSTestFactory.createBRJS(new File(pathPrefix)));
	}

	public BundlerFileTester givenDirectoryOnDisk(String directory)
	{
		bundleDir = new File(pathPrefix, directory);
		return this;
	}

	public BundlerFileTester givenTestDirectoryOnDisk(String directory, String testDirectory)
	{
		bundleDir = new File(pathPrefix, directory);
		bundleTestDir = new File(bundleDir, testDirectory);
		return this;
	}
	
	public BundlerFileTester whenRequestReceived(String bundleRequest)
	{
		this.bundleRequest = bundleRequest;
		return this;
	}

	public void thenBundledFilesEquals(String[] expectedFiles) throws RequestHandlingException
	{
		List<File> bundleFiles = bundler.getBundleFiles(bundleDir, bundleTestDir, bundleRequest);
		removeTempBundleFileIfExists(bundleFiles);
		
		for (File f : bundleFiles )
		{
			System.err.println(f.getAbsolutePath());
		}
		assertArrayEquals("1a", addPathPrefix(pathPrefix, expectedFiles), convertToArray(bundleFiles));
	}
	
	// TODO: @writables-hack
	private void removeTempBundleFileIfExists(List<File> bundleFiles)
	{
		if((bundleFiles.size() > 1) && bundleFiles.get(bundleFiles.size() - 1).getPath().matches(".*alias-file_.*"))
		{
			bundleFiles.remove(bundleFiles.size() - 1);
		}
	}
	
	private String[] addPathPrefix(String appPrefix, String[] expectedFiles)
	{
		List<String> prefixedFiles = new ArrayList<String>();

		for (int i = 0; i < expectedFiles.length; ++i)
		{
			prefixedFiles.add(appPrefix + "/" + expectedFiles[i]);
		}
		
		return prefixedFiles.toArray(new String[]{});
	}
}
