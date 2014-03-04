package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


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
