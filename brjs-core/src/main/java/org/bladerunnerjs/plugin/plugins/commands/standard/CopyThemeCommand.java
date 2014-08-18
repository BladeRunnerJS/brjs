package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.RelativePathUtility;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CopyThemeCommand extends ArgsParsingCommandPlugin
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
		return "Duplicate an existing CSS theme";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String argPath = parsedArgs.getString("app-folder-path");
		String origTheme = parsedArgs.getString("copy-from-theme-name");
		String newTheme = parsedArgs.getString("copy-to-theme-name");
		
		Path appPath = Paths.get(argPath);
		String matchLocation = "";
				
		app = brjs.app(appPath.getName(0).toString());
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		int pathCount = appPath.getNameCount();
		
		if(pathCount > 1)
		{
			matchLocation = appPath.subpath(1, pathCount).toString();
		}
		
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		for(Aspect asp : app.aspects())
		{ //asp, hue hue, pun intended
			for(AssetLocation al : asp.assetLocations())
			{
				if(al.dirExists())
				{
					assetLocations.add(al);
				}
			}
		}
		
		for(Bladeset bs : app.bladesets())
		{		
			for(AssetLocation al : bs.assetLocations())
			{
				if(al.dirExists())
				{
					assetLocations.add(al);
				}
			}
		
			for(Blade blade : bs.blades())
			{
				for(AssetLocation al : blade.assetLocations())
				{
					if(al.dirExists())
					{
						assetLocations.add(al);
					}
				}
			}
		}
		
		if(themeExists(assetLocations, origTheme, matchLocation))
		{
			for(AssetLocation al : assetLocations)
			{
				if(themeExistsWithinAssetLocation(al, origTheme, matchLocation)) {
					copyTheme(al, origTheme, newTheme);
				}
			}
		}
		else
		{
			logger.warn(Messages.THEME_FOLDER_DOES_NOT_EXIST, origTheme);
		}
		
		return 0;
	}
	
	void copyTheme(AssetLocation location, String origTheme, String newTheme) throws CommandOperationException{
		 File srcDir = new File(location.dir().getPath());
		 File dstDir = new File(location.dir().getParentFile().getPath(), newTheme);	 			 
		 
		 if(dstDir.exists())
		 {
			 logger.warn(Messages.THEME_FOLDER_EXISTS, RelativePathUtility.get(brjs, brjs.dir(), dstDir));
			 return;
		 }
		 
		 try {
				FileUtils.copyDirectory(srcDir, dstDir);
		 } 
		 catch (IOException e) {
				e.printStackTrace();
		 }
		
		logger.println(Messages.COPY_THEME_SUCCESS_CONSOLE_MSG, RelativePathUtility.get(brjs, app.dir(), srcDir), RelativePathUtility.get(brjs, app.dir(), dstDir));			
	}
	
	private boolean themeExists(List<AssetLocation> assetLocations, String origTheme, String matchLocation) {
		for(AssetLocation al : assetLocations) {
			if(themeExistsWithinAssetLocation(al, origTheme, matchLocation)) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean themeExistsWithinAssetLocation(AssetLocation location, String origTheme, String matchLocation) {
		matchLocation = Paths.get(matchLocation).toString();
		String pathToCompare = Paths.get(RelativePathUtility.get(brjs, app.dir(), location.dir())).toString();
		
		return location instanceof ThemedAssetLocation && location.dir().getName().compareTo(origTheme) == 0
				&& pathToCompare.contains(matchLocation);
	}
	
}
