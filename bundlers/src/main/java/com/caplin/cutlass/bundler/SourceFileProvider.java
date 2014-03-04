package com.caplin.cutlass.bundler;

import java.io.File;
import java.util.List;

import org.bladerunnerjs.model.exception.request.ContentProcessingException;

public interface SourceFileProvider {

	List<File> getSourceFiles(File baseDir, File testDir) throws ContentProcessingException;
	
}
