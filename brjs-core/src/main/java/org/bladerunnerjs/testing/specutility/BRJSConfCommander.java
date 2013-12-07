package org.bladerunnerjs.testing.specutility;

import org.bladerunnerjs.model.BladerunnerConf;

public class BRJSConfCommander {
	private final BladerunnerConf bladerunnerConf;
	
	public BRJSConfCommander(BladerunnerConf bladerunnerConf) {
		this.bladerunnerConf = bladerunnerConf;
	}
	
	public BRJSConfCommander setJettyPort(int jettyPort) throws Exception {
		bladerunnerConf.setJettyPort(jettyPort);
		
		return this;
	}
	
	public BRJSConfCommander setDefaultInputEncoding(String defaultInputEncoding) throws Exception {
		bladerunnerConf.setDefaultInputEncoding(defaultInputEncoding);
		
		return this;
	}
	
	public void write() throws Exception {
		bladerunnerConf.write();
	}
}
