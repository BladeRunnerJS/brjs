package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.model.Asset;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.ContentOutputStream;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class CssContentPlugin extends AbstractContentPlugin {
	
	private final ContentPathParser contentPathParser;
	private BRJS brjs;
	private AssetPlugin cssAssetPlugin;
	
	{
		contentPathParser = createContentPathParser();
	}
	
	protected ContentPathParser createContentPathParser(){
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("css/<theme>/bundle.css").as("simple-request")
				.and("css/<theme>_<languageCode>/bundle.css").as("language-request")
				.and("css/<theme>_<languageCode>_<countryCode>/bundle.css").as("locale-request")
			.where("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("languageCode").hasForm(Locale.LANGUAGE_CODE_FORMAT)
				.and("countryCode").hasForm(Locale.COUNTRY_CODE_FORMAT);
		
		ContentPathParser result =  contentPathParserBuilder.build();
		return result;
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
		cssAssetPlugin = brjs.plugins().assetPlugin(CssAssetPlugin.class);
	}
	
	@Override
	public String getRequestPrefix() {
		return "css";
	}
	
	@Override
	public String getCompositeGroupName() {
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, ContentOutputStream os, String version) throws ContentProcessingException {
		String theme = contentPath.properties.get("theme");
		String languageCode = contentPath.properties.get("languageCode");
		String countryCode = contentPath.properties.get("countryCode");
		Locale locale = new Locale(languageCode, countryCode);
		
		try(Writer writer = new OutputStreamWriter(os, brjs.bladerunnerConf().getBrowserCharacterEncoding())) {
			
			List<Asset> cssAssets = getCssAssets(bundleSet, cssAssetPlugin);
			
			for(Asset cssAsset : cssAssets) {
				String assetThemeName = getThemeName(cssAsset.assetLocation());
				
				if(assetThemeName.equals(theme) && cssAsset.getAssetName().matches(locale.getLocaleFilePattern(".*_", ".css"))) {
					writeAsset(cssAsset, writer);
				}
			}
		}
		catch (IOException | ConfigException e) {
			throw new ContentProcessingException(e);
		}
	}
	
	protected List<Asset> getCssAssets(BundleSet bundleSet, AssetPlugin cssAssetPlugin){
		List<Asset> cssAssets = bundleSet.getResourceFiles(cssAssetPlugin);
		return cssAssets;
	}
	
	
	private String getThemeName(AssetLocation cssAssetLocation) {
		String themeName;
		
		if(cssAssetLocation instanceof ThemedAssetLocation) {
			themeName = ((ThemedAssetLocation) cssAssetLocation).getThemeName();
		}else {
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
	
	private List<String> getValidContentPaths(BundleSet bundleSet, Locale... locales) throws ContentProcessingException {
		Set<String> contentPaths = new LinkedHashSet<>();
		
		try {
			List<Locale> supportedLocales = Arrays.asList(bundleSet.getBundlableNode().app().appConf().getLocales());
			
			for(Asset cssAsset : bundleSet.getResourceFiles(cssAssetPlugin)) {
				AssetLocation cssAssetLocation = cssAsset.assetLocation();
				String themeName = (cssAssetLocation instanceof ThemedAssetLocation) ? ((ThemedAssetLocation) cssAssetLocation).getThemeName() : "common";
				
				Locale assetLocale = Locale.createLocaleFromFilepath(".*_", cssAsset.getAssetName());
				
				if(assetLocale.isEmptyLocale()) {
					contentPaths.add(getContentPathParser().createRequest("simple-request", themeName));
				}
				else {
					if(supportedLocales.contains(assetLocale)) {
						if (!assetLocale.isCompleteLocale()) {
							contentPaths.add(getContentPathParser().createRequest("language-request", themeName, assetLocale.getLanguageCode()));
						} else {
							contentPaths.add(getContentPathParser().createRequest("locale-request", themeName, assetLocale.getLanguageCode(), assetLocale.getCountryCode()));
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
	
}
