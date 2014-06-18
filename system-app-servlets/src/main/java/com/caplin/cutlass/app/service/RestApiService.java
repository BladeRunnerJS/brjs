package com.caplin.cutlass.app.service;

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

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateAppCommand;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladesetCommand;
import org.bladerunnerjs.plugin.plugins.commands.standard.JsDocCommand;

import com.caplin.cutlass.CutlassConfig;
import com.caplin.cutlass.command.copy.CopyBladesetCommand;
import com.caplin.cutlass.command.importing.ImportApplicationCommand;
import com.caplin.cutlass.command.test.TestCommand;
import com.caplin.cutlass.command.test.testrunner.TestRunnerController;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.BladeNode;
import com.caplin.cutlass.structure.model.node.BladesetNode;


public class RestApiService
{
	private static final String JS_TEST_REPORT_SWITCH = "--" + TestRunnerController.REPORT_SWITCH;
	public static final String IMPORT_BLADESETS_NEWBLADESET_NAME_KEY = "newBladesetName";
	public static final String IMPORT_BLADESETS_BLADES_KEY = "blades";
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
	
	public File getAppImageLocation(String app) throws Exception
	{
		File appPath = brjs.userApp(app).dir();
		File appImage = new File(appPath,"thumb.png"); 
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
		ImportApplicationCommand cmd = new ImportApplicationCommand( brjs );
		String[] args = new String[]{ appZip.getAbsolutePath(), appName, requirePrefix };		
		doCommand( cmd, args );
	}
	
	public void exportWar(String appName, File destinationWar) throws Exception
	{
		if (destinationWar.exists())
		{
			destinationWar.delete();
		}
		File targetDir = destinationWar.getParentFile();
		
		App app = brjs.userApp(appName);
		if (!app.dirExists()) {
			throw new Exception("Unable to export, the app '" + appName + "' doesn't exist.");
		}
		
		app.buildWar(targetDir);
		
		File tempWar = new File(targetDir, appName + ".war");
		FileUtils.moveFile(tempWar, destinationWar);
	}
	
	public void importBladeset(String sourceApp, Map<String,Map<String,List<String>>> bladesets, String targetApp) throws Exception
	{
		for (String bladeset : bladesets.keySet())
		{
			Map<String,List<String>> bladesetMap = bladesets.get(bladeset);
			
			String newBladesetName = bladesetMap.get(IMPORT_BLADESETS_NEWBLADESET_NAME_KEY).get(0);
			List<String> blades = bladesetMap.get(IMPORT_BLADESETS_BLADES_KEY);
			
			CopyBladesetCommand cmd = new CopyBladesetCommand( new File(brjs.root().dir(), CutlassConfig.SDK_DIR) );
			String[] args = new String[]{ sourceApp, bladeset, targetApp, newBladesetName };		
			doCommand( cmd, args );
			
			BladesetNode bladesetNode = SdkModel.getRootNode( new File(brjs.root().dir(), CutlassConfig.SDK_DIR) ).getPath().appsPath().appPath(targetApp).bladesetPath(newBladesetName).getNode();
			
			if(bladesetNode != null)
			{
				List<BladeNode> bladeNodes = bladesetNode.getBladeNodes();
				
				for (BladeNode bladeNode : bladeNodes)
				{
					if (!blades.contains(bladeNode.getName()))
					{						
						FileUtils.deleteDirectory(bladeNode.getDir());
					}
				}
			}
		}
	}
	
	public void createApp(String appName, String requirePrefix) throws Exception
	{
		CreateAppCommand cmd = new CreateAppCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appName, requirePrefix };		
		doCommand( cmd, args );
	}
	
	public void createBladeset(String appName, String bladesetName) throws Exception
	{
		CreateBladesetCommand cmd = new CreateBladesetCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appName, bladesetName };		
		doCommand( cmd, args );
	}
	
	public void createBlade(String appName, String bladesetName, String bladeName) throws Exception
	{
		CreateBladeCommand cmd = new CreateBladeCommand();
		cmd.setBRJS(brjs);
		String[] args = new String[]{ appName, bladesetName, bladeName };		
		doCommand( cmd, args );
	}
	
	public String runBladesetTests(String appName, String bladesetName, String testType) throws Exception
	{
		TestCommand cmd = new TestCommand();
		String bladesetPath = SdkModel.getRootNode( new File(brjs.root().dir(), CutlassConfig.SDK_DIR) ).getPath().appsPath().appPath(appName).bladesetPath(bladesetName).getPathStr();
		String[] args = new String[]{ bladesetPath, testType, JS_TEST_REPORT_SWITCH };	
		OutputStream out = doCommand( cmd, args );
		return out.toString();
	}
	
	public String runBladeTests(String appName, String bladesetName, String bladeName, String testType) throws Exception
	{
		TestCommand cmd = new TestCommand();
		String bladePath = SdkModel.getRootNode( new File(brjs.root().dir(), CutlassConfig.SDK_DIR) ).getPath().appsPath(
			).appPath(appName).bladesetPath(bladesetName).bladesPath().bladePath(bladeName).getPathStr();
		String[] args = new String[]{ bladePath, testType, JS_TEST_REPORT_SWITCH };
		OutputStream out = doCommand( cmd, args );
		return out.toString();
	}
	
	public String getCurrentReleaseNotes() throws Exception
	{
		File latestReleaseNote = getLatestReleaseNoteFile();
		if (latestReleaseNote != null)
		{
			return FileUtils.readFileToString(latestReleaseNote);			
		}
		throw new Exception("Unable to find latest release note.");
	}
	
	public String getSdkVersion() throws IOException
	{
		File versionFile = brjs.versionInfo().getFile();
		return FileUtils.readFileToString(versionFile);
	}
	
	public void getJsdocForApp(String appName) throws Exception {
		JsDocCommand jsDocCommand = new JsDocCommand();
		jsDocCommand.setBRJS(brjs);
		String[] args = new String[]{appName, "-v"};
		//TODO: should this be something that wraps stdOut?
		jsDocCommand.doCommand(args);
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
		OutputStream out = new ByteArrayOutputStream();
		ConsoleWriter oldConsoleWriter = brjs.getConsoleWriter();
		brjs.setConsoleWriter( new PrintStream(out) );
		
		command.doCommand(args);
		
		brjs.setConsoleWriter( oldConsoleWriter );
		return out;
	}
	
	private File getLatestReleaseNoteFile() 
	{
		return new File( new File(brjs.root().dir(), CutlassConfig.SDK_DIR) , "docs/release-notes/latest.html");
	}
	
}
