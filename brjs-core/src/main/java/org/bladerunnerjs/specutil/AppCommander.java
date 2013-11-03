package org.bladerunnerjs.specutil;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BladerunnerUri;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.specutil.engine.Command;
import org.bladerunnerjs.specutil.engine.CommanderChainer;
import org.bladerunnerjs.specutil.engine.NodeCommander;
import org.bladerunnerjs.specutil.engine.SpecTest;
import org.bladerunnerjs.specutil.engine.ValueCommand;


public class AppCommander extends NodeCommander<App> {
	private final App app;
	
	public AppCommander(SpecTest specTest, App app) {
		super(specTest, app);
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
	
	public void requestReceived(String requestPath, String response) throws MalformedRequestException, ResourceNotFoundException, BundlerProcessingException, UnsupportedEncodingException {
		BladerunnerUri uri = new BladerunnerUri(app.root(), app.dir(), "/app", requestPath, null);
		ByteArrayOutputStream responseOutput = new ByteArrayOutputStream();
		app.handleLogicalRequest(uri, responseOutput);
		response = responseOutput.toString("UTF-8");
	}
}
