package org.bladerunnerjs.api.spec.utility;

import java.util.ArrayList;
import java.util.List;

import org.bladerunnerjs.api.AppConf;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.api.spec.engine.ModelCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;


public class AppConfCommander extends ModelCommander {
	private final AppConf appConf;
	private CommanderChainer commanderChainer;
	
	public AppConfCommander(SpecTest modelTest, AppConf appConf) {
		super(modelTest);
		this.appConf = appConf;
		commanderChainer = new CommanderChainer(modelTest);
	}
	
	public AppConfCommander setRequirePrefix(final String requirePrefix) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				appConf.setRequirePrefix(requirePrefix);
				appConf.write();
			}
		});
		
		return this;
	}
	
	public CommanderChainer localesUpdatedTo(final String... locales) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				List<Locale> createdLocales = new ArrayList<Locale>();
				for (String locale : locales) {
					createdLocales.add( new Locale(locale) );
				}
				appConf.setLocales( createdLocales.toArray(new Locale[0]) );
				appConf.write();
			}
		});
		
		return commanderChainer;
	}
	
	public void write() throws Exception {
		appConf.write();
	}
}
