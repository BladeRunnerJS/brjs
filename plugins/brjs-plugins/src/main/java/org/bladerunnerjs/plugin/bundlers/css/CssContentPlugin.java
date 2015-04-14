package org.bladerunnerjs.plugin.bundlers.css;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.CharResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.FileAsset;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;


public class CssContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin {
	
	private final ContentPathParser contentPathParser;
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
	}
	
	@Override
	public String getRequestPrefix() {
		return "css";
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
			
			for(Asset cssAsset : getCssAssets(bundleSet)) {
				
				String themeName = getThemeName(cssAsset);
				
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
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor output, String version) throws MalformedRequestException, ContentProcessingException {
		// Using getContentPathParser as CT have there own content parser
		ParsedContentPath parsedContentPath = getContentPathParser().parse(contentPath);
		
		String theme = parsedContentPath.properties.get("theme");
		String languageCode = parsedContentPath.properties.get("languageCode");
		String countryCode = parsedContentPath.properties.get("countryCode");
		Locale locale = new Locale(languageCode, countryCode);

		List<Reader> readerList = new ArrayList<Reader>();
		
		for(Asset cssAsset : getCssAssets(bundleSet)) {
			String assetThemeName = getThemeName(cssAsset);
			
			if(assetThemeName.equals(theme) && cssAsset.getAssetName().matches(locale.getLocaleFilePattern(".*_", ".css"))) {
				CssRewriter processor = new CssRewriter(brjs, cssAsset);
				
				try {
					String css = processor.getRewrittenFileContents();
					readerList.add(new StringReader("\n\n\n/*** " + cssAsset.getAssetPath() + " ***/\n\n" + css));
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
	
	protected void orderCssAssets(List<Asset> cssAssets) {
		// do nothing, protected so the CT CSS plugin that uses a different CSS ordering can override it
	}
	
	private List<Asset> getCssAssets(BundleSet bundleSet) {
		List<Asset> cssAssets = bundleSet.getAssets( Arrays.asList("css!", "theme!"), Arrays.asList(FileAsset.class));
		orderCssAssets(cssAssets);
		return cssAssets;
	}
	
	private String getThemeName(Asset cssAsset) {
		String cssAssetRequirePath = cssAsset.getPrimaryRequirePath();
		if (cssAssetRequirePath.startsWith("theme!")) {
			return StringUtils.substringAfter( StringUtils.substringBefore(cssAssetRequirePath, ":"), "!");
		} else {
			return "common";
		}
	}
	
}
