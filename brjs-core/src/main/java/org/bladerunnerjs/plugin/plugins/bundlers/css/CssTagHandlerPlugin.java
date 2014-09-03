package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.ContentPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractTagHandlerPlugin;

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
	public void writeDevTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		try {
			writeTagContent(true, writer, bundleSet, tagAttributes, locale, version);
		}
		catch(MalformedTokenException | ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public void writeProdTagContent(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, Writer writer, String version) throws IOException {
		try {
			writeTagContent(false, writer, bundleSet, tagAttributes, locale, version);
		}
		catch(MalformedTokenException | ContentProcessingException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public List<String> getGeneratedDevRequests(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException
	{
		try {
			return getGeneratedRequests(true, tagAttributes, bundleSet, locale, version);
		}
		catch(IOException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	@Override
	public List<String> getGeneratedProdRequests(Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException
	{
		try {
			return getGeneratedRequests(false, tagAttributes, bundleSet, locale, version);
		}
		catch(IOException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	@Override
	public List<String> getDependentContentPluginRequestPrefixes()
	{
		return Arrays.asList( "css" );
	}
	
	private void writeTagContent(boolean isDev, Writer writer, BundleSet bundleSet, Map<String, String> tagAttributes, Locale locale, String version) throws IOException, ContentProcessingException, MalformedTokenException {
		for (StylesheetRequest stylesheet : getOrderedStylesheets(isDev, tagAttributes, bundleSet, locale, version)) {
			writeStylesheet(writer, stylesheet);
		}
	}

	public List<String> getGeneratedRequests(boolean isDev, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException, IOException
	{
		List<String> requests = new ArrayList<>();
		for (StylesheetRequest stylesheet : getOrderedStylesheets(isDev, tagAttributes, bundleSet, locale, version)) {
			requests.add( stylesheet.href );
		}
		return requests;
	}
	
	public List<StylesheetRequest> getOrderedStylesheets(boolean isDev, Map<String, String> tagAttributes, BundleSet bundleSet, Locale locale, String version) throws MalformedTokenException, ContentProcessingException, IOException
	{
		try {
			App app = bundleSet.getBundlableNode().app();
			String theme = getTheme(tagAttributes);
			List<String> alternateThemes = getAlternateThemes(tagAttributes);
			List<String> contentPaths = (isDev) ? cssContentPlugin.getValidDevContentPaths(bundleSet, locale) : cssContentPlugin.getValidProdContentPaths(bundleSet, locale);
			List<StylesheetRequest> stylesheetRequests = new ArrayList<>();
			
			if (theme == null && alternateThemes.size() == 0) {
				appendStylesheetRequestsForCommonTheme(stylesheetRequests, isDev, app, contentPaths, version, locale);
			} else if (theme != null) {
				appendStylesheetRequestsForCommonTheme(stylesheetRequests, isDev, app, contentPaths, version, locale);
				Matcher themeMatcher = VARIANT_THEME_PATTERN.matcher(theme);
				if (themeMatcher.matches()){
					String parentTheme = themeMatcher.group(1);
					try {
						appendStylesheetRequestsForMainTheme(stylesheetRequests, isDev, app, contentPaths, parentTheme, theme, version, locale);
					}
					catch (IOException e){
						logger.warn(Messages.NO_PARENT_THEME_FOUND_MESSAGE, theme, parentTheme);
					}
					appendStylesheetRequestsForVariantTheme(stylesheetRequests, isDev, app, contentPaths, theme, version, locale);
				} else {
					appendStylesheetRequestsForMainTheme(stylesheetRequests, isDev, app, contentPaths, theme, theme, version, locale);
				}
			}
			
			for (String alternateTheme : alternateThemes) {
				appendStylesheetRequestsForAlternateTheme(stylesheetRequests, isDev, app, contentPaths, alternateTheme, version, locale);
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
	
	private void appendStylesheetRequestsForCommonTheme(List<StylesheetRequest> stylesheetRequests, boolean isDev, App app, List<String> contentPaths, String version, Locale locale) throws IOException, MalformedTokenException, MalformedRequestException {
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, COMMON_THEME_NAME)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(requestPath) );
			}
		}
	}
	
	private void appendStylesheetRequestsForMainTheme(List<StylesheetRequest> stylesheetRequests, boolean isDev, App app, List<String> contentPaths, String themeName, String themeTitle, String version, Locale locale) throws IOException, MalformedTokenException, MalformedRequestException {
		boolean foundTheme = false;
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, themeName)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(requestPath, themeTitle) );
				foundTheme = true;
			}
		}
		if (!foundTheme) {
			throw new IOException( String.format(Messages.UNKNOWN_THEME_EXCEPTION, themeName) );
		}
	}

	private void appendStylesheetRequestsForVariantTheme(List<StylesheetRequest> stylesheetRequests, boolean isDev, App app, List<String> contentPaths, String themeName, String version, Locale locale) throws MalformedRequestException, MalformedTokenException, IOException {
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, themeName)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(requestPath, themeName) );
			}
		}
	}
	
	private void appendStylesheetRequestsForAlternateTheme(List<StylesheetRequest> stylesheetRequests, boolean isDev, App app, List<String> contentPaths, String themeName, String version, Locale locale) throws IOException, MalformedTokenException, MalformedRequestException {
		boolean foundTheme = false;
		for(String contentPath : contentPaths) {
			if (localeMatches(contentPath, locale) && themeMatches(contentPath, themeName)) {
				String requestPath = getRequestPath(isDev, app, contentPath, version);
				stylesheetRequests.add( new StylesheetRequest(requestPath, themeName, true) );
				foundTheme = true;
			}
		}
		if (!foundTheme) {
			throw new IOException( String.format(Messages.UNKNOWN_THEME_EXCEPTION, themeName) );
		}
	}
	
	
	private String getRequestPath(boolean isDev, App app, String contentPath, String version) throws MalformedTokenException {
		return (isDev) ? app.createDevBundleRequest(contentPath, version) : app.createProdBundleRequest(contentPath, version); 
	}
	
	private boolean themeMatches(String contentPath, String themeName) throws MalformedRequestException {
		String contentPathTheme = cssContentPlugin.getContentPathParser().parse(contentPath).properties.get("theme");
		return contentPathTheme.equals(themeName);
	}
	
	private boolean localeMatches(String contentPath, Locale locale) throws MalformedRequestException {
		String contentPathLanguageCode = cssContentPlugin.getContentPathParser().parse(contentPath).properties.get("languageCode"); 
		String contentPathCountryCode = cssContentPlugin.getContentPathParser().parse(contentPath).properties.get("countryCode");
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
		String rel;
		String title;
		String href;
		public StylesheetRequest(String href) {
			this(href, null);
		}
		public StylesheetRequest(String href, String title) {
			this(href, title, false);
		}
		public StylesheetRequest(String href, String title, boolean isAlternate) {
			this.rel = (isAlternate) ? "alternate stylesheet" : "stylesheet";
			this.title = title;
			this.href = href;
		}
	}
	
}
