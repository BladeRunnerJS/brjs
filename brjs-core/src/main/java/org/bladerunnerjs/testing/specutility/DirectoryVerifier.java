package org.bladerunnerjs.testing.specutility;

import static org.bladerunnerjs.testing.utility.BRJSAssertions.assertContains;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.BladerunnerConf;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;

public class DirectoryVerifier {
	private final MemoizedFile dir;
	private VerifierChainer verifierChainer;

	public DirectoryVerifier(SpecTest specTest, MemoizedFile dir) {
		this.dir = dir;
		verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer containsDir(String filePath) {
		assertTrue(new File(dir, filePath).isDirectory());
		
		return verifierChainer;
	}
	
	public VerifierChainer containsFile(String filePath) {
		File file = new File(dir, filePath);
		assertTrue("expected " + file.getPath() + " to exist, but it didnt", file.isFile());
		
		return verifierChainer;
	}
	
	public VerifierChainer doesNotContainFile(String filePath) {
		File theFile = new File(dir, filePath);
		assertFalse("expected '"+theFile.getAbsolutePath()+"' not to exist", theFile.isFile());
		
		return verifierChainer;
	}
	
	public VerifierChainer containsFileWithContents(String filePath, String contents) throws Exception {
		File file = new File(dir, filePath);
		assertTrue("file " + file.getPath() + " didn't exist.", file.exists());
		assertContains(contents, org.apache.commons.io.FileUtils.readFileToString(file, BladerunnerConf.OUTPUT_ENCODING));
		
		return verifierChainer;
	}

	public VerifierChainer sameAsFile(String filePath) throws IOException
	{
		File checkAgainstFile = new File(filePath);
		
		assertTrue( "file contents wasnt equal", org.apache.commons.io.FileUtils.contentEquals(dir, checkAgainstFile) );
		
		return verifierChainer;
	}

	public VerifierChainer isEmpty() {
		assertTrue("The directory is empty", dir.isEmpty());
		return verifierChainer;
	}

	public VerifierChainer contentsTheSameAsFile(String filePath) throws IOException
	{
		File checkAgainstFile = new File(filePath);
		
		assertTrue( "file contents wasnt equal", org.apache.commons.io.FileUtils.contentEquals(dir, checkAgainstFile) );
		
		return verifierChainer;
	}
	
}
