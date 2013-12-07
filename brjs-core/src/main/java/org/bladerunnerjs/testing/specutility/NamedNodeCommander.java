package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


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
