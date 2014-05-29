package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ThemesAssetLocation;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRJSConformantAssetLocationPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class CssContentPlugin extends AbstractContentPlugin {
	private static final Pattern LOCALE_PATTERN = Pattern.compile("^.*_([a-z]{2}_[A-Z]{2})\\.css$");
	private static final Pattern LANGUAGE_PATTERN = Pattern.compile("^.*_([a-z]{2})\\.css$");
	private final ContentPathParser contentPathParser;
	private BRJS brjs;
	private AssetPlugin cssAssetPlugin;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("css/<theme>/bundle.css").as("simple-request")
				.and("css/<theme>_<languageCode>/bundle.css").as("language-request")
				.and("css/<theme>_<languageCode>_<countryCode>/bundle.css").as("locale-request")
			.where("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("languageCode").hasForm("[a-z]{2}")
				.and("countryCode").hasForm("[A-Z]{2}");
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		cssAssetPlugin = brjs.plugins().assetProducer(CssAssetPlugin.class);
	}
	
	@Override
	public String getRequestPrefix() {
		return "css";
	}
	
	@Override
	public String getGroupName() {
		return "text/css";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException {
		String theme = contentPath.properties.get("theme");
		String languageCode = contentPath.properties.get("languageCode");
		String countryCode = contentPath.properties.get("countryCode");
		String locale = null;
		
		if (languageCode != null && countryCode != null) {
			locale = languageCode + "_" + countryCode;
		}
		else if (languageCode != null) {
			locale = languageCode;
		}
		
		String pattern = getFilePattern(locale, null);
		
		try(Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding())) {
			List<Asset> cssAssets = bundleSet.getResourceFiles(cssAssetPlugin);
			for(Asset cssAsset : cssAssets) {
				String assetThemeName = getThemeName(cssAsset.assetLocation());
				
				if(assetThemeName.equals(theme) && cssAsset.getAssetName().matches(pattern)) {
					writeAsset(cssAsset, writer);
				}
			}
		}
		catch (IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	private String getThemeName(AssetLocation cssAssetLocation) {
		String themeName;
		
		if(cssAssetLocation instanceof ThemesAssetLocation) {
			themeName = ((ThemesAssetLocation) cssAssetLocation).getThemeName();
		}
		else {
			themeName = "common";
		}
		
		return themeName;
	}
	
	private void writeAsset(Asset cssAsset, Writer writer) throws ContentProcessingException {
		try {
			CssRewriter processor = new CssRewriter(cssAsset);
			writer.append(processor.getFileContents());
			writer.write("\n");
		}
		catch (IOException e) {
			throw new ContentProcessingException(e, "Error while bundling asset '" + cssAsset.getAssetPath() + "'.");
		}
	}
	
	private List<String> getValidContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		Set<String> contentPaths = new LinkedHashSet<>();
		
		try {
			Set<String> supportedThemes = new HashSet<>(BRJSConformantAssetLocationPlugin.getBundlableNodeThemes(bundleSet.getBundlableNode()));
			Set<String> supportedLocales = new HashSet<>(Arrays.asList(bundleSet.getBundlableNode().app().appConf().getLocales()));
			
			for(Asset cssAsset : bundleSet.getResourceFiles(cssAssetPlugin)) {
				AssetLocation cssAssetLocation = cssAsset.assetLocation();
				String themeName = (cssAssetLocation instanceof ThemesAssetLocation) ? ((ThemesAssetLocation) cssAssetLocation).getThemeName() : "common";
				
				if(supportedThemes.contains(themeName)) {
					String assetLocale = getAssetLocale(cssAsset.getAssetName());
					
					if(assetLocale == null) {
						contentPaths.add(contentPathParser.createRequest("simple-request", themeName));
					}
					else {
						if(supportedLocales.contains(assetLocale)) {
							if(!assetLocale.contains("_")) {
								contentPaths.add(contentPathParser.createRequest("language-request", themeName, assetLocale));
							}
							else {
								String[] parts = assetLocale.split("_");
								String language = parts[0];
								String country = parts[1];
								
								contentPaths.add(contentPathParser.createRequest("locale-request", themeName, language, country));
							}
						}
					}
				}
			}
		}
		catch(MalformedTokenException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
		
		return new ArrayList<>(contentPaths);
	}
	
	private String getAssetLocale(String assetName) {
		String locale;
		Matcher localePatternMatcher = LOCALE_PATTERN.matcher(assetName);
		
		if(localePatternMatcher.matches()) {
			locale = localePatternMatcher.group(1);
		}
		else {
			Matcher languagePatternMatcher = LANGUAGE_PATTERN.matcher(assetName);
			
			if(languagePatternMatcher.matches()) {
				locale = languagePatternMatcher.group(1);
			}
			else {
				locale = null;
			}
		}
		
		return locale;
	}

	private String getFilePattern(String locale, String browser) {
		String pattern = "";
		if (locale != null) {
			// .*_en_GB.css
			pattern = ".*_" + locale;
		} else if (browser != null) {
			// .*_ie7.css
			pattern = ".*_" + browser;
		} else {
			// If we are looking for a CSS file without the locale or browser,
			// then
			// we can assume that it does not have underscores in it.
			pattern = "[^_]+";
		}
		
		return pattern + "\\.css";
	}
}
