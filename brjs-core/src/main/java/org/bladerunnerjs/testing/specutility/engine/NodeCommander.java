package org.bladerunnerjs.testing.specutility.engine;

import java.util.Map;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.utility.FileUtil;


public abstract class NodeCommander<N extends Node> extends ModelCommander {
	protected final CommanderChainer commanderChainer;
	private final N node;
	protected final FileUtil fileUtil;
	
	public NodeCommander(SpecTest specTest, N node) {
		super(specTest);
		this.node = node;
		fileUtil = new FileUtil(specTest.getActiveCharacterEncoding());
		commanderChainer = new CommanderChainer(specTest);
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
	
	public CommanderChainer populate(final Map<String, String> transformations) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				((BRJSNode) node).populate(transformations);
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
		fileUtil.write(node.file(filePath), fileContents);
		
		return commanderChainer;
	}
}
