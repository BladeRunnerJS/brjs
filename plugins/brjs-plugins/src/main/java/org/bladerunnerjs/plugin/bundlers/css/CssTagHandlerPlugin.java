package org.bladerunnerjs.plugin.bundlers.css;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.ContentPlugin;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractTagHandlerPlugin;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.utility.ContentPathParser;

public class CssTagHandlerPlugin extends AbstractTagHandlerPlugin {
	
	public static class Messages {
		public static final String NO_PARENT_THEME_FOUND_MESSAGE = "Theme '%s' was requested but the parent theme '%s' was not found. If you wish to use variant themes create a parent theme, otherwise do not use the theme name format '<parent>-<variant>'";
		public static final String UNKNOWN_THEME_EXCEPTION = "The theme '%s' is not a valid theme that is available in the aspect, bladeset or blades.";
		public static final String INVALID_THEME_EXCEPTION = String.format("The attribute '%s' should only contain a single theme and cannot contain spaces.", CssTagHandlerPlugin.THEME_ATTRIBUTE);
		
	}
	
	private ContentPlugin cssContentPlugin;
	private Logger logger;
	private Pattern VARIANT_THEME_PATTERN = Pattern.compile("([a-zA-Z0-9\\-]+)-(.*)");
	
	private static String COMMON_THEME_NAME = "common";
	private static String THEME_ATTRIBUTE = "theme";
	private static String ALT_THEME_ATTRIBUTE = "alternateTheme";
	@Override
	public void setBRJS(BRJS brjs)
	{
		cssContentPlugin = brjs.plugins().contentPlugin( getContentPluginRequirePrefix() );
		this.logger = brjs.logger(this.getClass());
	}
	
	// protected so the CT CSS plugin that uses a different CSS ordering can override it
	protected String getContentPluginRequirePrefix(){
		return "css";
	}
	
	@Override
	public String getTagName() {
		return "css.bundle";
	}
	
	@Override
	public void writeTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale, Writer writer, String version) throws IOException {
		try {
			for (StylesheetRequest stylesheet : getOrderedStylesheets(requestMode, tagAttributes, bundleSet, locale, version)) {
				writeStylesheet(writer, stylesheet);
			}
		}
		catch(MalformedTokenException | ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public List<String> getGeneratedContentPaths(Map<String, String> tagAttributes, BundleSet bundleSet, RequestMode requestMode, Locale locale) throws MalformedTokenException, ContentProcessingException
	{
		try {
			List<String> requests = new ArrayList<>();
			String version = bundleSet.bundlableNode().root().getAppVersionGenerator().getVersion();
			for (StylesheetRequest stylesheet : getOrderedStylesheets(requestMode, tagAttributes, bundleSet, locale, version)) {
				requests.add( stylesheet.contentPath );
			}
			return requests;
		}
		catch(IOException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	@Override
	public List<String> usedContentPluginRequestPrefixes()
	{
		return Arrays.asList( "css" );
	}
	
	public List<StylesheetRequest> getOrderedStylesheets(RequestMode requestMode, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException, IOException
	{
		try {
			App app = bundleSet.bundlableNode().app();
			String theme = getTheme(tagAttributes);
			List<String> alternateThemes = getAlternateThemes(tagAttributes);
			List<String> contentPaths = cssContentPlugin.getValidContentPaths(bundleSet, requestMode, locale);
			List<StylesheetRequest> stylesheetRequests = new ArrayList<>();
			
			if (theme == null && alternateThemes.size() == 0) {
				appendStylesheetRequestsForCommonTheme(stylesheetRequests, requestMode, app, contentPaths, version, locale);
			} else if (theme != null) {
				appendStylesheetRequestsForCommonTheme(stylesheetRequests, requestMode, app, contentPaths, version, locale);
				Matcher themeMatcher = VARIANT_THEME_PATTERN.matcher(theme);
				if (themeMatcher.matches()){
					String parentTheme = themeMatcher.group(1);
					try {
						appendStylesheetRequestsForMainTheme(stylesheetRequests, requestMode, app, contentPaths, parentTheme, theme, false, version, locale);
					}
					catch (IOException e){
						logger.warn(Messages.NO_PARENT_THEME_FOUND_MESSAGE, theme, parentTheme);
					}
					appendStylesheetRequestsForVariantTheme(stylesheetRequests, requestMode, app, contentPaths, theme, version, locale);
				} else {
					appendStylesheetRequestsForMainTheme(stylesheetRequests, requestMode, app, contentPaths, theme, theme, false, version, locale);
				}
			}
			
			for (String alternateTheme : alternateThemes) {
				Matcher themeMatcher = VARIANT_THEME_PATTERN.matcher(alternateTheme);
				if (themeMatcher.matches()){
					String parentTheme = themeMatcher.group(1);
					try {
						appendStylesheetRequestsForMainTheme(stylesheetRequests, requestMode, app, contentPaths, parentTheme, alternateTheme, true, version, locale);
					}
					catch (IOException e){
						logger.warn(Messages.NO_PARENT_THEME_FOUND_MESSAGE, alternateTheme, parentTheme);
					}
				}
				appendStylesheetRequestsForAlternateTheme(stylesheetRequests, requestMode, app, contentPaths, alternateTheme, version, locale);
			}
			
			return stylesheetRequests;
		}
		catch(MalformedTokenException | ContentProcessingException | MalformedRequestException e) {
			throw new IOException(e);
		}
	}
	
	private String getTheme(Map<String, String> tagAttributes) throws IOException {
		String themeName = tagAttributes.get(THEME_ATTRIBUTE);
		if (themeName != null && themeName.contains(",")) {
			throw new IOException( Messages.INVALID_THEME_EXCEPTION );
		}
		return themeName;
	}
	
	private List<String> getAlternateThemes(Map<String, String> tagAttributes) throws IOException {
		String alternateThemes = tagAttributes.get(ALT_THEME_ATTRIBUTE);
		if (alternateThemes == null) {
			return Arrays.asList();
		}
		return Arrays.asList( tagAttributes.get(ALT_THEME_ATTRIBUTE).split(",") );
	}
	
	private void appendStylesheetRequestsForCommonTheme(List<StylesheetRequest> stylesheetRequests, RequestMode requestMode, App app, List<String> contentPaths, String version, Locale locale) throws IOException, MalformedTokenException, MalformedRequestException {
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, COMMON_THEME_NAME)) {
				String requestPath = app.requestHandler().createRelativeBundleRequest(contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(contentPath, requestPath) );
			}
		}
	}
	
	private void appendStylesheetRequestsForMainTheme(List<StylesheetRequest> stylesheetRequests, RequestMode requestMode, App app, List<String> contentPaths, String themeName, String themeTitle, boolean isAlternate, String version, Locale locale) throws IOException, MalformedTokenException, MalformedRequestException {
		boolean foundTheme = false;
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, themeName)) {
				String requestPath = app.requestHandler().createRelativeBundleRequest(contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(contentPath, requestPath, themeTitle, isAlternate) );
				foundTheme = true;
			}
		}
		if (!foundTheme) {
			throw new IOException( String.format(Messages.UNKNOWN_THEME_EXCEPTION, themeName) );
		}
	}

	private void appendStylesheetRequestsForVariantTheme(List<StylesheetRequest> stylesheetRequests, RequestMode requestMode, App app, List<String> contentPaths, String themeName, String version, Locale locale) throws MalformedRequestException, MalformedTokenException, IOException {
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, themeName)) {
				String requestPath = app.requestHandler().createRelativeBundleRequest(contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(contentPath, requestPath, themeName) );
			}
		}
	}
	
	private void appendStylesheetRequestsForAlternateTheme(List<StylesheetRequest> stylesheetRequests, RequestMode requestMode, App app, List<String> contentPaths, String themeName, String version, Locale locale) throws IOException, MalformedTokenException, MalformedRequestException {
		boolean foundTheme = false;
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, themeName)) {
				String requestPath = app.requestHandler().createRelativeBundleRequest(contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(contentPath, requestPath, themeName, true) );
				foundTheme = true;
			}
		}
		if (!foundTheme) {
			throw new IOException( String.format(Messages.UNKNOWN_THEME_EXCEPTION, themeName) );
		}
	}
	
	private boolean themeMatches(String contentPath, String themeName) throws MalformedRequestException {
		String contentPathTheme = cssContentPlugin.castTo(RoutableContentPlugin.class).getContentPathParser().parse(contentPath).properties.get("theme");
		return contentPathTheme.equals(themeName);
	}
	
	private boolean localeMatches(String contentPath, Locale locale) throws MalformedRequestException {
		ContentPathParser cssContentPathParser = cssContentPlugin.castTo(RoutableContentPlugin.class).getContentPathParser();
		String contentPathLanguageCode = cssContentPathParser.parse(contentPath).properties.get("languageCode"); 
		String contentPathCountryCode = cssContentPathParser.parse(contentPath).properties.get("countryCode");
		Locale contentPathLocale = new Locale(contentPathLanguageCode, contentPathCountryCode);
		return ( contentPathLocale.isEmptyLocale() || locale.isAbsoluteOrPartialMatch(contentPathLocale) );
	}
	
	private void writeStylesheet(Writer writer, StylesheetRequest stylesheet) throws IOException
	{
		StringBuilder linkTagContent = new StringBuilder( "<link " );
		linkTagContent.append("rel=\""+stylesheet.rel+"\" ");
		if (stylesheet.title != null) {
			linkTagContent.append("title=\""+stylesheet.title+"\" ");			
		}
		linkTagContent.append("href=\""+stylesheet.href+"\"/>\n");
		writer.write( linkTagContent.toString() );
	}
	
	class StylesheetRequest {
		String contentPath;
		String rel;
		String title;
		String href;
		public StylesheetRequest(String contentPath, String href) {
			this(contentPath, href, null);
		}
		public StylesheetRequest(String contentPath, String href, String title) {
			this(contentPath, href, title, false);
		}
		public StylesheetRequest(String contentPath, String href, String title, boolean isAlternate) {
			this.contentPath = contentPath;
			this.rel = (isAlternate) ? "alternate stylesheet" : "stylesheet";
			this.title = title;
			this.href = href;
		}
	}
	
}
