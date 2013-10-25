package org.bladerunnerjs.specutil;

import static org.junit.Assert.*;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class JsLibVerifier extends NodeVerifier<JsLib> {
	private final JsLib jsLib;

	public JsLibVerifier(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
	}
	
	public void nameIsValid() {
		assertTrue(jsLib.getName() + " is not a valid name" , jsLib.isValidName());
	}
	
	public void nameIsInvalid() {
		assertFalse(jsLib.getName() + " is a valid name, but shouldn't be" , jsLib.isValidName());
	}
}
