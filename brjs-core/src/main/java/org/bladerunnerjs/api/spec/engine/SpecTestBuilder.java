package org.bladerunnerjs.api.spec.engine;

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
		fileUtil = new EncodedFileUtil(specTest.brjs, specTest.getActiveCharacterEncoding());
	}
	
	public BuilderChainer activeEncodingIs(String characterEncoding) {
		specTest.setActiveCharacterEncoding(characterEncoding);
		
		return builderChainer;
	}
	
	public void writeToFile(File file, String content) throws IOException {
		writeToFile(file, content, false);
	}
	
	public void writeToFile(File file, String content, boolean append) throws IOException {
		if (specTest.brjs != null) {
			fileUtil.write(file, content, append);
		} else { // use plain old Apache commons to write if BRJS hasnt been created yet
			org.apache.commons.io.FileUtils.write(file, content, append);
		}
		
	}
	
	
}
