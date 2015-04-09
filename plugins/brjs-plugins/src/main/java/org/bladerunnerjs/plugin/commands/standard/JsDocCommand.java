package org.bladerunnerjs.plugin.commands.standard;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtils;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

//TODO: we have very few (if any) tests around this command

public class JsDocCommand extends ArgsParsingCommandPlugin {
	public static final String APP_STORAGE_DIR_NAME = "jsdoc";

	public class Messages {
		public static final String API_DOCS_GENERATED_MSG = "API docs successfully generated in '%s'";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());

	}
	
	@Override
	public String getCommandName() {
		return APP_STORAGE_DIR_NAME;
	}
	
	@Override
	public String getCommandDescription() {
		return "Generate JsDocs for a given application";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application for which jsdoc will be generated"));
		argsParser.registerParameter(new Switch("verbose-flag").setShortFlag('v').setLongFlag("verbose").setDefault("false").setHelp("display more console output while generating the jsdoc output"));
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		App app = brjs.app(appName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		MemoizedFile outputDir = app.storageDir(APP_STORAGE_DIR_NAME);
		
		try {
			if (outputDir.isDirectory()) {
				FileUtils.cleanDirectory(outputDir);
			} else {
				outputDir.mkdirs();
			}
			copyJsDocPlaceholder(app);
			runCommand(app, outputDir);
		}
		catch(IOException | ConfigException e) {
			throw new CommandOperationException(e);
		}
		
		logger.println(Messages.API_DOCS_GENERATED_MSG, outputDir.getPath());
		
		return 0;
	}
	
	private void runCommand(App app, MemoizedFile outputDir) throws CommandOperationException, ConfigException {
		List<String> commandArgs = new ArrayList<>();
		
		MemoizedFile workingDir = brjs.dir();
		MemoizedFile jsdocToolkitInstallDir = getToolkitResourcesDir(brjs);
		// this allows the toolkit and the conf to be overridden
		MemoizedFile jsDocToolkitDir = getSystemOrUserConfPath(brjs, jsdocToolkitInstallDir, "jsdoc-toolkit");
		MemoizedFile jsDocConfFile = getSystemOrUserConfPath(brjs, jsdocToolkitInstallDir, "jsdoc-conf.json");
		
		if (brjs.bladerunnerConf().useNodeCommands()) {
			addNodeCommandArgs(commandArgs, workingDir, jsDocToolkitDir);
		} else {
			addRhinoCommandArgs(commandArgs, workingDir, jsDocToolkitDir);			
		}
		
		commandArgs.add( workingDir.getRelativePath(app.dir())+"/" ); // add the app dir
		// sdk/libs/javascript is added via config file so dirs can be optionally ignored
		commandArgs.add("-c"); // set the config file
			commandArgs.add( workingDir.getRelativePath(jsDocConfFile) );
		commandArgs.add("-r"); // recurse into dirs
		commandArgs.add("-d"); // the output dir
			commandArgs.add( workingDir.getRelativePath( brjs.getMemoizedFile(outputDir) ) );
		commandArgs.add("-q");
			commandArgs.add( "date="+getBuildDate()+"&version="+brjs.versionInfo().getVersionNumber() );
		
		
		logger.info("running command: " + StringUtils.join(commandArgs, " "));
		logger.info("working directory: " + workingDir);
			
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory( workingDir );
		processBuilder.command( commandArgs );
		
		CommandRunnerUtility.runCommand(brjs, processBuilder);
	}

	private void addNodeCommandArgs(List<String> commandArgs, MemoizedFile workingDir, MemoizedFile jsDocToolkitDir)
	{
		commandArgs.add("node");
		commandArgs.add(workingDir.getRelativePath(jsDocToolkitDir)+"/jsdoc.js");
	}
	
	private void addRhinoCommandArgs(List<String> commandArgs, MemoizedFile workingDir, MemoizedFile jsDocToolkitDir)
	{
		String command = workingDir.getRelativePath(jsDocToolkitDir)+"/jsdoc";
		if(System.getProperty("os.name").split(" ")[0].toLowerCase().equals("windows"))
		{
			command = command.replace("/", "\\") + ".cmd";
			commandArgs.add("cmd");
			commandArgs.add("/c");
		}
		commandArgs.add(command);
	}

	private MemoizedFile getSystemOrUserConfPath(BRJS brjs, MemoizedFile systemDirBase, String dirName) {
		File userConfDir = brjs.conf().file(dirName);
		if (userConfDir.exists()) {
			return brjs.getMemoizedFile( userConfDir );
		}
		return brjs.getMemoizedFile( new File(systemDirBase, dirName) );
	}
	
	private String getBuildDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	
	public static void copyJsDocPlaceholder(App app) throws IOException {
		MemoizedFile placeholderSrcDir = getToolkitResourcesDir(app.root()).file("jsdoc-placeholders");
		if (!placeholderSrcDir.exists()) {
			return;
		}
		MemoizedFile placeholderDestDir = app.storageDir(APP_STORAGE_DIR_NAME);
		placeholderDestDir.mkdirs();
		for (MemoizedFile srcFile : placeholderSrcDir.nestedFiles()) {
			String pathRelativeToDestDir = placeholderSrcDir.getRelativePath(srcFile);
			File destFile = new File(placeholderDestDir, pathRelativeToDestDir);
			if (!destFile.exists()) {
				FileUtils.copyFile(app.root(), srcFile, destFile);
			}
		}
	}

    private static MemoizedFile getToolkitResourcesDir(BRJS brjs)
    {
    	return brjs.sdkRoot().file("jsdoc-toolkit-resources");
    }
	
}
