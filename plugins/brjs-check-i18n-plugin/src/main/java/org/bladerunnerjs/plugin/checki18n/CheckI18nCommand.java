package org.bladerunnerjs.plugin.checki18n;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.SourceModule;
import org.bladerunnerjs.api.TestPack;
import org.bladerunnerjs.api.TestableNode;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.plugin.commands.standard.J2eeifyCommandPlugin.Messages;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

import org.bladerunnerjs.api.logging.Logger;

public class CheckI18nCommand extends JSAPArgsParsingCommandPlugin {
	
	public class Messages {
		public static final String APP_DOES_NOT_EXIST_EXCEPTION = "The app '%s' does not exist";
		public static final String NO_LOCALE_FOR_APP = "The app specified does not contain a default locale";
	}
	
	private BRJS brjs;
	private Logger logger;
	private Set<String> missingTokens = new HashSet<String>();
	private Pattern I18N_HTML_XML_TOKEN_PATTERN = Pattern.compile("@\\{(.*?)\\}");
	private Pattern JS_TOKEN_PATTERN = Pattern.compile("i18n\\([\\s]*[\'\"](.*?)[\'\"]([\\s]*[,+].*)?[\\s]*\\)");

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
	
	private void listMissingTokens(String appName, String locale) throws CommandArgumentsException {		
		App app = brjs.app(appName);
		String localeToBeChecked = getLocaleToBeChecked(app, locale);
		
		if(localeToBeChecked == "no locale specified.")
			throw new CommandArgumentsException( String.format(Messages.NO_LOCALE_FOR_APP), this );
		
		if(!app.dirExists()) 
			throw new CommandArgumentsException( String.format(Messages.APP_DOES_NOT_EXIST_EXCEPTION, appName), this );
		
		findMissingTranslationsForAppWithLocale(app, localeToBeChecked);
		
		String missingTokensMessage = this.missingTokens.size() == 0 ? " has no missing translations" : " has no translations defined for the following tokens:";
		String firstLogLine = "\n" + "For the locale " + localeToBeChecked + ", " + appName + missingTokensMessage + "\n";
		logger.println(firstLogLine);
		for (String missingToken : missingTokens) {
			logger.println(missingToken);
		}
	}

	private void findMissingTranslationsForAppWithLocale(App app, String localeToBeChecked) {
		for(Aspect aspect : app.aspects()) {
			checkMissingLocalsForBundlableNode(localeToBeChecked, aspect);
			
			checkMissingLocales(localeToBeChecked, aspect);
		}
		for(Bladeset bladeset : app.bladesets()) {
			checkMissingLocales(localeToBeChecked, bladeset);
			for(Blade blade : bladeset.blades()) {				
				checkMissingLocales(localeToBeChecked, blade);
				checkMissingLocales(localeToBeChecked, blade.workbench());
				checkMissingLocalsForBundlableNode(localeToBeChecked, blade.workbench());	
			}
		}
	}

	private void checkMissingLocales(String localeToBeChecked, TestableNode testableNode) {
		for(TypedTestPack typedTestPack : testableNode.testTypes()){
			for(TestPack testPack : typedTestPack.testTechs()){
				checkMissingLocalsForBundlableNode(localeToBeChecked, testPack);
			}
		}
	}

	private void checkMissingLocalsForBundlableNode(String localeToBeChecked, BundlableNode bundlableNode) {
		BundleSet bundleSet = null;
		try {
			bundleSet = bundlableNode.getBundleSet();
		} catch (ModelOperationException e) {
			e.printStackTrace();
		}	
		checkBundletForMissingTokens(bundleSet, localeToBeChecked);
	}

	private void checkBundletForMissingTokens(BundleSet bundleSet, String localeToBeChecked) {
		List<Asset> htmlAndXml = new ArrayList<>();
		htmlAndXml.addAll(bundleSet.seedAssets());
		htmlAndXml.addAll(bundleSet.assets("html!"));
		htmlAndXml.addAll(bundleSet.assets("xml!"));
		for(Asset asset : htmlAndXml){
			String content = null;
			try {
				content = IOUtils.toString(asset.getReader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			checkAssetForMissingTokens(bundleSet, content, localeToBeChecked, I18N_HTML_XML_TOKEN_PATTERN);
		}
		
		List<SourceModule> sourcceModules = bundleSet.sourceModules();
		for(SourceModule sourceModule : sourcceModules){
			String srcContent = null;
			try {
				srcContent = IOUtils.toString(sourceModule.getReader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			checkAssetForMissingTokens(bundleSet, srcContent, localeToBeChecked, JS_TOKEN_PATTERN);			
		}
		
	}

	private void checkAssetForMissingTokens(BundleSet bundleSet, String content, String localeToBeChecked, Pattern pattern) {
		Locale locale = new Locale(localeToBeChecked);
		Map<String,String> propertiesMap = null;
		try {
			propertiesMap = I18nPropertiesUtils.getI18nProperties(bundleSet, locale);
		} catch (ContentProcessingException e) {
			e.printStackTrace();
		}
		Matcher i18nTokenMatcher = pattern.matcher(content);
		
		while (i18nTokenMatcher.find()) {
			Boolean tokenIsNotComplete = false;
			Boolean propertiesFileContainPartialMatch = false;
			
			if(i18nTokenMatcher.groupCount() > 1){
				tokenIsNotComplete = i18nTokenMatcher.group(2) != null && i18nTokenMatcher.group(2).indexOf('+') != -1;
				propertiesFileContainPartialMatch = mapContainsPartialToken(propertiesMap, i18nTokenMatcher.group(1)) && tokenIsNotComplete;
			}
			String i18nKey = i18nTokenMatcher.group(1).toLowerCase();
			String keyReplacement = propertiesMap.get(i18nKey);
			
			if (keyReplacement == null && !propertiesFileContainPartialMatch) {				
				String missingToken = tokenIsNotComplete ? i18nKey + "*" : i18nKey;
				this.missingTokens.add(missingToken);
			}
		}		
	}

	private boolean mapContainsPartialToken(Map<String, String> propertiesMap, String partialToken) {
		for(Entry<String, String> entry : propertiesMap.entrySet()) {
			  String i18nToken = entry.getKey();
			  if(i18nToken.contains(partialToken))
				  return true;
			}
		return false;
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
