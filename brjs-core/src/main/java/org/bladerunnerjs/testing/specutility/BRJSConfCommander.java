package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.BladerunnerConf;

public class BRJSConfCommander {
	private final BladerunnerConf bladerunnerConf;
	
	public BRJSConfCommander(BladerunnerConf bladerunnerConf) {
		this.bladerunnerConf = bladerunnerConf;
	}
	
	public BRJSConfCommander setJettyPort(int jettyPort) throws Exception {
		bladerunnerConf.setJettyPort(jettyPort);
		bladerunnerConf.write();
		
		return this;
	}
	
	public BRJSConfCommander setDefaultFileCharacterEncoding(String defaultFileCharacterEncoding) throws Exception {
		bladerunnerConf.setDefaultFileCharacterEncoding(defaultFileCharacterEncoding);
		bladerunnerConf.write();
		
		return this;
	}
	
	public void write() throws Exception {
		bladerunnerConf.write();
	}
}
