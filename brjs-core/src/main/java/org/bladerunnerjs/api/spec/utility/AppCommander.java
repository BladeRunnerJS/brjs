package org.bladerunnerjs.api.spec.utility;

import static org.junit.Assert.assertFalse;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.spec.engine.Command;
import org.bladerunnerjs.api.spec.engine.CommanderChainer;
import org.bladerunnerjs.api.spec.engine.NodeCommander;
import org.bladerunnerjs.api.spec.engine.SpecTest;
import org.bladerunnerjs.api.spec.engine.ValueCommand;
import org.bladerunnerjs.model.StaticContentAccessor;
import org.bladerunnerjs.utility.FileUtils;

public class AppCommander extends NodeCommander<App> {
	private final App app;
	
	public AppCommander(SpecTest specTest, App app) {
		super(specTest, app);
		this.app = app;
	}
	
	public CommanderChainer populate(final String requirePrefix, final String templateGroup) {
		call(new Command() {
			public void call() throws Exception {
				app.populate(requirePrefix, templateGroup);
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
				return new AppConfCommander(specTest, app.appConf());
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
	
	public CommanderChainer requestReceived(final String requestPath, final StringBuffer response) throws MalformedRequestException, ResourceNotFoundException, ContentProcessingException, UnsupportedEncodingException {
		call(new Command() {
			public void call() throws Exception {
				ResponseContent contentOutput = app.requestHandler().handleLogicalRequest(requestPath, new StaticContentAccessor(app));
				ByteArrayOutputStream pluginContent = new ByteArrayOutputStream();
				contentOutput.write(pluginContent);
				response.append( pluginContent );
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer fileDeleted(final String filePath)
	{
		call(new Command() {
			public void call() throws Exception {
				MemoizedFile deleteFile = app.file(filePath);
				FileUtils.forceDelete( deleteFile );
				assertFalse( "failed to delete " + deleteFile.getAbsolutePath(), deleteFile.exists() );
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer fileContentsChangeTo(final String filePath, final String fileContents)
	{
		call(new Command() {
			public void call() throws Exception {
				fileUtil.write(app.file(filePath), fileContents);
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer aspectsListed()
	{
		call(new Command() {
			public void call() throws Exception {
				app.aspects();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer bladesetsListed()
	{
		call(new Command() {
			public void call() throws Exception {
				app.bladesets();
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer appConfHasBeenRead() {
		call(new Command() {
			public void call() throws Exception {
				app.appConf();
			}
		});
		
		return commanderChainer;
	}
}
