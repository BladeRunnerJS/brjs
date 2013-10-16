package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.AppConf;
import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.ModelCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;


public class AppConfCommander extends ModelCommander {
	private final AppConf appConf;
	
	public AppConfCommander(SpecTest modelTest, AppConf appConf) {
		super(modelTest);
		this.appConf = appConf;
	}
	
	public AppConfCommander setAppNamespace(final String appNamespace) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				appConf.setAppNamespace(appNamespace);
			}
		});
		
		return this;
	}
	
	public AppConfCommander setLocales(final String locales) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				appConf.setLocales(locales);
			}
		});
		
		return this;
	}
	
	public void write() throws Exception {
		appConf.write();
	}
}
