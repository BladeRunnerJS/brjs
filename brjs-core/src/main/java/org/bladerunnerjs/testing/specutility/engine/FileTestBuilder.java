package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.utility.EncodedFileUtil;

public class FileTestBuilder extends SpecTestBuilder {
	private final EncodedFileUtil fileUtil;
	private final File file;
	private final BuilderChainer builderChainer;

	public FileTestBuilder(SpecTest specTest, File file) {
		super(specTest);
		this.file = file;
		builderChainer = new BuilderChainer(specTest);
		fileUtil = new EncodedFileUtil(specTest.getActiveCharacterEncoding());
	}
	
	public BuilderChainer containsFile(String filePath) throws Exception {
		fileUtil.write(new File(file, filePath), filePath);
		
		return builderChainer;
	}
	
	public BuilderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		fileUtil.write(new File(file, filePath), fileContents);
		
		return builderChainer;
	}
	
	public BuilderChainer containsFiles(String... filePaths) throws IOException {
		for(String filePath : filePaths) {
			fileUtil.write(new File(file, filePath), filePath);
		}
		
		return builderChainer;
	}

	public void isReadOnly()
	{
		file.setReadOnly();
	}
}
