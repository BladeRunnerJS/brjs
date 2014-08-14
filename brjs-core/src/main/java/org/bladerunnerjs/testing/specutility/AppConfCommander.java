package org.bladerunnerjs.testing.specutility;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.ModelCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;


public class AppConfCommander extends ModelCommander {
	private final AppConf appConf;
	
	public AppConfCommander(SpecTest modelTest, AppConf appConf) {
		super(modelTest);
		this.appConf = appConf;
	}
	
	public AppConfCommander setAppNamespace(final String requirePrefix) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				appConf.setRequirePrefix(requirePrefix);
				appConf.write();
			}
		});
		
		return this;
	}
	
	public AppConfCommander setLocales(final String locales) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				List<Locale> createdLocales = new ArrayList<Locale>();
				for (String locale : locales.split(",")) {
					createdLocales.add( new Locale(locale) );
				}
				appConf.setLocales( createdLocales.toArray(new Locale[0]) );
				appConf.write();
			}
		});
		
		return this;
	}
	
	public void write() throws Exception {
		appConf.write();
	}
}
