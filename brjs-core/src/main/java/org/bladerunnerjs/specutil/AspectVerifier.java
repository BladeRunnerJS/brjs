package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AspectVerifier extends NodeVerifier<Aspect> {
	public AspectVerifier(SpecTest modelTest, Aspect aspect) {
		super(modelTest, aspect);
	}
}
