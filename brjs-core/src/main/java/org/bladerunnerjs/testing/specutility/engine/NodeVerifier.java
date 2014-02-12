package org.bladerunnerjs.testing.specutility.engine;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;
import org.bladerunnerjs.utility.FileUtil;
import org.bladerunnerjs.utility.JsStyleUtility;


public abstract class NodeVerifier<N extends Node> {
	protected final VerifierChainer verifierChainer;
	private final N node;
	private final FileUtil fileUtil;
	
	public NodeVerifier(SpecTest specTest, N node) {
		this.node = node;
		fileUtil = new FileUtil("UTF-8");
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
	
	public VerifierChainer hasStorageFile(String pluginName, String filePath) {
		assertTrue("The file '" + filePath + "' does not exist at: " + node.storageFile(pluginName, filePath).getAbsoluteFile(), node.storageFile(pluginName, filePath).exists());
		
		return verifierChainer;
	}
	
	public VerifierChainer jsStyleIs(String jsStyle) {
		assertEquals(jsStyle, JsStyleUtility.getJsStyle(node.dir()));
		
		return verifierChainer;
	}
}
