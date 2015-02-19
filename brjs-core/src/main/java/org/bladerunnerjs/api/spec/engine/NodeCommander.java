package org.bladerunnerjs.api.spec.engine;

import java.io.File;
import java.io.IOException;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.utility.EncodedFileUtil;


public abstract class NodeCommander<N extends Node> extends ModelCommander {
	protected final CommanderChainer commanderChainer;
	private final N node;
	protected final EncodedFileUtil fileUtil;
	
	public NodeCommander(SpecTest specTest, N node) {
		super(specTest);
		this.node = node;
		fileUtil = new EncodedFileUtil(specTest.brjs, specTest.getActiveCharacterEncoding());
		commanderChainer = new CommanderChainer(specTest);
	}
	
	public CommanderChainer create() {
		call(() -> node.create());
		
		return commanderChainer;
	}
	
	public CommanderChainer populate(String templateGroup) throws Exception {
		call(() -> ((BRJSNode) node).populate(templateGroup));
		
		return commanderChainer;
	}
	
	public CommanderChainer delete() {
		call(() -> node.delete());
		
		return commanderChainer;
	}
	
	public CommanderChainer ready() {
		call(() -> node.ready());
		
		return commanderChainer;
	}
	
	
	// TODO Unable to use composition to create new private NodeBuilder instance because it's an abstract class
	public CommanderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		File theFile = node.file(filePath);
		writeToFile(theFile, fileContents);
		
		return commanderChainer;
	}
	
	public CommanderChainer hasPackageStyle(String packagePath, String jsStyle) {
		((BRJS) node.root()).jsStyleAccessor().setJsStyle(node.file(packagePath), jsStyle);
		return commanderChainer;
	}
	
	public CommanderChainer hasNamespacedJsPackageStyle(String packagePath) {
		return hasPackageStyle(packagePath, SpecTest.NAMESPACED_JS_STYLE);
	}
	
	public CommanderChainer hasNamespacedJsPackageStyle() {
		return hasNamespacedJsPackageStyle("");
	}
	
	public CommanderChainer hasCommonJsPackageStyle(String packagePath) {
		return hasPackageStyle(packagePath, SpecTest.COMMON_JS_STYLE);
	}
	
	public CommanderChainer hasCommonJsPackageStyle() {
		return hasCommonJsPackageStyle("");
	}
	
	public void writeToFile(File file, String content) throws IOException {
		writeToFile(file, content, false);
	}
	
	public void writeToFile(File file, String content, boolean append) throws IOException {
		fileUtil.write(file, content, append);
		specTest.brjs.getFileModificationRegistry().incrementFileVersion(file);
	}
}
