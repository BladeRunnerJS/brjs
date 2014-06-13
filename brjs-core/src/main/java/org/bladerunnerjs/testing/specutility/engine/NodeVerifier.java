package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;
import org.bladerunnerjs.utility.FileUtil;
import org.bladerunnerjs.utility.JsStyleUtility;
import org.bladerunnerjs.utility.RelativePathUtility;


public abstract class NodeVerifier<N extends Node> {
	protected final VerifierChainer verifierChainer;
	private final N node;
	private final FileUtil fileUtil;
	
	public NodeVerifier(SpecTest specTest, N node) {
		this.node = node;
		fileUtil = new FileUtil(specTest.getActiveCharacterEncoding());
		verifierChainer = new VerifierChainer(specTest);
	}
	
	public VerifierChainer isSameAs(N node) {
		assertSame(node, this.node);
		
		return verifierChainer;
	}
	
	public VerifierChainer dirExists() {
		assertTrue("The directory '" + node.dir().getName() + "' does not exist",node.dirExists());
		
		return verifierChainer;
	}
	
	public VerifierChainer dirDoesNotExist() {
		assertFalse("The directory '" + node.dir().getName() + "' exists, but shouldn't",node.dirExists());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasDir(String dirName) {
		assertTrue("The directory '" + dirName + "' does not exist at: "  + node.file(dirName).getAbsolutePath(), node.file(dirName).isDirectory());
		
		return verifierChainer;
	}
	
	public VerifierChainer doesNotHaveDir(String dirName) {
		assertFalse("The directory '" + dirName + "' exists, but shouldn't", node.file(dirName).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasFile(String fileName) {
		assertTrue("The file '" + fileName + "' does not exist at: " + node.file(fileName).getAbsoluteFile(), node.file(fileName).isFile());
		
		return verifierChainer;
	}
	
	public VerifierChainer doesNotHaveFile(String fileName) {
		assertFalse("The file '" + fileName + "' exists, but shouldn't",node.file(fileName).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer fileHasContents(String fileName, String fileContents) throws Exception {
		assertTrue("The file '" + fileName + "' did not exist at: " + node.file(fileName).getAbsoluteFile(), node.file(fileName).exists());
		assertEquals(fileContents, fileUtil.readFileToString(node.file(fileName)));
		
		return verifierChainer;
		
	}
	
	public VerifierChainer fileContentsDoesNotContain(String fileName, String fileContents) throws Exception {
		assertTrue("The file '" + fileName + "' did not exist at: " + node.file(fileName).getAbsoluteFile(), node.file(fileName).exists());
		String actualContents = fileUtil.readFileToString(node.file(fileName));
		assertFalse("Expected file not to contain " + fileContents + " but it did. Content was:\n"+actualContents, actualContents.contains(fileContents) );
		
		return verifierChainer;
		
	}
	
	public VerifierChainer fileContentsContains(String fileName, String fileContents) throws Exception {
		assertTrue("The file '" + fileName + "' did not exist at: " + node.file(fileName).getAbsoluteFile(), node.file(fileName).exists());
		String actualContents = fileUtil.readFileToString(node.file(fileName));
		assertTrue("Expected file to contain " + fileContents + " but didnt. Content was:\n"+actualContents, actualContents.contains(fileContents) );
		
		return verifierChainer;
		
	}
	
	public VerifierChainer firstFileIsLarger(String filePath1, String filePath2) {
		File file1 = node.file(filePath1);
		File file2 = node.file(filePath2);
		
		assertTrue("The file '" + file1.getPath() + "' does not exist, so can not be compared for size.", file1.exists());
		assertTrue("The file '" + file2.getPath() + "' does not exist, so can not be compared for size.", file2.exists());
		assertTrue("The file '" + file1.getPath() + "' (" + file1.length() + " bytes) was not larger than file '" + file2.getPath() + "' (" + file2.length() + " bytes).", file1.length() > file2.length());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasStorageFile(String pluginName, String filePath) {
		assertTrue("The file '" + filePath + "' does not exist at: " + node.storageFile(pluginName, filePath).getAbsoluteFile(), node.storageFile(pluginName, filePath).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer jsStyleIs(String jsStyle) {
		assertEquals(jsStyle, JsStyleUtility.getJsStyle(node.dir()));
		
		return verifierChainer;
	}
	
	public VerifierChainer containsPersistentNodeProperty(String pluginName, String propertyName, String propertyValue) throws PropertiesException
	{
		assertEquals(propertyValue, node.nodeProperties(pluginName).getPersisentProperty(propertyName));
		
		return verifierChainer;
	}
	
	public VerifierChainer containsTransientNodeProperty(String pluginName, String propertyName, String propertyValue)
	{
		assertEquals(propertyValue, node.nodeProperties(pluginName).getTransientProperty(propertyName));
		
		return verifierChainer;
	}
	
	public VerifierChainer hasFilesAndDirs(List<String> files, List<String> dirs) {
		for (String filePath : files) {
			hasFile(filePath);
		}
		for (String dirPath : dirs) {
			hasDir(dirPath);
		}
		
		Collection<File> recursivelyFoundFiles = FileUtils.listFilesAndDirs(node.dir(), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);
		recursivelyFoundFiles.remove(node.dir());
		
		List<String> fileAndDirPaths = new ArrayList<String>();
		fileAndDirPaths.addAll(dirs);
		fileAndDirPaths.addAll(files);
		
		for (File foundFile : recursivelyFoundFiles) {
			String relativePath = RelativePathUtility.get(node.dir(), foundFile);
			if (foundFile.isFile()) {
				assertFoundFileIsExpected(relativePath, fileAndDirPaths);
			} else if (foundFile.isDirectory()) {
				assertFoundFileIsExpected(relativePath, dirs);				
			}
		}
		
		return verifierChainer;
	}

	private void assertFoundFileIsExpected(String relativePath, List<String> expectedPaths)
	{
		for (String expectedPath : expectedPaths) {
			if (relativePath.startsWith(expectedPath)) {
				return;
			}
		}
		fail("found path '" + relativePath + "' that wasn't expected. Expected paths were '" + StringUtils.join(expectedPaths, "") + "'.");
	}
	
	
	
}
