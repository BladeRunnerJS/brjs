package org.bladerunnerjs.specutil;

import static org.junit.Assert.fail;

import java.io.File;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.specutil.engine.VerifierChainer;


public class AspectVerifier extends NodeVerifier<Aspect> {
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
	}
	
	public VerifierChainer bundledFilesEquals(File... file) {
		fail("the model doesn't yet support bundling!");
		
		return verifierChainer;
	}
}
