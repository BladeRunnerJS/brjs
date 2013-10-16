package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class NamedNodeCommander extends NodeCommander<NamedNode> {
	private final NamedNode namedNode;
	
	public NamedNodeCommander(SpecTest modelTest, NamedNode namedNode) {
		super(modelTest, namedNode);
		this.namedNode = namedNode;
	}
	
	public CommanderChainer populate() throws Exception {
		call(new Command() {
			public void call() throws Exception {
				((BRJSNode) namedNode).populate();
			}
		});
		
		return commanderChainer;
	}
}
