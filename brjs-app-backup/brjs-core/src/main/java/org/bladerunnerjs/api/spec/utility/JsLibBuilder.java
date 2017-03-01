package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.AssetContainerBuilder;
import org.bladerunnerjs.api.spec.engine.BuilderChainer;
import org.bladerunnerjs.api.spec.engine.SpecTest;


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
