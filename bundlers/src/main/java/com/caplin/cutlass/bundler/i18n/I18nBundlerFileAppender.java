package com.caplin.cutlass.bundler.i18n;

import static com.caplin.cutlass.bundler.ResourceAdder.*;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.bundler.BladeRunnerFileAppender;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public class I18nBundlerFileAppender implements BladeRunnerFileAppender {

	private static final String RESOURCES_I18N_FOLDER = "resources/i18n";
	
	@Override
	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files)
	{
		// do nothing
	}
	
	@Override
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws ContentProcessingException
	{
		// do nothing
	}
	
	@Override
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files)
	{
		if(libraryResourcesRoot.exists())
		{
			List<File> propertiesFiles = Arrays.asList(libraryResourcesRoot.listFiles((FilenameFilter) new SuffixFileFilter(".properties")));
			Collections.sort(propertiesFiles);
			
			files.addAll(propertiesFiles);
		}
	}

	@Override
	public void appendAppAspectFiles(File aspectRoot, List<File> files)
	{
		appendResourceDirectory(aspectRoot, RESOURCES_I18N_FOLDER, files);
	}

	@Override
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files)
	{
		appendResourceDirectory(aspectRoot, RESOURCES_I18N_FOLDER, files);
	}
	
	@Override
	public void appendBladesetFiles(File bladesetRoot, List<File> files)
	{
		appendResourceDirectory(bladesetRoot, RESOURCES_I18N_FOLDER, files);
	}

	@Override
	public void appendBladeFiles(File bladeRoot, List<File> files)
	{
		appendResourceDirectory(bladeRoot, RESOURCES_I18N_FOLDER, files);
	}

	@Override
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files)
	{
		appendResourceDirectory(workbenchRoot, RESOURCES_I18N_FOLDER, files);
	}

	@Override
	public void appendTestFiles(File testDir, List<File> files) {}
}
