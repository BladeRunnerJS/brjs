package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.model.BladerunnerConf;

public class BladerunnerConfBuilder {
	private BladerunnerConf bladerunnerConf;
	private BuilderChainer builderChainer;
	
	public BladerunnerConfBuilder(SpecTest specTest, BladerunnerConf bladerunnerConf) {
		this.bladerunnerConf = bladerunnerConf;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer defaultInputEncodingIs(String defaultInputEncoding) throws Exception {
		bladerunnerConf.setDefaultInputEncoding(defaultInputEncoding);
		
		return builderChainer;
	}
}
