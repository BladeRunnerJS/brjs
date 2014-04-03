package org.bladerunnerjs.testing.specutility;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.plugin.Event;
import org.bladerunnerjs.testing.specutility.engine.Command;
import org.bladerunnerjs.testing.specutility.engine.CommanderChainer;
import org.bladerunnerjs.testing.specutility.engine.NodeCommander;
import org.bladerunnerjs.testing.specutility.engine.SpecTest;
import org.bladerunnerjs.testing.specutility.engine.ValueCommand;
import org.bladerunnerjs.testing.specutility.logging.MockLogLevelAccessor;
import org.bladerunnerjs.utility.FileUtility;


public class BRJSCommander extends NodeCommander<BRJS> {
	private final BRJS brjs;
	
	public BRJSCommander(SpecTest modelTest, BRJS brjs) {
		super(modelTest, brjs);
		this.brjs = brjs;
	}
	
	public CommanderChainer populate() throws Exception {
		call(new Command() {
			public void call() throws Exception {
				brjs.populate();
			}
		});
		
		return commanderChainer;
	}
	
	public BRJSConfCommander bladerunnerConf() {
		BRJSConfCommander commander = call(new ValueCommand<BRJSConfCommander>() {
			public BRJSConfCommander call() throws Exception {
				return new BRJSConfCommander(brjs.bladerunnerConf());
			}
		});
		
		return commander;
	}
	
	public CommanderChainer runCommand(final String... args) throws Exception {
		call(new Command() {
			public void call() throws Exception {
				brjs.runCommand(args);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer runUserCommand(final String... args) {
		call(new Command() {
			public void call() throws Exception {
				brjs.runUserCommand(new MockLogLevelAccessor(), args);
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer discoverApps()
	{
		call(new Command() {
			public void call() throws Exception {
				brjs.apps();
			}
		});
		
		return commanderChainer;
	}

	public CommanderChainer discoverAllChildren() {
		call(new Command() {
			public void call() throws Exception {
				brjs.discoverAllChildren();
			}
		});
		
		return commanderChainer;
	}
	
	public CommanderChainer hasBeenCreated() {
		call(new Command() {
			public void call() throws Exception {
				specTest.brjs = specTest.createModel();
			}
		});
		
		return commanderChainer;
	}

	public void eventFires(Event event, Node node)
	{
		node.notifyObservers(event, node);
	}

	//TODO: find a better way to run tests that use the 'serve' command - its the only command that doesnt immediately return 
	public void runThreadedCommand(final String... args)
	{
		Thread t = new Thread( new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					runCommand(args);
				}
				catch (Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});
		t.start();
		try
		{
			Thread.sleep(2000);
		}
		catch (InterruptedException e)
		{
			throw new RuntimeException(e);
		}
	}

	public void zipFileIsExtractedTo(String pathToZip, String pathToExtractZip) throws ZipException, IOException 
	{
		ZipFile zipFile = new ZipFile(new File(brjs.dir(), pathToZip));
		File unzippedContentFolder = new File(brjs.dir(), pathToExtractZip);
		
		unzippedContentFolder.mkdirs();
		
		FileUtility.unzip(zipFile, unzippedContentFolder);		
	}
}
