package org.bladerunnerjs.api.spec.engine;

import org.bladerunnerjs.api.BladerunnerConf;

public class BladerunnerConfBuilder {
	private BladerunnerConf bladerunnerConf;
	private BuilderChainer builderChainer;
	
	public BladerunnerConfBuilder(SpecTest specTest, BladerunnerConf bladerunnerConf) {
		this.bladerunnerConf = bladerunnerConf;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer defaultFileCharacterEncodingIs(String defaultFileCharacterEncoding) throws Exception {
		bladerunnerConf.setDefaultFileCharacterEncoding(defaultFileCharacterEncoding);
		bladerunnerConf.write();
		
		return builderChainer;
	}

	public BuilderChainer hasIgnoredPaths(String... ignoredPaths) throws Exception {
		bladerunnerConf.setIgnoredPaths(ignoredPaths);
		bladerunnerConf.write();
		
		return builderChainer;
	}
}
