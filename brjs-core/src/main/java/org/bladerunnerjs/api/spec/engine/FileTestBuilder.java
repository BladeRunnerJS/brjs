package org.bladerunnerjs.api.spec.engine;

import java.io.File;

import org.bladerunnerjs.api.memoization.MemoizedFile;

public class FileTestBuilder extends SpecTestBuilder {
	
	private final File file;
	private final BuilderChainer builderChainer;
	private SpecTest specTest;

	public FileTestBuilder(SpecTest specTest, MemoizedFile file) {
		this(specTest, file.getUnderlyingFile());
	}
	
	public FileTestBuilder(SpecTest specTest, File file) {
		super(specTest);
		this.specTest = specTest;
		this.file = file;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		File theFile = new File(file, filePath);
		writeToFile(theFile, fileContents);
		
		return builderChainer;
	}
	
	public BuilderChainer containsFile(String filePath) throws Exception {
		return containsFileWithContents(filePath, filePath);
	}
	
	public BuilderChainer containsFiles(String... filePaths) throws Exception {
		for(String filePath : filePaths) {
			containsFile(filePath);
		}
		
		return builderChainer;
	}

	public void isReadOnly()
	{
		file.setReadOnly();
		specTest.brjs.getFileModificationRegistry().incrementFileVersion(file);
	}
	
}
