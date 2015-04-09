package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.*;

import org.bladerunnerjs.api.spec.engine.NodeVerifier;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.VerifierChainer;
import org.bladerunnerjs.model.engine.NamedNode;


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
		assertFalse(namedNode.getName() + " is a valid Node name, but it shouldn't be", namedNode.isValidName());
		
		return verifierChainer;
	}
}
