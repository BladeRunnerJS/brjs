package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.PropertiesException;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.utility.EncodedFileUtil;


public abstract class NodeBuilder<N extends Node> {
	protected BuilderChainer builderChainer;
	protected SpecTest specTest;
	protected N node;
	private EncodedFileUtil fileUtil;
	
	public NodeBuilder(SpecTest specTest, N node) {
		this.specTest = specTest;
		this.node = node;
		fileUtil = new EncodedFileUtil(specTest.getActiveCharacterEncoding());
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer hasBeenCreated() throws Exception {
		node.create();
		
		return builderChainer;
	}
	
	public BuilderChainer hasBeenPopulated() throws Exception {
		((BRJSNode) node).populate();
		
		return builderChainer;
	}
	
	public BuilderChainer containsFolder(String directoryName) throws Exception {
		FileUtils.forceMkdir(new File(node.dir(), directoryName));
		
		return builderChainer;
	}
	
	public BuilderChainer containsFile(String filePath) throws Exception {
		fileUtil.write(node.file(filePath), filePath + "\n");
		
		return builderChainer;
	}
	
	public BuilderChainer containsFiles(String... filePaths) throws Exception {
		for(String filePath : filePaths) {
			containsFile(filePath);
		}
		
		return builderChainer;
	}
	
	public BuilderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		fileUtil.write(node.file(filePath), fileContents);
		
		return builderChainer;
	}
	
	public BuilderChainer containsBinaryFileWithContents(String filePath, byte... binaryData) throws Exception {
		File outputFile = node.file(filePath);
		outputFile.getParentFile().mkdirs();
		outputFile.createNewFile();
		IOUtils.write( binaryData, new FileOutputStream(outputFile) );
		
		return builderChainer;
	}
	
	public BuilderChainer containsEmptyFile(String filePath) throws Exception {
		containsFileWithContents(filePath, "");
		
		return builderChainer;
	}
	
	public BuilderChainer containsStorageFile(String pluginName, String filePath) throws Exception {
		fileUtil.write(node.storageFile(pluginName, filePath), "");
		
		return builderChainer;
	}
	
	public BuilderChainer containsLockedStorageFile(String pluginName, String filePath) throws Exception {
		containsStorageFile(pluginName, filePath);
		node.storageFile(pluginName, filePath).setWritable(false);
		
		return builderChainer;
	}
	
	public BuilderChainer hasDir(String filePath)
	{
		node.file(filePath).mkdirs();
		
		return builderChainer;
	}
	
	public BuilderChainer hasPersistentNodeProperty(String pluginName, String propertyName, String propertyValue) throws PropertiesException
	{
		node.nodeProperties(pluginName).setPersisentProperty(propertyName, propertyValue);
		
		return builderChainer;
	}
	
	public BuilderChainer hasTransientNodeProperty(String pluginName, String propertyName, String propertyValue)
	{
		node.nodeProperties(pluginName).setTransientProperty(propertyName, propertyValue);
		
		return builderChainer;
	}
}
