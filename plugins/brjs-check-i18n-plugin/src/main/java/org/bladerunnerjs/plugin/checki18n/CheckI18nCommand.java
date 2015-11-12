package org.bladerunnerjs.plugin.checki18n;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;

import org.bladerunnerjs.api.logging.Logger;

public class CheckI18nCommand extends JSAPArgsParsingCommandPlugin {

	private BRJS brjs;
	private Logger logger;
	private String[] missingTokens = new String[0];

	@Override
	public String getCommandName() {
		return "check-i18n";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application to search for missing translations"));
		argsParser.registerParameter(new UnflaggedOption("locale").setDefault("default").setHelp("the locale used to search tokens"));		
	}	

	@Override
	public String getCommandDescription() {
		return "Show missing i18n translations for a given application/locale.";
	}

	@Override
	public String getCommandHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		String locale = parsedArgs.getString("locale");
		
		listMissingTokens(appName, locale);
		return 0;
	}
	
	private void listMissingTokens(String appName, String locale) {		
		App app = brjs.app(appName);
		String localeToBeChecked = getLocaleToBeChecked(app, locale);
		if(localeToBeChecked == "no locale specified"){
			logger.println(appName + " has no default locale");
			return;
		}
		
		findMissingTranslationsForAppWithLocale(app, localeToBeChecked);
		
		String missingTokensMessage = this.missingTokens.length == 0 ? " has no missing translations" : " has no translations defined for the following token:";
		String firstLogLine = "For the locale " + localeToBeChecked + ", " + appName + missingTokensMessage;
		logger.println(firstLogLine);		
	}

	private void findMissingTranslationsForAppWithLocale(App app, String localeToBeChecked) {
		//todo html assets
		//todo js assets
		//todo xml assets
		
	}

	private String getLocaleToBeChecked(App app, String locale) {
		String localeToBeChecked = "";
		if(locale == "default"){			
			try {
				localeToBeChecked = app.appConf().getDefaultLocale().toString();
			} catch (ConfigException e) {
				return "no locale specified";
			}
		} else {
			localeToBeChecked = locale;
		}				
		return localeToBeChecked;
	}

	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());		
	}
}
