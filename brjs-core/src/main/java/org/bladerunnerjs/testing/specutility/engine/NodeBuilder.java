package org.bladerunnerjs.testing.specutility.engine;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;


public abstract class NodeBuilder<N extends Node> {
	protected BuilderChainer builderChainer;
	protected SpecTest specTest;
	protected N node;
	
	public NodeBuilder(SpecTest specTest, N node) {
		this.specTest = specTest;
		this.node = node;
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
		FileUtils.write(node.file(filePath), "");
		
		return builderChainer;
	}
	
	public BuilderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		FileUtils.write(node.file(filePath), fileContents);
		
		return builderChainer;
	}
	
	public BuilderChainer containsStorageFile(String pluginName, String filePath) throws Exception {
		FileUtils.write(node.storageFile(pluginName, filePath), "");
		
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
}
