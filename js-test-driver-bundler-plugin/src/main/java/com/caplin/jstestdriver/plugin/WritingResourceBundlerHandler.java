package com.caplin.jstestdriver.plugin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.logging.ConsoleLoggerConfigurator;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.slf4j.impl.StaticLoggerBinder;

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
		OutputStream outputStream = createBundleOutputStream(bundlerFile);
		
		try
		{
			//TODO: this is *really* hacky - get rid of this once everything is using the new model
			if (thisBundler != null)
			{
				useLegacyBundlerToHandleBundle(rootDir, testDir, bundlerFile, outputStream);
			}
			else
			{
				
				useBrjsToHandleBundle(rootDir, testDir, outputStream);
			}
			
		}
		catch (Exception ex)
		{
			throw new RuntimeException("There was an error while bundling.", ex);
		}
		finally 
		{
			try
			{
				outputStream.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		/* TODO: this is a hack because when we create file infos files cannot be loaded properly in tests
		  			(see TODO in BundlerInjector */
		//return Arrays.asList(bundlerFile);
		return Arrays.asList(new File[0]);
	}

	private void useLegacyBundlerToHandleBundle(File rootDir, File testDir, File bundlerFile, OutputStream outputStream) throws RequestHandlingException
	{
		String rootPath = rootDir.getAbsolutePath();
		String bundleRequestPath = StringUtils.substringAfter(bundlerFile.getAbsolutePath(), rootPath).replace("\\", "/");
		if (bundleRequestPath.contains(BundlerHandler.BUNDLE_PREFIX))
		{
			bundleRequestPath = StringUtils.substringAfterLast(bundleRequestPath, BundlerHandler.BUNDLE_PREFIX + "/");
		}
		List<File> bundleFiles = thisBundler.getBundleFiles(rootDir, testDir, bundleRequestPath);
		thisBundler.writeBundle(bundleFiles, outputStream);
	}

	private void useBrjsToHandleBundle(File rootDir, File testDir, OutputStream outputStream) throws ModelOperationException, RequirePathException, IOException, FileNotFoundException, MalformedRequestException, ResourceNotFoundException, BundlerProcessingException
	{
		BRJS brjs = null;
		try
		{
    		brjs = new BRJS(rootDir, new ConsoleLoggerConfigurator(StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger()));
    		App app = brjs.locateAncestorNodeOfClass(testDir, App.class);
    		if (app == null)
    		{
    			throw new RuntimeException("Unable to calculate App node for the test dir: " + testDir.getAbsolutePath());
    		}				
    		
    		String pathRelativeToApp = StringUtils.substringAfter(testDir.getAbsolutePath(), app.dir().getAbsolutePath());
    		String bladerunnerUriRequestPathPrefix = StringUtils.substringBeforeLast(pathRelativeToApp, bundlerFileExtension);
    		
    		BladerunnerUri requestUri = new BladerunnerUri(brjs, app.dir(), "/"+app.getName(), bladerunnerUriRequestPathPrefix+"js/dev/en_GB/combined/bundle.js", null);
    		
    		app.handleLogicalRequest(requestUri, outputStream);
		}
		finally
		{
			if (brjs != null)
			{
				brjs.close();
			}
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
