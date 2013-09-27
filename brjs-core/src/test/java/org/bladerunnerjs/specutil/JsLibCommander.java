package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class JsLibCommander extends NodeCommander<JsLib> {
	private final JsLib jsLib;

	public JsLibCommander(SpecTest modelTest, JsLib jsLib) {
		super(modelTest, jsLib);
		this.jsLib = jsLib;
	}
	
	public CommanderChainer populate(final String libraryNamespace) {
		call(new Command() {
			public void call() throws Exception {
				jsLib.populate(libraryNamespace);
			}
		});
		
		return commanderChainer;
	}
}
