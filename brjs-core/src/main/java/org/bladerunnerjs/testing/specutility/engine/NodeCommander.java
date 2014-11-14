package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.plugins.bundlers.commonjs.CommonJsSourceModule;
import org.bladerunnerjs.plugin.plugins.bundlers.namespacedjs.NamespacedJsSourceModule;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.utility.EncodedFileUtil;
import org.bladerunnerjs.utility.JsStyleUtility;


public abstract class NodeCommander<N extends Node> extends ModelCommander {
	protected final CommanderChainer commanderChainer;
	private final N node;
	protected final EncodedFileUtil fileUtil;
	
	public NodeCommander(SpecTest specTest, N node) {
		super(specTest);
		this.node = node;
		fileUtil = new EncodedFileUtil(specTest.getActiveCharacterEncoding());
		commanderChainer = new CommanderChainer(specTest);
	}
	
	public CommanderChainer create() {
		call(() -> node.create());
		
		return commanderChainer;
	}
	
	public CommanderChainer populate() throws Exception {
		call(() -> ((BRJSNode) node).populate());
		
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
		fileUtil.write(node.file(filePath), fileContents);
		
		return commanderChainer;
	}
	
	public CommanderChainer hasPackageStyle(String packagePath, String jsStyle) {
		JsStyleUtility.setJsStyle(node.file(packagePath), jsStyle);
		return commanderChainer;
	}
	
	public CommanderChainer hasNamespacedJsPackageStyle(String packagePath) {
		return hasPackageStyle(packagePath, NamespacedJsSourceModule.JS_STYLE);
	}
	
	public CommanderChainer hasNamespacedJsPackageStyle() {
		return hasNamespacedJsPackageStyle("");
	}
	
	public CommanderChainer hasCommonJsPackageStyle(String packagePath) {
		return hasPackageStyle(packagePath, CommonJsSourceModule.JS_STYLE);
	}
	
	public CommanderChainer hasCommonJsPackageStyle() {
		return hasCommonJsPackageStyle("");
	}
}
