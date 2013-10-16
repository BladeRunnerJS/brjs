package org.bladerunnerjs.core.plugin.bundler;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

import org.bladerunnerjs.core.plugin.Plugin;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.sinbin.AppMetaData;


public interface LegacyFileBundlerPlugin extends Plugin
{
	public String getBundlerExtension();
	
	public List<String> getValidRequestForms();
	
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException;
	
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws RequestHandlingException;
	
	public List<String> getValidRequestStrings(AppMetaData appMetaData) throws RequestHandlingException;
}
