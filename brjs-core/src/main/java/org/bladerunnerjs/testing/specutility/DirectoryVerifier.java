package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;

import java.io.File;

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
}
