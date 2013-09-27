package org.bladerunnerjs.specutil;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.specutil.engine.ValueCommand;


public class AppCommander extends NodeCommander<App> {
	private final App app;

	public AppCommander(SpecTest modelTest, App app) {
		super(modelTest, app);
		this.app = app;
	}
	
	public CommanderChainer populate(final String appNamespace) {
		call(new Command() {
			public void call() throws Exception {
				app.populate(appNamespace);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer deployApp() {
		call(new Command() {
			public void call() throws Exception {
				app.deploy();
			}
		});
		
		return commanderChainer;
	}
	
	public AppConfCommander appConf() throws Exception {
		AppConfCommander commander = call(new ValueCommand<AppConfCommander>() {
			public AppConfCommander call() throws Exception {
				return new AppConfCommander(modelTest, app.appConf());
			}
		});
		
		return commander;
	}

	public CommanderChainer fileCreated(final String filePath)
	{
		call(new Command() {
			public void call() throws Exception {
				app.file(filePath).createNewFile();
			}
		});
		
		return commanderChainer;
	}
}
