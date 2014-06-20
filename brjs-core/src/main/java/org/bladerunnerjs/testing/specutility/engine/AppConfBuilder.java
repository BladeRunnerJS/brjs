package org.bladerunnerjs.testing.specutility.engine;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.plugin.Locale;


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
		List<Locale> createdLocales = new ArrayList<Locale>();
		for (String locale : locales) {
			createdLocales.add( new Locale(locale) );
		}
		appConf.setLocales( createdLocales.toArray(new Locale[0]) );
		appConf.write();
		
		return builderChainer;
	}
}
