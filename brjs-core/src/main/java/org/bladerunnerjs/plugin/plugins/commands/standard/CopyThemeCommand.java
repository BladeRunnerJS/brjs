package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import javax.naming.InvalidNameException;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.plugin.plugins.commands.standard.CreateBladeCommand.Messages;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.NameValidator;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CopyThemeCommand extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String COPY_THEME_SUCCESS_CONSOLE_MSG = "Successfully copied theme %s% in %s% app into new theme called %s%";
		public static final String APP_DEPLOYED_CONSOLE_MSG = "Successfully deployed '%s' app";
	}
	
	private BRJS brjs;
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
		String appName = parsedArgs.getString("app-folder-path");
		String origTheme = parsedArgs.getString("copy-from-theme-name");
		String newTheme = parsedArgs.getString("copy-to-theme-name");
		
		App app = brjs.app(appName);
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		for(Bladeset bs : app.bladesets()){
			for(Blade blade : bs.blades()){
				for(AssetLocation al : blade.assetLocations()){
					if(al.dirExists())
					{
						//System.out.println(al.dir().getPath());
						copyTheme(al.assetContainer(), origTheme, newTheme);
					}
				}
			}
		}
		
	
		
		//resources assets locations you will get themes
		
		//When we have path.getName(1) (it's the app/theme hopefully), then we
		//work only within that folder structure
		
		
		//When there is only path.getName(0), then we need to check all theme folders in all nodes
		
		return 0;
	}
	
	void copyTheme(AssetContainer assetContainer, String origTheme, String newTheme){
		
		//check if newTheme already exists
		
		
		for (AssetLocation location: assetContainer.assetLocations()){			
			 if (location instanceof ThemedAssetLocation && location.dir().getName().compareTo(origTheme) == 0) {
				 File srcDir = new File(location.dir().getPath());
				 File dstDir = new File(location.dir().getParentFile().getPath(), newTheme);
				 System.out.println(location.dir().getPath());
				 
				 if(dstDir.exists()){
					 System.out.println("YAY! It doesn't exist!");
				 }
					 
				 
				 try {
					FileUtils.copyDirectory(srcDir, dstDir);
				} catch (IOException e) {
					e.printStackTrace();
				}
			 }
		}
	}
	
}
