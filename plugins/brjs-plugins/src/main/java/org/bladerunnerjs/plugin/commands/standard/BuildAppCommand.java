package org.bladerunnerjs.plugin.commands.standard;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.DirectoryAlreadyExistsCommandException;
import org.bladerunnerjs.api.model.exception.command.DirectoryDoesNotExistCommandException;
import org.bladerunnerjs.api.model.exception.command.DirectoryNotEmptyCommandException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtils;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

public class BuildAppCommand extends ArgsParsingCommandPlugin {

	public class Messages {
		public static final String APP_BUILT_CONSOLE_MSG = "Built app '%s' available at '%s'";
		public static final String UNABLE_TO_DELETE_BULIT_APP_EXCEPTION = "Unable to automatically delete a previously built app at %s, possibly because its being used by another process. "+
						"The app will be exported to %s instead.";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	private class Parameters {
		public static final String APP_NAME = "app-name";
		public static final String TARGET_DIR = "target-dir";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption(Parameters.APP_NAME).setRequired(true).setHelp("the application within which the new blade will be created"));
		argsParser.registerParameter(new UnflaggedOption(Parameters.TARGET_DIR).setHelp("the directory within which the exported app will be built"));
		argsParser.registerParameter(new Switch("war").setShortFlag('w').setLongFlag("war").setDefault("false").setHelp("whether the exported files should be placed into a war zip."));
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());
	}
	
	@Override
	public String getCommandName() {
		return "build-app";
	}
	
	@Override
	public String getCommandDescription() {
		return "Build an application so that it can be deployed on web server.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		
		String appName = parsedArgs.getString(Parameters.APP_NAME);
		String targetDirPath = parsedArgs.getString(Parameters.TARGET_DIR);
		boolean warExport = parsedArgs.getBoolean("war");
		boolean hasExplicitExportDirArg = (targetDirPath != null);
		
		App app = brjs.app(appName);
		
		MemoizedFile targetDir = brjs.storageDir("built-apps");
		MemoizedFile appExportDir;
		MemoizedFile warExportFile;
		
		if (hasExplicitExportDirArg) 
		{
			targetDir = brjs.getMemoizedFile( new File(targetDirPath) );
			if (!targetDir.isDirectory()) 
			{
				targetDir = brjs.file("sdk/" + targetDirPath);
			}
			appExportDir = targetDir;
			warExportFile = targetDir.file(appName+".war");			
		} 
		else {
			appExportDir = targetDir.file(appName);
			warExportFile = targetDir.file(appName+".war");
			
			if (warExport && warExportFile.exists()) {
				boolean deleted = FileUtils.deleteQuietly(warExportFile);
				if (!deleted) {
					MemoizedFile oldWarExportFile = warExportFile;
					warExportFile = targetDir.file(appName+"_"+getBuiltAppTimestamp()+".war");
					brjs.logger(this.getClass()).warn( Messages.UNABLE_TO_DELETE_BULIT_APP_EXCEPTION, app.dir().getRelativePath(oldWarExportFile), app.dir().getRelativePath(warExportFile)); 
				}
			} else if (!warExport && appExportDir.exists()){
				boolean deleted = FileUtils.deleteQuietly(appExportDir);			
				if (!deleted) {
					MemoizedFile oldAppExportDir = appExportDir;
					appExportDir = targetDir.file(appName+"_"+getBuiltAppTimestamp());
					brjs.logger(this.getClass()).warn( Messages.UNABLE_TO_DELETE_BULIT_APP_EXCEPTION, app.dir().getRelativePath(oldAppExportDir), app.dir().getRelativePath(appExportDir));
				}
			}
			targetDir.mkdirs();
		}
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(!targetDir.isDirectory()) throw new DirectoryDoesNotExistCommandException(targetDirPath, this);
		
		try {
			if (warExport) {
				if(warExportFile.exists()) throw new DirectoryAlreadyExistsCommandException(warExportFile.getPath(), this);
				app.buildWar(warExportFile);
				brjs.getFileModificationRegistry().incrementFileVersion(warExportFile);
				logger.println(Messages.APP_BUILT_CONSOLE_MSG, appName, warExportFile.getAbsolutePath());
			} else {
				if (hasExplicitExportDirArg) {
					if (appExportDir.listFiles().length > 0) throw new DirectoryNotEmptyCommandException(appExportDir.getPath(), this);								
				} else {
					appExportDir.mkdir();			
				}
				app.build(appExportDir);
				brjs.getFileModificationRegistry().incrementFileVersion(appExportDir);
				logger.println(Messages.APP_BUILT_CONSOLE_MSG, appName, appExportDir.getAbsolutePath());
			}
		}
		catch (ModelOperationException e) {
			throw new CommandOperationException(e);
		}
		
		return 0;
	}
	
	private String getBuiltAppTimestamp() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhmmss");
		return sdf.format(date);
	}
}
