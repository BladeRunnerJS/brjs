package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.utility.EncodedFileUtil;

public class SpecTestBuilder {
	private BuilderChainer builderChainer;
	private SpecTest specTest;
	private EncodedFileUtil fileUtil;
	
	public SpecTestBuilder(SpecTest specTest) {
		this.specTest = specTest;
		builderChainer = new BuilderChainer(specTest);
		fileUtil = new EncodedFileUtil(specTest.getActiveCharacterEncoding());
	}
	
	public BuilderChainer activeEncodingIs(String characterEncoding) {
		specTest.setActiveCharacterEncoding(characterEncoding);
		
		return builderChainer;
	}
	
	public void writeToFile(File file, String content) throws IOException {
		writeToFile(file, content, false);
	}
	
	public void writeToFile(File file, String content, boolean append) throws IOException {
		fileUtil.write(file, content, append);
		if (specTest.brjs != null) specTest.brjs.getFileModificationRegistry().incrementFileVersion(file);
	}
	
	
}
