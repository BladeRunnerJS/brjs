package org.bladerunnerjs.testing.specutility;

import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.Reader;
import java.io.UnsupportedEncodingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.ContentPluginUtility;
import org.bladerunnerjs.model.StaticContentPluginUtility;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.ValueCommand;

public class AppCommander extends NodeCommander<App> {
	private final App app;
	
	public AppCommander(SpecTest specTest, App app) {
		super(specTest, app);
		this.app = app;
	}
	
	public CommanderChainer populate(final String requirePrefix) {
		call(new Command() {
			public void call() throws Exception {
				app.populate(requirePrefix);
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
				ContentPluginUtility contentOutputStream = new StaticContentPluginUtility(app);
				Reader contentOutput = app.handleLogicalRequest(requestPath, contentOutputStream);
				response.append( IOUtils.toString(contentOutput) );
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer fileDeleted(final String filePath)
	{
		call(new Command() {
			public void call() throws Exception {
				File deleteFile = app.file(filePath);
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
}
