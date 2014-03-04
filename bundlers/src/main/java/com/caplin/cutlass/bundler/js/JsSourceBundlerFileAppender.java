package com.caplin.cutlass.bundler.js;

import java.io.File;
import java.util.List;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import com.caplin.cutlass.structure.CutlassDirectoryLocator;

public class JsSourceBundlerFileAppender implements BladeRunnerFileAppender
{
	
	@Override
	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files)
	{
		// TODO
	}
	
	@Override
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws ContentProcessingException
	{
		files.add(librarySourceRoot);
	}
	
	@Override
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files)
	{
		// do nothing
	}

	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		files.add(new File(aspectRoot, CutlassConfig.RELATIVE_SRC_DIR));
	}
	
	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		// do nothing
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		files.add(new File(bladesetRoot, CutlassConfig.RELATIVE_SRC_DIR));
	}

	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		files.add(new File(bladeRoot, CutlassConfig.RELATIVE_SRC_DIR));
	}

	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		files.add(new File(workbenchRoot, CutlassConfig.RELATIVE_SRC_DIR));
	}

	@Override
	public void appendTestFiles(File testDir, List<File> files)
	{
		String srcTestDir = CutlassConfig.TESTS_DIR + File.separator + CutlassConfig.RELATIVE_SRC_TEST_DIR;
		File bladesetDir = CutlassDirectoryLocator.getParentBladeset(testDir);
		File bladeDir = CutlassDirectoryLocator.getParentBlade(testDir);
		if (bladesetDir != null && bladesetDir.exists())
		{
			files.add( new File(bladesetDir, srcTestDir));
		}
		if (bladeDir != null && bladeDir.exists())
		{
			files.add( new File(bladeDir, srcTestDir) );
		}
		else
		{
			/* slight hack for getting parent src-test dir in libraries - we need to put this logic in the model */
			/* TODO: add tests for this - its currently implicitly tested by the js library tests */
			File parentSrcTestDir = new File( testDir.getParentFile().getParentFile(), CutlassConfig.RELATIVE_SRC_TEST_DIR);
			if (parentSrcTestDir != null && parentSrcTestDir.exists())
			{
				files.add( parentSrcTestDir );
			}
		}
		files.add( new File(testDir, CutlassConfig.RELATIVE_SRC_TEST_DIR) );
	}

}
