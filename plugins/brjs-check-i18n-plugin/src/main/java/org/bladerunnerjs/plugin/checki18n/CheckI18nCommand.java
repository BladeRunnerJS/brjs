package org.bladerunnerjs.plugin.checki18n;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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
import org.bladerunnerjs.plugin.commands.standard.BuildAppCommand.Messages;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

import org.bladerunnerjs.api.logging.Logger;

public class CheckI18nCommand extends JSAPArgsParsingCommandPlugin {
	
	public class Messages {
		public static final String APP_CHECK_I18N_CONSOLE_MSG = "I18N tokens for the app '%s' available at '%s'";
		public static final String APP_DOES_NOT_EXIST_EXCEPTION = "The app '%s' does not exist";
		public static final String NO_LOCALE_FOR_APP = "The app specified does not contain a default locale";
	}
	
	private BRJS brjs;
	private Logger logger;
	private String locale;
	boolean missingTokensExist = false;
	private TreeMap<String, HashMap<String, String>> allExistingTokens = new TreeMap<String, HashMap<String, String>>();
	private HashMap<String, Set<String>> missingTokensMap = new HashMap<String, Set<String>>();
	private Locale[] appLocales;
	private Pattern I18N_HTML_XML_TOKEN_PATTERN = Pattern.compile("@\\{(.*?)\\}");
	private Pattern JS_TOKEN_PATTERN = Pattern.compile("i18n\\([\\s]*[\'\"](.*?)[\'\"]([\\s]*[,+].*)?[\\s]*\\)");

	@Override
	public String getCommandName() {
		return "check-i18n";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application to search for missing translations"));
		argsParser.registerParameter(new UnflaggedOption("locale").setDefault("All").setHelp("the locale used to search tokens"));
	}	

	@Override
	public String getCommandDescription() {
		return "Show missing i18n translations for a given application/locale.";
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		locale = parsedArgs.getString("locale");
				
		listMissingTokens(appName);
		generateAllTranslationsCSV(appName);
		return missingTokensExist ? -1 : 0;
	}
	
	private void listMissingTokens(String appName) throws CommandArgumentsException {		
		App app = brjs.app(appName);
		
		try {
			appLocales = app.appConf().getLocales();
		} catch (ConfigException e) {
			e.printStackTrace();
		}
		
		if(!app.dirExists()) 
			throw new CommandArgumentsException( String.format(Messages.APP_DOES_NOT_EXIST_EXCEPTION, appName), this );
		
		findMissingTranslationsForAppWithLocale(app);
		
		for (Entry<String, Set<String>> tokensList : missingTokensMap.entrySet()) {
		    logMissingLocalesToConsole(appName, tokensList.getKey(), tokensList);
		}
	}

	private void generateAllTranslationsCSV(String appName) {	
		logger.println("\ngenerating CSV\n");
		
		TreeSet<String> localeNames = new TreeSet<String>();
		for(Locale locale : appLocales ){
			localeNames.add(locale.getLanguageCode());
		}
				
		List<String> headings = new ArrayList<String>();
		headings.add("Token");
		for(String localeName : localeNames){
			headings.add(localeName);
		}
		headings.add("IsUsed");
		
		List<List<String>> rows = new ArrayList<List<String>>();
		rows.add(headings);
		
		for(Entry<String, HashMap<String, String>> translationMap : allExistingTokens.entrySet()){
			List<String> newEntry = new ArrayList<String>();
			newEntry.add(translationMap.getKey());
			for(String localeName : localeNames){
				newEntry.add(translationMap.getValue().get(localeName));
			}
			newEntry.add(translationMap.getValue().get("used"));
			rows.add(newEntry);
		}
		
		File file = brjs.storageFile(this.getClass().getSimpleName(), appName + "-tokens.csv" );
		file.getParentFile().mkdirs();
		
		try(Writer writer = new BufferedWriter(new FileWriter(file));) {
			for(List<String> row : rows){
				writer.append(StringUtils.join(row, ",")+"\n");
			}
			writer.append("** the 'used' column only relates to tokens which have been used in their entirety"
				+ " and will not include tokens which are concatentated");
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.println(Messages.APP_CHECK_I18N_CONSOLE_MSG, appName, file.getAbsolutePath());		
	}		
		
	private void logMissingLocalesToConsole(String appName, String localeToBeChecked, Entry<String, Set<String>> tokensList) {
		String missingTokensMessage = tokensList.getValue().size() == 0 ? " has no missing translations" : " has no translations defined for the following tokens:";
		String firstLogLine = "\n" + "For the locale " + tokensList.getKey() + ", " + appName + missingTokensMessage + "\n";
		logger.println(firstLogLine);
		for (String missingToken : tokensList.getValue()) {
			logger.println(missingToken);
		}
	}

	private void findMissingTranslationsForAppWithLocale(App app) {
		for(Aspect aspect : app.aspects()) {
			logger.println("checking " + aspect.getName() + " aspect");
			checkMissingLocalsForBundlableNode(aspect);
			
			checkMissingLocales(aspect);
		}
		for(Bladeset bladeset : app.bladesets()) {
			logger.println("checking " + bladeset.getName() + " bladeset");
			checkMissingLocales(bladeset);
			for(Blade blade : bladeset.blades()) {
				logger.println("checking " + blade.getName() + " blade");
				checkMissingLocales(blade);
				checkMissingLocales(blade.workbench());
				checkMissingLocalsForBundlableNode(blade.workbench());	
			}
		}
	}

	private void checkMissingLocales(TestableNode testableNode) {
		for(TypedTestPack typedTestPack : testableNode.testTypes()){
			for(TestPack testPack : typedTestPack.testTechs()){
				checkMissingLocalsForBundlableNode(testPack);
			}
		}
	}

	private void checkMissingLocalsForBundlableNode(BundlableNode bundlableNode) {
		BundleSet bundleSet = null;
		try {
			bundleSet = bundlableNode.getBundleSet();
		} catch (ModelOperationException e) {
			e.printStackTrace();
		}	
		checkBundletForMissingTokens(bundleSet);
	}

	private void checkBundletForMissingTokens(BundleSet bundleSet) {
		List<Asset> assetList = new ArrayList<>();
		assetList.addAll(bundleSet.seedAssets());
		assetList.addAll(bundleSet.assets("html!"));
		assetList.addAll(bundleSet.assets("xml!"));
		for(Asset asset : assetList){
			String content = null;
			try {
				content = IOUtils.toString(asset.getReader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			checkAssetForMissingTokens(bundleSet, content, I18N_HTML_XML_TOKEN_PATTERN);
		}
		
		List<SourceModule> sourcceModules = bundleSet.sourceModules();
		for(SourceModule sourceModule : sourcceModules){
			String srcContent = null;
			try {
				srcContent = IOUtils.toString(sourceModule.getReader());
			} catch (IOException e) {
				e.printStackTrace();
			}
			checkAssetForMissingTokens(bundleSet, srcContent, JS_TOKEN_PATTERN);
		}
	}

	private void checkAssetForMissingTokens(BundleSet bundleSet, String content, Pattern pattern) {
		Locale[] appLocalesToBeChecked;
		appLocalesToBeChecked = locale == "All" ? appLocales : new Locale[] { new Locale(locale) };
		for(Locale localeToCheck: appLocalesToBeChecked ){
			Map<String,String> propertiesMap = null;
			try {
				propertiesMap = I18nPropertiesUtils.getI18nProperties(bundleSet, localeToCheck);
			} catch (ContentProcessingException e) {
				e.printStackTrace();
			}
			matchTokensForLocale(content, pattern, propertiesMap, localeToCheck.getLanguageCode());
		}
	}

	private void matchTokensForLocale(String content, Pattern pattern, Map<String, String> propertiesMap, String localeCode) {
		Matcher i18nTokenMatcher = pattern.matcher(content);
		Set<String> missingTokens = new HashSet<String>();
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
				String missingToken = tokenIsNotComplete ? i18nKey + "* a token beginning with this prefix could not be found" : i18nKey;
				missingTokens.add(missingToken);	
				populateAllTokensMap(localeCode, i18nKey, "", true);
				missingTokensExist = true;
			}
			else if (keyReplacement != null) {
				populateAllTokensMap(localeCode, i18nKey, keyReplacement, true);
			}
		}
		if(!missingTokensMap.containsKey(localeCode)){
			missingTokensMap.put(localeCode, missingTokens);
		}
		else{
			missingTokensMap.get(localeCode).addAll(missingTokens);
		}
		
		addUnusedTokensToAllTokensMap(propertiesMap, localeCode);		
	}

	private void addUnusedTokensToAllTokensMap(Map<String, String> propertiesMap, String localeCode) {
		for (Map.Entry<String, String> i18nPair : propertiesMap.entrySet())
		{
			String i18nKey = i18nPair.getKey();
			String translation = i18nPair.getValue();
			
			if(!allExistingTokens.containsKey(i18nKey)){
				populateAllTokensMap(localeCode, i18nKey, translation, false);					
			}
			allExistingTokens.get(i18nKey).put(localeCode, translation);
		}
	}

	private void populateAllTokensMap(String localeCode, String i18nKey, String keyReplacement, Boolean isUSed) {
		if(!allExistingTokens.containsKey(i18nKey)){
			HashMap<String, String> translations = new HashMap<String, String>();					
			allExistingTokens.put(i18nKey, translations);
		}
		allExistingTokens.get(i18nKey).put("used", isUSed.toString());
		allExistingTokens.get(i18nKey).put(localeCode, keyReplacement);
	}

	private boolean mapContainsPartialToken(Map<String, String> propertiesMap, String partialToken) {
		for(Entry<String, String> entry : propertiesMap.entrySet()) {
			  String i18nToken = entry.getKey();
			  if(i18nToken.contains(partialToken))
				  return true;
			}
		return false;
	}

	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());		
	}
}
