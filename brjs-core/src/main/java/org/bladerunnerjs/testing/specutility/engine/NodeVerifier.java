package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;
import org.bladerunnerjs.utility.FileUtil;
import org.bladerunnerjs.utility.JsStyleUtility;


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
		assertTrue("The directory '" + dirName + "' does not exist at: "  + node.file(dirName).getAbsolutePath(), node.file(dirName).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer doesNotHaveDir(String dirName) {
		assertFalse("The directory '" + dirName + "' exist, but shouldn't", node.file(dirName).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer hasFile(String fileName) {
		assertTrue("The file '" + fileName + "' does not exist at: " + node.file(fileName).getAbsoluteFile(), node.file(fileName).exists());
		
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
}
