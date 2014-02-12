package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundlableNode;

import com.caplin.cutlass.BRJSAccessor;

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
    		brjs = BRJSAccessor.root;
    	
    		BundlableNode bundlableNode = brjs.locateAncestorNodeOfClass(testDir, BundlableNode.class);
    		if (bundlableNode == null)
    		{
    			throw new RuntimeException("Unable to calculate bundlable node for the test dir: " + testDir.getAbsolutePath());
    		}
    		
    		bundlableNode.handleLogicalRequest(brjsRequestPath, outputStream);
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
	
}
