package org.bladerunnerjs.testing.specutility.engine;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;


public abstract class NodeCommander<N extends Node> extends ModelCommander {
	protected final CommanderChainer commanderChainer;
	private final N node;
	
	public NodeCommander(SpecTest modelTest, N node) {
		super(modelTest);
		this.node = node;
		commanderChainer = new CommanderChainer(modelTest);
	}
	
	public CommanderChainer create() {
		call(new Command() {
			public void call() throws Exception {
				node.create();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer populate() throws Exception {
		call(new Command() {
			public void call() throws Exception {
				((BRJSNode) node).populate();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer delete() {
		call(new Command() {
			public void call() throws Exception {
				node.delete();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer ready() {
		call(new Command() {
			public void call() throws Exception {
				node.ready();
			}
		});
		
		return commanderChainer;
	}
	
	// TODO Unable to use composition to create new private NodeBuilder instance because it's an abstract class
	public CommanderChainer containsFileWithContents(String filePath, String fileContents) throws Exception {
		FileUtils.write(node.file(filePath), fileContents, "UTF-8");
		
		return commanderChainer;
	}
}
