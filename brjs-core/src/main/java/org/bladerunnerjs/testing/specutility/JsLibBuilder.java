package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.testing.specutility.engine.AssetContainerBuilder;
import org.bladerunnerjs.testing.specutility.engine.BuilderChainer;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class JsLibBuilder extends AssetContainerBuilder<JsLib> {
	private final JsLib jsLib;
	
	public JsLibBuilder(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
	}
	
	public BuilderChainer hasBeenPopulated() throws Exception {
		jsLib.populate("lib");
		
		return builderChainer;
	}
	
	public BuilderChainer containsPackageJsonWithMainSourceModule(String string) {
		// TODO Auto-generated method stub
		return builderChainer;
	}
}
