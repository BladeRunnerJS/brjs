package org.bladerunnerjs.testing.specutility;

import static org.bladerunnerjs.testing.utility.BRJSAssertions.assertContains;
import static org.junit.Assert.*;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;

public class DirectoryVerifier {
	private final File dir;
	private VerifierChainer verifierChainer;

	public DirectoryVerifier(SpecTest specTest, File dir) {
		this.dir = dir;
		verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer containsFile(String filePath) {
		assertTrue(new File(dir, filePath).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer containsFileWithContents(String filePath, String contents) throws Exception {
		File file = new File(dir, filePath);
		assertTrue(file.exists());
		assertContains(contents, FileUtils.readFileToString(file));
		
		return verifierChainer;
	}
}
