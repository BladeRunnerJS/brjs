package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.exception.request.BundlerProcessingException;


public interface BladeRunnerFileAppender {

	public void appendThirdPartyLibraryFiles(File appRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendLibrarySourceFiles(File librarySourceRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendLibraryResourceFiles(File libraryResourcesRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendAppAspectFiles(File aspectRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendWorkbenchAspectFiles(File aspectRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendBladesetFiles(File bladesetRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendBladeFiles(File bladeRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendWorkbenchFiles(File workbenchRoot, List<File> files) throws BundlerProcessingException;
	
	public void appendTestFiles(File testDir, List<File> files) throws BundlerProcessingException;
	
}
