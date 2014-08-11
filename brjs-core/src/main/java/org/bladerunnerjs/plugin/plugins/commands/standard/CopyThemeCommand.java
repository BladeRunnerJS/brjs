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
	}
	
	private BRJS brjs;
	private App app;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-folder-path").setRequired(true).setHelp("required path leading to the app in which themes should be created. If path specifies a themes folder then the CSS theme is created only within that folder."));
		argsParser.registerParameter(new UnflaggedOption("copy-from-theme-name").setRequired(true).setHelp("the name of the application that will be created"));
		argsParser.registerParameter(new UnflaggedOption("copy-to-theme-name").setRequired(true).setHelp("the name of the application that will be created"));
		
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
		return "Copy a CSS theme files into another CSS theme.";
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
			matchLocation = appPath.subpath(1, pathCount).toString();
		
		List<AssetLocation> assetLocations = new ArrayList<AssetLocation>();
		
		for(Aspect asp : app.aspects()) //asp, hue hue, pun intended
			for(AssetLocation al : asp.assetLocations())
				if(al.dirExists())
					assetLocations.add(al);
		
		for(Bladeset bs : app.bladesets())
		{		
			for(AssetLocation al : bs.assetLocations())
				if(al.dirExists())
					assetLocations.add(al);
		
			for(Blade blade : bs.blades())
				for(AssetLocation al : blade.assetLocations())
					if(al.dirExists())
						assetLocations.add(al);
		}
		
		for(AssetLocation al : assetLocations){
			copyTheme(al, origTheme, newTheme, matchLocation);
		}
		
		return 0;
	}
	
	void copyTheme(AssetLocation location, String origTheme, String newTheme, String matchLocation) throws CommandOperationException{
		//check if newTheme already exists
		matchLocation = Paths.get(matchLocation).toString();
		String pathToCompare = Paths.get(RelativePathUtility.get(brjs, app.dir(), location.dir())).toString();
		
		if (location instanceof ThemedAssetLocation && location.dir().getName().compareTo(origTheme) == 0
				&& pathToCompare.contains(matchLocation) ) {
			 File srcDir = new File(location.dir().getPath());
			 File dstDir = new File(location.dir().getParentFile().getPath(), newTheme);	 			 
			 
			 if(dstDir.exists())
			 {
				 logger.warn(Messages.THEME_FOLDER_EXISTS, RelativePathUtility.get(brjs, brjs.dir(), dstDir));
				 return;
			 }
			 
			try {
				FileUtils.copyDirectory(srcDir, dstDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			logger.println(Messages.COPY_THEME_SUCCESS_CONSOLE_MSG, RelativePathUtility.get(brjs, app.dir(), srcDir), RelativePathUtility.get(brjs, app.dir(), dstDir));
		 }		
	}
	
}
