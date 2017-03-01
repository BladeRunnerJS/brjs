package org.bladerunnerjs.plugin.commands.standard;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.utility.FileUtils;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CopyThemeCommand extends JSAPArgsParsingCommandPlugin
{
	public class Messages {
		public static final String COPY_THEME_SUCCESS_CONSOLE_MSG = "Successfully copied theme '%s' into '%s'";
		public static final String THEME_FOLDER_EXISTS = "Theme folder already exist, see '%s'. Not copying contents.";
		public static final String THEME_FOLDER_DOES_NOT_EXIST = "Theme '%s' does not exist in the selected app.";
	}
	
	private BRJS brjs;
	private App app;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-folder-path").setRequired(true).setHelp("required path leading to the app in which themes should be created. If path specifies a themes folder then the CSS theme is created only within that folder."));
		argsParser.registerParameter(new UnflaggedOption("copy-from-theme-name").setRequired(true).setHelp("the name of the theme that will be copied"));
		argsParser.registerParameter(new UnflaggedOption("copy-to-theme-name").setRequired(true).setHelp("the name of the new theme into which the content will be copied"));
		
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "copy-theme";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Duplicate an existing CSS theme.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String argPath = parsedArgs.getString("app-folder-path");
		String origTheme = parsedArgs.getString("copy-from-theme-name");
		String newTheme = parsedArgs.getString("copy-to-theme-name");
		
		Path appPath = Paths.get(argPath);
				
		app = brjs.app(appPath.getName(0).toString());
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		int pathCount = appPath.getNameCount();		
		String matchLocation = (pathCount > 1) ? matchLocation = appPath.subpath(1, pathCount).toString() : "";		
		
		Map<MemoizedFile,MemoizedFile> copyToFromMap = new HashMap<>();
		
		List<AssetContainer> assetContainers = new ArrayList<>();
		assetContainers.addAll(app.aspects());
		for(Bladeset bladeset : app.bladesets())
		{		
			assetContainers.add(bladeset);
			assetContainers.addAll(bladeset.blades());
		}
		
		boolean foundOrigTheme = false;
		
		for(AssetContainer container : assetContainers) {
			MemoizedFile origThemeDir = container.file("themes/"+origTheme);
			MemoizedFile newThemeDir = container.file("themes/"+newTheme);
			String relativePath = Paths.get(app.dir().getRelativePath(origThemeDir)).toString();
			if (origThemeDir.exists()) {
				foundOrigTheme = true;
			}
			if (newThemeDir.exists()) {
				logger.warn(Messages.THEME_FOLDER_EXISTS, brjs.dir().getRelativePath(newThemeDir));
			} else if ( origThemeDir.isDirectory() && (relativePath.contains(matchLocation) || matchLocation.equals("")) ) {
				copyToFromMap.put(origThemeDir, newThemeDir);
			}
		}
		
		
		if (foundOrigTheme)
		{
			for (MemoizedFile copyFileFrom : copyToFromMap.keySet()) {
				MemoizedFile copyFileTo = copyToFromMap.get(copyFileFrom);
				try
				{
					FileUtils.copyDirectory(copyFileFrom, copyFileTo);
					logger.println(Messages.COPY_THEME_SUCCESS_CONSOLE_MSG, app.dir().getRelativePath(copyFileFrom), app.dir().getRelativePath(copyFileTo));
				}
				catch (IOException ex)
				{
					throw new CommandOperationException(ex);
				}
			}
		}
		else
		{
			logger.warn(Messages.THEME_FOLDER_DOES_NOT_EXIST, origTheme);
		}
		
		return 0;
	}
	
}
