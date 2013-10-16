package com.caplin.jstestdriver.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.bladerunnerjs.model.utility.FileUtility;
import com.google.jstestdriver.FileInfo;
import com.google.jstestdriver.hooks.ResourcePreProcessor;

public class DummyBundleInjector implements ResourcePreProcessor {

	@Override
	public List<FileInfo> processDependencies(List<FileInfo> files) {
		System.out.println("DummyBundleInjector.processDependencies:");
		addExtraFiles(files, "src");
		return files;
	}

	@Override
	public List<FileInfo> processPlugins(List<FileInfo> files) {
		return files;
	}

	@Override
	public List<FileInfo> processTests(List<FileInfo> files) {
		System.out.println("DummyBundleInjector.processTests:");
		addExtraFiles(files, "test");
		return files;
	}
	
	private void addExtraFiles(List<FileInfo> files, String prefix) {
		List<File> extraSrcFiles;
		try {
			extraSrcFiles = Arrays.asList( FileUtility.createTemporaryFile(prefix+"1", ".js"), FileUtility.createTemporaryFile(prefix+"2", ".js"));
		} catch (IOException e) {
			throw new RuntimeException("Error creating temporary files");
		}
		for (File f : extraSrcFiles) {
			System.out.println("Adding file: " + f.getAbsolutePath());
			files.add(new FileInfo(f.getAbsolutePath(), -1, -1, false, false, null, ""));
		}
	}
	
}
