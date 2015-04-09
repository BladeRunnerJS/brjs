package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.api.spec.engine.NodeCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.engine.NamedNode;


public class NamedNodeCommander extends NodeCommander<NamedNode> {
	private final NamedNode namedNode;
	
	public NamedNodeCommander(SpecTest modelTest, NamedNode namedNode) {
		super(modelTest, namedNode);
		this.namedNode = namedNode;
	}
	
	public CommanderChainer populate(String templateGroup) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				((BRJSNode) namedNode).populate(templateGroup);
			}
		});
		
		return commanderChainer;
	}
	
}
