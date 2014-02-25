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
		
		return builderChainer;
	}
	
	public BuilderChainer defaultOutputEncodingIs(String defaultOutputEncoding) throws Exception {
		bladerunnerConf.setBrowserCharacterEncoding(defaultOutputEncoding);
		
		return builderChainer;
	}
}
