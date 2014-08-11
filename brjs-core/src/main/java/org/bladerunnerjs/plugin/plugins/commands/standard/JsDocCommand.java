package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

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
		
		File outputDir = app.storageDir(APP_STORAGE_DIR_NAME);
		
		try {
			if (outputDir.isDirectory()) {
				FileUtils.cleanDirectory(outputDir);
			} else {
				outputDir.mkdirs();
			}
			copyJsDocPlaceholder(app);
			runCommand(app, outputDir);
		}
		catch(IOException e) {
			throw new CommandOperationException(e);
		}
		
		logger.println(Messages.API_DOCS_GENERATED_MSG, outputDir.getPath());
		
		return 0;
	}
	
	private void runCommand(App app, File outputDir) throws CommandOperationException {
		List<String> commandArgs = new ArrayList<>();
		
		File workingDir = brjs.dir();
		File jsdocToolkitInstallDir = getToolkitResourcesDir(brjs);
		// this allows the toolkit and the conf to be overridden
		File jsDocToolkitDir = getSystemOrUserConfPath(brjs, jsdocToolkitInstallDir, "jsdoc-toolkit");
		File jsDocConfFile = getSystemOrUserConfPath(brjs, jsdocToolkitInstallDir, "jsdoc-conf.json");
		
		String command = RelativePathUtility.get(brjs, workingDir, jsDocToolkitDir)+"/jsdoc";
		if(System.getProperty("os.name").split(" ")[0].toLowerCase().equals("windows"))
		{
			command = command.replace("/", "\\") + ".cmd";
			commandArgs.add("cmd");
			commandArgs.add("/c");
		}
		commandArgs.add(command);
		
		commandArgs.add( RelativePathUtility.get(brjs, workingDir, app.dir())+"/" ); // add the app dir
		// sdk/libs/javascript is added via config file so dirs can be optionally ignored
		commandArgs.add("-c"); // set the config file
			commandArgs.add( RelativePathUtility.get(brjs, workingDir, jsDocConfFile) );
		commandArgs.add("-r"); // recurse into dirs
		commandArgs.add("-d"); // the output dir
			commandArgs.add( RelativePathUtility.get(brjs, workingDir, outputDir) );
		commandArgs.add("-q");
			commandArgs.add( "date="+getBuildDate()+"&version="+brjs.versionInfo().getVersionNumber() );
		
		
		logger.info("running command: " + StringUtils.join(commandArgs, " "));
		logger.info("working directory: " + workingDir);
			
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.directory( workingDir );
		processBuilder.command(commandArgs);
		
		CommandRunnerUtility.runCommand(brjs, processBuilder);
	}
	
	private File getSystemOrUserConfPath(BRJS brjs, File systemDirBase, String dirName) {
		File userConfDir = brjs.conf().file(dirName);
		if (userConfDir.exists()) {
			return userConfDir;
		}
		return new File(systemDirBase, dirName);
	}
	
	private String getBuildDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd MMMMM yyyy");
		Date date = new Date();
		return dateFormat.format(date);
	}
	
	
	public static void copyJsDocPlaceholder(App app) throws IOException {
		File placeholderSrcDir = new File(getToolkitResourcesDir(app.root()), "jsdoc-placeholders");
		if (!placeholderSrcDir.exists()) {
			return;
		}
		File placeholderDestDir = app.storageDir(APP_STORAGE_DIR_NAME);
		placeholderDestDir.mkdirs();
		for (File srcFile : FileUtils.listFiles(placeholderSrcDir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE)) {
			String pathRelativeToDestDir = RelativePathUtility.get(app.root(), placeholderSrcDir, srcFile);
			File destFile = new File(placeholderDestDir, pathRelativeToDestDir);
			if (!destFile.exists()) {
				FileUtils.copyFile(srcFile, destFile);
			}
		}
	}

    private static File getToolkitResourcesDir(BRJS brjs)
    {
    	return new File(brjs.sdkRoot().dir(), "jsdoc-toolkit-resources");
    }
	
}
