package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.testing.specutility.engine.NodeVerifier;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.VerifierChainer;


public class NamedNodeVerifier extends NodeVerifier<NamedNode> {
	private final NamedNode namedNode;
	
	public NamedNodeVerifier(SpecTest modelTest, NamedNode namedNode) {
		super(modelTest, namedNode);
		this.namedNode = namedNode;
	}
	
	public VerifierChainer nameIsValid() {
		assertTrue(namedNode.getName() + " is not a valid Node name", namedNode.isValidName());
		
		return verifierChainer;
	}
	
	public VerifierChainer nameIsInvalid() {
		assertFalse(namedNode.getName() + " is a valid Node name, but it shouldn't", namedNode.isValidName());
		
		return verifierChainer;
	}
}
