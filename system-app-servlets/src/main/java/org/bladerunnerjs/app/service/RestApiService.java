package org.bladerunnerjs.app.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.plugin.CommandPlugin;
import org.bladerunnerjs.legacy.command.test.TestCommand;
import org.bladerunnerjs.legacy.command.test.testrunner.TestRunnerController;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.events.CommandExecutedEvent;
import org.bladerunnerjs.plugin.commands.standard.CopyBladesetCommand;
import org.bladerunnerjs.plugin.commands.standard.CreateAppCommand;
import org.bladerunnerjs.plugin.commands.standard.CreateBladeCommand;
import org.bladerunnerjs.plugin.commands.standard.CreateBladesetCommand;
import org.bladerunnerjs.plugin.commands.standard.ExportApplicationCommand;
import org.bladerunnerjs.plugin.commands.standard.ImportAppCommand;
import org.bladerunnerjs.plugin.commands.standard.JsDocCommand;
import org.bladerunnerjs.utility.FileUtils;

public class RestApiService
{
	private static final String JS_TEST_REPORT_SWITCH = "--" + TestRunnerController.REPORT_SWITCH;
	public static final String IMPORT_BLADESETS_NEWBLADESET_NAME_KEY = "newBladesetName";
	public static final String IMPORT_BLADESETS_BLADES_KEY = "blades";
	private static final String APPS_DIR = "apps";
	private BRJS brjs;
	
	private Logger logger;
	
	
	//TODO: rather than instantiating specific commands we should use brjs to 'find' commands so they can easily be replaced
	
	public RestApiService(BRJS brjs)
	{
		this.brjs = brjs;
		logger = brjs.logger(RestApiService.class);
	}
	
	public String getApps()
	{
		StringBuilder response = new StringBuilder();
		response.append("[");
		
		List<App> applications = brjs.userApps();
		response.append(joinListOfNodes( new ArrayList<NamedNode>(applications),", "));
		
		response.append("]");
		return response.toString();
	}
	
	public String getApp(String appName) throws Exception
	{
		StringBuilder response = new StringBuilder();
		response.append("{");
		
		App app = brjs.userApp(appName);
		if (!app.dirExists())
		{
			throw new Exception("App " + app.getName() + " does not exist");
		}
		
		List<Bladeset> bladesets = app.bladesets();
		for (int i = 0; i < bladesets.size(); i++)
		{
			Bladeset bladeset = bladesets.get(i);
			response.append("\""+bladeset.getName()+"\"" + ":[");
			List<Blade> blades = bladeset.blades();
			response.append(joinListOfNodes( new ArrayList<NamedNode>(blades),", " ));
			response.append("]");
			if (i < bladesets.size()-1)
			{
				response.append(", ");
			}
		}
				
		response.append("}");
		return response.toString();
	}
	
	public InputStream getAppImageInputStream(String app) throws Exception
	{
		File appImage = getAppImageLocation(app);
		if (appImage != null)
		{
			logger.debug("using app image " + appImage.getPath());
			return new FileInputStream(appImage);
		} 
		else 
		{
			logger.debug("using default app image");
			return this.getClass().getClassLoader().getResourceAsStream("images/default-thumb.png");
		}
	}
	
	public MemoizedFile getAppImageLocation(String app) throws Exception
	{
		MemoizedFile appImage = brjs.userApp(app).file("thumb.png"); 
		if (appImage.exists())
		{
			return appImage;
		} 
		else 
		{
			return null;
		}
	}
	
	public void importMotif(String appName, String requirePrefix, File appZip) throws Exception
	{
		ImportAppCommand cmd = new ImportAppCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appZip.getAbsolutePath(), appName, requirePrefix };		
		doCommand( cmd, args );
		
		brjs.app(appName).incrementFileVersion();
	}
	
	public void exportWar(String appName, File destinationWar) throws Exception
	{
		if (destinationWar.exists())
		{
			destinationWar.delete();
		}
		
		App app = brjs.userApp(appName);
		if (!app.dirExists()) {
			throw new Exception("Unable to export, the app '" + appName + "' doesn't exist.");
		}
		
		notifyOfCommand(new ExportApplicationCommand());
		app.buildWar( brjs.getMemoizedFile(destinationWar) );
	}
	
	public void importBladeset(String sourceApp, Map<String,Map<String,List<String>>> bladesets, String targetApp) throws Exception
	{
		for (String bladeset : bladesets.keySet())
		{
			Map<String,List<String>> bladesetMap = bladesets.get(bladeset);
			
			String newBladesetName = bladesetMap.get(IMPORT_BLADESETS_NEWBLADESET_NAME_KEY).get(0);
			List<String> blades = bladesetMap.get(IMPORT_BLADESETS_BLADES_KEY);
			
			CopyBladesetCommand cmd = new CopyBladesetCommand();
			cmd.setBRJS(brjs);
			String[] args = new String[]{ sourceApp, bladeset, targetApp, newBladesetName };		
			doCommand( cmd, args );
			
			Bladeset bladesetNode = brjs.app(targetApp).bladeset(newBladesetName);
			for (Blade bladeNode : bladesetNode.blades())
			{
				if (!blades.contains(bladeNode.getName()))
				{						
					FileUtils.deleteDirectory(bladeNode.dir());
				}
				
				bladeNode.incrementFileVersion();
			}
		}
	}
	
	public void createApp(String appName, String requirePrefix) throws Exception
	{
		CreateAppCommand cmd = new CreateAppCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appName, requirePrefix };		
		doCommand( cmd, args );
		
		brjs.app(appName).incrementFileVersion();
	}
	
	public void createBladeset(String appName, String bladesetName) throws Exception
	{
		CreateBladesetCommand cmd = new CreateBladesetCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appName, bladesetName };		
		doCommand( cmd, args );
		
		brjs.app(appName).bladeset(bladesetName).incrementFileVersion();
	}
	
	public void createBlade(String appName, String bladesetName, String bladeName) throws Exception
	{
		CreateBladeCommand cmd = new CreateBladeCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appName, bladesetName, bladeName };		
		doCommand( cmd, args );
		
		brjs.app(appName).bladeset(bladesetName).blade(bladeName).incrementFileVersion();
	}
	
	public String runBladesetTests(String appName, String bladesetName, String testType) throws Exception
	{
		TestCommand cmd = new TestCommand();
		cmd.setBRJS(brjs);
		String bladesetPath;
		if (isDefaultBladeset(brjs.app(appName).bladeset(bladesetName)))
		{
			bladesetPath = brjs.app(appName).bladeset(bladesetName).dir().getAbsolutePath() + File.separator + Bladeset.BLADES_DIRNAME;
		}
		else 
		{
			bladesetPath = brjs.app(appName).bladeset(bladesetName).dir().getAbsolutePath();
		}
		String[] args = new String[]{ bladesetPath, testType, JS_TEST_REPORT_SWITCH };	
		OutputStream out = doCommand( cmd, args );
		return out.toString();
	}
	
	private boolean isDefaultBladeset(Bladeset bladeset) {
		int indexOfApps = bladeset.dir().getAbsolutePath().indexOf(APPS_DIR + File.separator);
		String pathAfterApps = bladeset.dir().getAbsolutePath().substring(indexOfApps + (APPS_DIR + File.separator).length());
		if (pathAfterApps.contains(File.separator))
			return false;
		return true;
	}
	
	public String runBladeTests(String appName, String bladesetName, String bladeName, String testType) throws Exception
	{
		TestCommand cmd = new TestCommand();
		cmd.setBRJS(brjs);
		String bladePath = brjs.app(appName).bladeset(bladesetName).blade(bladeName).dir().getAbsolutePath();
		String[] args = new String[]{ bladePath, testType, JS_TEST_REPORT_SWITCH };
		OutputStream out = doCommand( cmd, args );
		return out.toString();
	}
	
	public String getCurrentReleaseNotes() throws Exception
	{
		File latestReleaseNote = getLatestReleaseNoteFile();
		if (latestReleaseNote != null)
		{
			return org.apache.commons.io.FileUtils.readFileToString(latestReleaseNote);			
		}
		throw new Exception("Unable to find latest release note.");
	}
	
	public String getSdkVersion() throws IOException
	{
		MemoizedFile versionFile = brjs.versionInfo().getFile();
		return org.apache.commons.io.FileUtils.readFileToString(versionFile);
	}
	
	public void getJsdocForApp(String appName) throws Exception {
		JsDocCommand jsDocCommand = new JsDocCommand();
		jsDocCommand.setBRJS(brjs);
		String[] args = new String[]{appName, "-v"};
		//TODO: should this be something that wraps stdOut?
		doCommand(jsDocCommand, args);
	}

	/* helper methods */
	
	private String joinListOfNodes(List<NamedNode> nodes, String seperator)
	{
		StringBuilder ret = new StringBuilder();
		for (int i = 0; i < nodes.size(); i++)
		{
			ret.append( "\""+nodes.get(i).getName()+"\"" );
			if (i < nodes.size()-1)
			{
				ret.append(", ");
			}
		}
		return ret.toString();
	}
	
	private OutputStream doCommand(CommandPlugin command, String[] args) throws Exception
	{	
		ByteArrayOutputStream commandOutput = new ByteArrayOutputStream();
		
		PrintStream oldSysOut = System.out;
		System.setOut( new MultiOutputPrintStream(System.out, new PrintStream(commandOutput)) );
		
		try {
			notifyOfCommand(command);
			command.doCommand(args);
		} finally {
			System.setOut(oldSysOut);
		}
		
		return commandOutput;
	}
	
	private MemoizedFile getLatestReleaseNoteFile() 
	{
		return brjs.root().file("sdk/docs/release-notes/latest.html");
	}

	
	private void notifyOfCommand(CommandPlugin command) {
		brjs.notifyObservers(new CommandExecutedEvent("dashboard", command.getCommandName()), brjs);
	}
	
	private class MultiOutputPrintStream extends PrintStream {
		private PrintStream secondary;

		MultiOutputPrintStream(PrintStream primary, PrintStream secondary) {
			super(primary);
			this.secondary = secondary;
		}
		public void write(byte buf[], int off, int len) {
			super.write(buf, off, len);
			secondary.write(buf, off, len);
		}
		public void flush() {
			super.flush();
			secondary.flush();
		}
	}
	
}
