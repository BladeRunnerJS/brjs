package org.bladerunnerjs.plugin.commands.standard;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;
import org.bladerunnerjs.appserver.ApplicationServerUtils;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.TemplateUtility;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class J2eeifyCommandPlugin extends ArgsParsingCommandPlugin
{
	public class Messages {
		public static final String APP_DOES_NOT_EXIST_EXCEPTION = "The app '%s' does not exist";
		public static final String SUCCESSFULLY_J2EEIFIED_APP_MESSAGE = "Successfully 'j2eeified' the '%s' app, the neccessary jars and config have been copied into '%s'";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the name of the application to 'j2eeify'"));
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
		return "j2eeify";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Copies WEB-INF files into an application ready for adding servlets and other J2EE features";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		App app = brjs.app(appName);
		
		if(!app.dirExists()) throw new CommandArgumentsException( String.format(Messages.APP_DOES_NOT_EXIST_EXCEPTION, appName), this );
		
		Map<String, String> transformations = new HashMap<>();
		transformations.put("app-name", app.getName());
		try
		{
			TemplateUtility.installTemplate(app, brjs.file("sdk/j2eeify-app"), transformations, true );
			String webXmlContents =  IOUtils.toString( ApplicationServerUtils.getDefaultWebXmlResourceLocation() );
			FileUtils.write( app.file("WEB-INF/web.xml") , webXmlContents );
			FileUtils.copyDirectory(brjs.appJars().dir(), app.file("WEB-INF/lib"));
		}
		catch (TemplateInstallationException | IOException ex)
		{
			throw new CommandOperationException(ex);
		}
		
		String relativeWebInf = app.root().dir().getRelativePath(app.file("WEB-INF"));
		logger.println(Messages.SUCCESSFULLY_J2EEIFIED_APP_MESSAGE, appName, relativeWebInf);
		
		return 0;
	}
}
