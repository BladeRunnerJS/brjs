package org.bladerunnerjs.plugin.commands.standard;

import java.io.File;
import java.io.IOException;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.NodeAlreadyExistsException;
import org.bladerunnerjs.api.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.TemplateUtility;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class CreateLibraryCommand extends ArgsParsingCommandPlugin
{
	public static enum SupportedLibraryType {
		br,
		thirdparty;
	};
	
	public class Messages {
		public static final String LIBRARY_CREATE_SUCCESS_CONSOLE_MSG = "Successfully created new library '%s'";
		public static final String LIBRARY_PATH_CONSOLE_MSG = "  %s";
		public static final String INVALID_LIB_TYPE_MESSAGE = "Unsupported library type '%s'. Valid types are: %s";
	}
	
	private static final String NEW_LIBRARY_NAME = "new-library-name";
	private static final String TARGET_APP_NAME = "target-app-name";
	private static final String LIBRARY_TYPE = "type";
	private static final String TEMPLATE_GROUP = "template-group";
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption(TARGET_APP_NAME).setRequired(true).setHelp("the application the new library will be created within"));
		argsParser.registerParameter(new UnflaggedOption(NEW_LIBRARY_NAME).setRequired(true).setHelp("the name of the library that will be created"));
		argsParser.registerParameter(new FlaggedOption(LIBRARY_TYPE).setShortFlag('t').setLongFlag("type").setDefault( SupportedLibraryType.br.toString() ));
		argsParser.registerParameter(new FlaggedOption(TEMPLATE_GROUP).setShortFlag('T').setLongFlag("template").setDefault("default").setRequired(false).setHelp("the user-defined template that will be used"));
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
		return "create-library";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create a new empty library within a given application.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString(TARGET_APP_NAME);
		String libraryName = parsedArgs.getString(NEW_LIBRARY_NAME);
		String libraryType = parsedArgs.getString(LIBRARY_TYPE);
		String templateGroup = parsedArgs.getString(TEMPLATE_GROUP);
		
		SupportedLibraryType createLibraryType;
        try {
          createLibraryType = SupportedLibraryType.valueOf(libraryType);
        	SupportedLibraryType.valueOf(libraryType);
        } catch (IllegalArgumentException ex) {
           String exceptionMsg = String.format(Messages.INVALID_LIB_TYPE_MESSAGE, libraryType, StringUtils.join(SupportedLibraryType.values(), ", ") );
           throw new CommandArgumentsException(exceptionMsg, this);
        }
		
		App app = brjs.app(appName);
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		
		JsLib library = app.appJsLib(libraryName);
		if (library.dirExists()) throw new NodeAlreadyExistsException(library, this);
		
		if (TemplateUtility.templateExists(brjs, library.rootAssetLocation(), templateGroup, this)) {
		
			switch ( createLibraryType ) {
				case br:
					break;
				case thirdparty:
					createThirdpartyManifest(library);
					break;
			}
			
			try {
				library.populate(templateGroup);
			}
			catch(InvalidNameException e) {
				throw new CommandArgumentsException(e, this);
			}
			catch(ModelUpdateException e) {
				throw new CommandOperationException("Cannot create library '" + library.dir().getPath() + "'", e);
			}
			catch(TemplateInstallationException e) {
				throw new CommandArgumentsException(e, this);
			}
			logger.println(Messages.LIBRARY_CREATE_SUCCESS_CONSOLE_MSG, libraryName);
			logger.println(Messages.LIBRARY_PATH_CONSOLE_MSG, library.dir().getPath());
			
		}
		return 0;
	}

	private void createThirdpartyManifest(JsLib library) throws CommandOperationException
	{
		ThirdpartyLibManifest manifest;
		try
		{
			File manifestFile = library.file(ThirdpartyLibManifest.LIBRARY_MANIFEST_FILENAME); //TODO: it should be easier to create the manifest
			manifestFile.getParentFile().mkdirs();
			manifestFile.createNewFile();
			manifest = new ThirdpartyLibManifest( library.assetLocation(".") );
			manifest.write();
		}
		catch (ConfigException | IOException e)
		{
			throw new CommandOperationException("Cannot create library manifest for '" + library.dir().getPath() + "'", e);
		}
	}
}
