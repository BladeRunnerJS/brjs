package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.AssetContainerBuilder;
import org.bladerunnerjs.specutil.engine.BuilderChainer;
import org.bladerunnerjs.specutil.engine.SpecTest;


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
}
