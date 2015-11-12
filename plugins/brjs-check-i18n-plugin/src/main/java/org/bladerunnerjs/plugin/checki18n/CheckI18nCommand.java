package org.bladerunnerjs.plugin.checki18n;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.api.plugin.Locale;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

import org.bladerunnerjs.api.logging.Logger;

public class CheckI18nCommand extends JSAPArgsParsingCommandPlugin {

	private BRJS brjs;
	private Logger logger;
	private List<String> missingTokens = new ArrayList<String>();
	private final static Pattern I18N_TOKEN_PATTERN = Pattern.compile("@\\{(.*?)\\}");

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
		
		String missingTokensMessage = this.missingTokens.size() == 0 ? " has no missing translations" : " has no translations defined for the following token:";
		String firstLogLine = "For the locale " + localeToBeChecked + ", " + appName + missingTokensMessage;
		logger.println(firstLogLine);
		for (String missingToken : missingTokens) {
			logger.println(missingToken);
		}
	}

	private void findMissingTranslationsForAppWithLocale(App app, String localeToBeChecked) {
		//todo js assets... bundleset.sourceModules();
		for(Aspect aspect : app.aspects()) {
			//todo check tests
			BundleSet bundleSet = null;
			try {
				bundleSet = aspect.getBundleSet();
			} catch (ModelOperationException e) {
				e.printStackTrace();
			}	
			checkBundletForMissingTokens(bundleSet, localeToBeChecked);						
		}
		for(Bladeset bladeset : app.bladesets()) {
			//todo check bladeset content
			for(Blade blade : bladeset.blades()) {
				//todo check blade content
			}
		}
	}

	private void checkBundletForMissingTokens(BundleSet bundleSet, String localeToBeChecked) {
		List<Asset> htmlAndXml = new ArrayList<>();
		htmlAndXml.addAll(bundleSet.seedAssets());
		htmlAndXml.addAll(bundleSet.assets("html!"));
		htmlAndXml.addAll(bundleSet.assets("xml!"));
		for(Asset asset : htmlAndXml){
			String htmlContent = null;
			try {
				htmlContent = IOUtils.toString(asset.getReader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			checkAssetForMissingTokens(bundleSet, htmlContent, localeToBeChecked);
		}
		
	}

	//todo write a version that works for js
	private void checkAssetForMissingTokens(BundleSet bundleSet, String content, String localeToBeChecked) {
		Locale locale = new Locale(localeToBeChecked);
		Map<String,String> propertiesMap = null;
		try {
			//todo - not retrieving properties
			propertiesMap = I18nPropertiesUtils.getI18nProperties(bundleSet, locale);
		} catch (ContentProcessingException e) {
			e.printStackTrace();
		}
		Matcher i18nTokenMatcher = I18N_TOKEN_PATTERN.matcher(content);
		
		while (i18nTokenMatcher.find()) {
			String i18nKey = i18nTokenMatcher.group(1).toLowerCase();
			String keyReplacement = propertiesMap.get(i18nKey);
			if (keyReplacement == null) {
				this.missingTokens.add(i18nKey);
			}
		}		
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
