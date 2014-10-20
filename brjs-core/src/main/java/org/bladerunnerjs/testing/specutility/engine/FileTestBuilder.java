package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.bladerunnerjs.utility.EncodedFileUtil;

public class FileTestBuilder extends SpecTestBuilder {
	private final EncodedFileUtil fileUtil;
	private final File file;
	private final BuilderChainer builderChainer;
	private SpecTest specTest;

	public FileTestBuilder(SpecTest specTest, File file) {
		super(specTest);
		this.specTest = specTest;
		this.file = file;
		builderChainer = new BuilderChainer(specTest);
		fileUtil = new EncodedFileUtil(specTest.getActiveCharacterEncoding());
	}
	
	public BuilderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		File theFile = new File(file, filePath);
		fileUtil.write(theFile, fileContents);
		specTest.incrementFileVersion(theFile);
		
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
		specTest.incrementFileVersion(file);
	}
	
}
