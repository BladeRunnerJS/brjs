package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.model.BladerunnerConf;

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

	public BuilderChainer hasFileObserverValue(String value) throws Exception
	{
		bladerunnerConf.setFileObserverValue(value);
		bladerunnerConf.write();
		
		return builderChainer;
	}
}
