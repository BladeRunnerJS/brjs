package org.bladerunnerjs.testing.specutility.engine;

public class SpecTestBuilder {
	private BuilderChainer builderChainer;
	private SpecTest specTest;
	
	public SpecTestBuilder(SpecTest specTest) {
		this.specTest = specTest;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer activeEncodingIs(String characterEncoding) {
		specTest.setActiveCharacterEncoding(characterEncoding);
		
		return builderChainer;
	}
}
