package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.AssetPlugin;
import org.bladerunnerjs.plugin.CharResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class CssContentPlugin extends AbstractContentPlugin {
	
	private final ContentPathParser contentPathParser;
	private AssetPlugin cssAssetPlugin;
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("css/<theme>/bundle.css").as("simple-request")
				.and("css/<theme>_<languageCode>/bundle.css").as("language-request")
				.and("css/<theme>_<languageCode>_<countryCode>/bundle.css").as("locale-request")
			.where("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("languageCode").hasForm(Locale.LANGUAGE_CODE_FORMAT)
				.and("countryCode").hasForm(Locale.COUNTRY_CODE_FORMAT);
		
		contentPathParser =  contentPathParserBuilder.build();
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
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
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
	
	@Override
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws ContentProcessingException {
		
		String theme = contentPath.properties.get("theme");
		String languageCode = contentPath.properties.get("languageCode");
		String countryCode = contentPath.properties.get("countryCode");
		Locale locale = new Locale(languageCode, countryCode);

		List<Reader> readerList = new ArrayList<Reader>();
		List<Asset> cssAssets = getCssAssets(bundleSet, cssAssetPlugin);
		for(Asset cssAsset : cssAssets) {
			String assetThemeName = getThemeName(cssAsset.assetLocation());
			
			if(assetThemeName.equals(theme) && cssAsset.getAssetName().matches(locale.getLocaleFilePattern(".*_", ".css"))) {
				CssRewriter processor = new CssRewriter(cssAsset);
				
				try {
					String css = processor.getRewrittenFileContents();
					readerList.add(new StringReader(css));
				} catch (IOException e) {
					throw new ContentProcessingException(e);
				}
				readerList.add(new StringReader("\n"));
			}
		}
		
		return new CharResponseContent( brjs, readerList );
	}
	
	@Override
	public boolean outputAllBundles()
	{
		return false;
	}
	
	// protected so the CT CSS plugin that uses a different CSS ordering can override it
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
	
}
