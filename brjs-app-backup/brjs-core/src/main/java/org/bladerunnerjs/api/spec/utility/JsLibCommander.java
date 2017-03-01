package org.bladerunnerjs.api.spec.utility;

import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.api.spec.engine.NodeCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class JsLibCommander extends NodeCommander<JsLib> {
	private final JsLib jsLib;

	public JsLibCommander(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
	}
	
	public CommanderChainer populate(final String libraryNamespace, final String templateGroup) {
		call(new Command() {
			public void call() throws Exception {
				jsLib.populate(libraryNamespace, templateGroup);
			}
		});
		
		return commanderChainer;
	}
}
