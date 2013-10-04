package org.bladerunnerjs.specutil.engine;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.specutil.engine.CommanderChainer;


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
}
