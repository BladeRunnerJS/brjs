package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.logging.ConsoleLoggerConfigurator;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.utility.RelativePathUtility;
import org.slf4j.impl.StaticLoggerBinder;

import com.caplin.cutlass.file.RelativePath;

public class BRJSWritingResourceBundlerHandler extends WritingResourceBundlerHandler
{
	protected final String bundlerFileExtension;
	private final String brjsRequestPath;
	

	public BRJSWritingResourceBundlerHandler(String bundlerFileExtension, String brjsRequestPath, boolean serveOnly)
	{
		super(null, bundlerFileExtension, serveOnly);
		this.bundlerFileExtension = bundlerFileExtension;
		this.brjsRequestPath = brjsRequestPath;
	}
	
	@Override
	public List<File> getBundledFiles(File rootDir, File testDir, File bundlerFile)
	{
		createParentDirectory(bundlerFile);
		OutputStream outputStream = createBundleOutputStream(bundlerFile);
		
		BRJS brjs = null;
		try
		{
    		brjs = new BRJS(rootDir, new ConsoleLoggerConfigurator(StaticLoggerBinder.getSingleton().getLoggerFactory().getRootLogger()));
    		App app = brjs.locateAncestorNodeOfClass(testDir, App.class);
    		if (app == null)
    		{
    			throw new RuntimeException("Unable to calculate App node for the test dir: " + testDir.getAbsolutePath());
    		}				
    		
    		String pathRelativeToApp = "/" + RelativePathUtility.get(app.dir(), testDir);
    		String bladerunnerUriRequestPathPrefix = StringUtils.substringBeforeLast(pathRelativeToApp, bundlerFileExtension);
    		bladerunnerUriRequestPathPrefix = (bladerunnerUriRequestPathPrefix.endsWith("/"))  ? bladerunnerUriRequestPathPrefix : bladerunnerUriRequestPathPrefix+"/";
    		
    		BladerunnerUri requestUri = new BladerunnerUri(brjs, app.dir(), "/"+app.getName(), bladerunnerUriRequestPathPrefix+brjsRequestPath, null);
    		
    		app.handleLogicalRequest(requestUri, outputStream);
		}
		catch (Exception ex)
		{
			throw new RuntimeException("There was an error while bundling.", ex);
		}
		finally 
		{
			brjs.close();
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
	
}
