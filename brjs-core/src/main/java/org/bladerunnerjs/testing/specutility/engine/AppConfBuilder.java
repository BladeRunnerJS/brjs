package org.bladerunnerjs.testing.specutility.engine;

import org.bladerunnerjs.model.AppConf;

import com.google.common.base.Joiner;

public class AppConfBuilder extends SpecTest {
	private AppConf appConf;
	private BuilderChainer builderChainer;
	
	public AppConfBuilder(SpecTest specTest, AppConf appConf) {
		this.appConf = appConf;
		builderChainer = new BuilderChainer(specTest);
	}
	
	public BuilderChainer hasRequirePrefix(String requirePrefix) throws Exception {
		appConf.setRequirePrefix(requirePrefix);
		appConf.write();
		
		return builderChainer;
	}
	
	public BuilderChainer supportsLocales(String... locales) throws Exception {
		appConf.setLocales(Joiner.on(",").join(locales));
		appConf.write();
		
		return builderChainer;
	}
}
