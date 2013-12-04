package com.caplin.jstestdriver.plugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.caplin.cutlass.LegacyFileBundlerPlugin;

public class WritingResourceBundlerHandler implements BundlerHandler
{
	protected final String bundlerFileExtension;
	protected final LegacyFileBundlerPlugin thisBundler;
	private final boolean serveOnly;

	public WritingResourceBundlerHandler(LegacyFileBundlerPlugin thisBundler, String bundlerFileExtension, boolean serveOnly)
	{
		this.serveOnly = serveOnly;
		this.thisBundler = thisBundler;
		this.bundlerFileExtension = bundlerFileExtension;
	}
	
	@Override
	public boolean serveOnly()
	{
		return serveOnly;
	}
	
	@Override
	public List<File> getBundledFiles(File rootDir, File testDir, File bundlerFile)
	{
		createParentDirectory(bundlerFile);

		String rootPath = rootDir.getAbsolutePath();
		String bundleRequestPath = StringUtils.substringAfter(bundlerFile.getAbsolutePath(), rootPath).replace("\\", "/");
		if (bundleRequestPath.contains(BundlerHandler.BUNDLE_PREFIX))
		{
			bundleRequestPath = StringUtils.substringAfterLast(bundleRequestPath, BundlerHandler.BUNDLE_PREFIX + "/");
		}

		OutputStream outputStream = createBundleOutputStream(bundlerFile);
		
		try
		{
			List<File> bundleFiles = thisBundler.getBundleFiles(rootDir, testDir, bundleRequestPath);
			thisBundler.writeBundle(bundleFiles, outputStream);
			outputStream.close();
		}
		catch (Exception ex)
		{
			throw new RuntimeException("There was an error while bundling.", ex);
		}
		/* TODO: this is a hack because when we create file infos files cannot be loaded properly in tests
		  			(see TODO in BundlerInjector */
		//return Arrays.asList(bundlerFile);
		return Arrays.asList(new File[0]);
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
	
	@Override
	public String getAcceptedFileSuffix()
	{
		return bundlerFileExtension;
	}

	@Override
	public LegacyFileBundlerPlugin getBundler()
	{
		return thisBundler;
	}

}
