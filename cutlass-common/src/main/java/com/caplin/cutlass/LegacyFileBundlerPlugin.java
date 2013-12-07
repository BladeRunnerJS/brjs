package com.caplin.cutlass;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.plugin.Plugin;


public interface LegacyFileBundlerPlugin extends Plugin
{
	public String getBundlerExtension();
	
	public List<String> getValidRequestForms();
	
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException;
	
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws RequestHandlingException;
	
	public List<String> getValidRequestStrings(AppMetaData appMetaData) throws RequestHandlingException;
}
