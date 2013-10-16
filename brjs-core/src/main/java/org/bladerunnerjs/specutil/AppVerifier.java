package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.specutil.engine.NodeVerifier;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AppVerifier extends NodeVerifier<App> {
	public AppVerifier(SpecTest modelTest, App app) {
		super(modelTest, app);
	}
}
