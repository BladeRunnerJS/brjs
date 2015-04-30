package org.bladerunnerjs.plugin.bundlers.cssresource;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.BundleSet;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedRequestException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.BinaryResponseContent;
import org.bladerunnerjs.api.plugin.Locale;
import org.bladerunnerjs.api.plugin.ResponseContent;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.api.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.api.BladesetWorkbench;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.api.BladeWorkbench;
import org.bladerunnerjs.plugin.bundlers.css.CssRewriter;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class CssResourceContentPlugin extends AbstractContentPlugin implements RoutableContentPlugin {	
	public static final String ASPECT_THEME_REQUEST = "aspect-theme-request";
	public static final String ASPECT_RESOURCE_REQUEST = "aspect-resource-request";
	public static final String BLADESET_THEME_REQUEST = "bladeset-theme-request";
	public static final String BLADESET_RESOURCE_REQUEST = "bladeset-resource-request";
	public static final String BLADE_THEME_REQUEST = "blade-theme-request";
	public static final String BLADE_RESOURCE_REQUEST = "blade-resource-request";
	public static final String BLADEWORKBENCH_RESOURCE_REQUEST = "bladeworkbench-resource-request";
	public static final String BLADESETWORKBENCH_RESOURCE_REQUEST = "bladesetworkbench-resource-request";
	public static final String LIB_REQUEST = "lib-request";
	public static final String WORKBENCH_THEME_REQUEST = "workbench-theme-request";
	
	private final ContentPathParser contentPathParser;
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("cssresource/aspect_<aspect>/theme_<theme>/<resourcePath>").as(ASPECT_THEME_REQUEST)
				.and("cssresource/aspect_<aspect>_resource/<resourcePath>").as(ASPECT_RESOURCE_REQUEST)
				.and("cssresource/bladeset_<bladeset>/theme_<theme>/<resourcePath>").as(BLADESET_THEME_REQUEST)
				.and("cssresource/bladeset_<bladeset>_resource/<resourcePath>").as(BLADESET_RESOURCE_REQUEST)
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/theme_<theme>/<resourcePath>").as(BLADE_THEME_REQUEST)
				.and("cssresource/bladeset_<bladeset>/blade_<blade>_resource/<resourcePath>").as(BLADE_RESOURCE_REQUEST)
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/workbench_resource/<resourcePath>").as(BLADEWORKBENCH_RESOURCE_REQUEST)
				.and("cssresource/bladeset_<bladeset>/workbench_resource/<resourcePath>").as(BLADESETWORKBENCH_RESOURCE_REQUEST)
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/workbench/theme_<theme>/<resourcePath>").as(WORKBENCH_THEME_REQUEST)
				.and("cssresource/lib_<lib>/<resourcePath>").as(LIB_REQUEST)
			.where("aspect").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("bladeset").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("blade").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("lib").hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and("resourcePath").hasForm(ContentPathParserBuilder.PATH_TOKEN)
				.and("theme").hasForm(ContentPathParserBuilder.NAME_TOKEN);
		
		contentPathParser = contentPathParserBuilder.build();
	}
	
	@Override
	public void setBRJS(BRJS brjs) {
		this.brjs = brjs;
	}
	
	@Override
	public String getRequestPrefix() {
		return "cssresource";
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException {
		List<String> contentPaths = new ArrayList<>();
		
		try
		{
			for(AssetContainer assetContainer : bundleSet.bundlableNode().scopeAssetContainers())
			{
				contentPaths.addAll( getValidContentPaths(assetContainer) );
			}
		}
		catch (MalformedTokenException | ConfigException ex)
		{
			throw new ContentProcessingException(ex);
		}
		
		return contentPaths;
	}
	
	@Override
	public List<String> getUsedContentPaths(BundleSet bundleSet, RequestMode requestMode, Locale... locales) throws ContentProcessingException
	{
		List<String> validContentPaths = getValidContentPaths(bundleSet, requestMode, locales);
		List<String> usedContentPaths = new ArrayList<>();
		
		// This is to protect against .less or .sass files referencing the file where we wont detect it since we only look at .css files
		for (String imageExtension : ImageIO.getReaderFormatNames()) {
			List<String> foundContentPaths = new ArrayList<>();
			for (String contentPath : validContentPaths) {
				if (contentPath.endsWith(imageExtension)) {
					foundContentPaths.add(contentPath);
				}
			}
			usedContentPaths.addAll(foundContentPaths);
			validContentPaths.removeAll(foundContentPaths);			
		}
		
		
		List<Asset> assetsToDetirmineUsedPaths = bundleSet.getAssets("css!", "theme!");
		assetsToDetirmineUsedPaths.addAll(bundleSet.bundlableNode().seedAssets());
		
		for (Asset asset : assetsToDetirmineUsedPaths) {
			filterUsedContentPaths(asset, validContentPaths, usedContentPaths);
			if (validContentPaths.isEmpty()) {
				break;
			}
		}
		
		return usedContentPaths;
	}

	private void filterUsedContentPaths(Asset asset, List<String> validContentPaths, List<String> usedContentPaths) throws ContentProcessingException {
		List<String> foundContentPaths = new ArrayList<>();
		String assetContents = getCssAssetFileContents(asset);
		for (String contentPath : validContentPaths) {
			if (assetContents.contains(contentPath)) {
				foundContentPaths.add(contentPath);
			}
		}
		usedContentPaths.addAll(foundContentPaths);
		validContentPaths.removeAll(foundContentPaths);
	}
	
	private String getCssAssetFileContents(Asset cssAsset) throws ContentProcessingException {
		try
		{
			return new CssRewriter(brjs, cssAsset).getRewrittenFileContents();
		}
		catch (ContentProcessingException | IOException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException {
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
		
		BundlableNode bundlableNode = bundleSet.bundlableNode();
		String theme = parsedContentPath.properties.get("theme");
		String resourcePath = parsedContentPath.properties.get("resourcePath");
		MemoizedFile resourceFile = null;
		
		if (parsedContentPath.formName.equals(ASPECT_THEME_REQUEST))
		{
			String aspectName = parsedContentPath.properties.get("aspect");
			Aspect aspect =  bundlableNode.app().aspect(aspectName);
			MemoizedFile themeDir = getThemeDir(aspect, theme);
			resourceFile = themeDir.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(ASPECT_RESOURCE_REQUEST))
		{
			String aspectName = parsedContentPath.properties.get("aspect");
			Aspect aspect =  bundlableNode.app().aspect(aspectName);
			resourceFile = aspect.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADESET_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			MemoizedFile themeDir = getThemeDir(bladeset, theme);
			resourceFile = themeDir.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADESET_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			resourceFile = bladeset.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADE_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(parsedContentPath.properties.get("blade"));
			MemoizedFile themeDir = getThemeDir(blade, theme);
			resourceFile = themeDir.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADE_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(parsedContentPath.properties.get("blade"));
			resourceFile = blade.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(WORKBENCH_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(parsedContentPath.properties.get("blade"));
			BladeWorkbench workbench = blade.workbench();
			MemoizedFile themeDir = getThemeDir(workbench, theme);
			resourceFile = themeDir.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADEWORKBENCH_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(parsedContentPath.properties.get("blade"));
			BladeWorkbench workbench = blade.workbench();
			resourceFile = workbench.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADESETWORKBENCH_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			BladesetWorkbench workbench = bladeset.workbench();
			resourceFile = workbench.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(LIB_REQUEST))
		{
			JsLib jsLib = bundlableNode.app().jsLib(parsedContentPath.properties.get("lib"));
			resourceFile = jsLib.file(resourcePath);
		}
		else
		{
			throw new ContentProcessingException("Cannot handle request with form name " + parsedContentPath.formName);
		}
		
		try
		{
			if (fileIgnoredByBrjsConfig(resourceFile)) {
				String relativePath = brjs.dir().getRelativePath(resourceFile);
				throw new FileNotFoundException("The file at '"+relativePath+"' is ignored by the BRJS configuration so cannot be served");
			}
			return new BinaryResponseContent( new FileInputStream(resourceFile) );	
		}
		catch (FileNotFoundException | ConfigException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	private boolean fileIgnoredByBrjsConfig(MemoizedFile resourceFile) throws ConfigException
	{
		String relativePath = brjs.dir().getRelativePath(resourceFile);
		for (String ignoredPath : brjs.bladerunnerConf().getIgnoredPaths()) {
			if (relativePath.contains(ignoredPath+"/") || relativePath.endsWith(ignoredPath)) {
				return true;
			}
		}
		return false;
	}
	
	private List< String> getValidContentPaths(AssetContainer assetContainer) throws MalformedTokenException, ConfigException
	{		
		List<String> contentPaths = new ArrayList<>();
		
		String themeRequestName = "";
		String resourcesRequestName = "";
		String[] requestArgs = new String[0];
		
		
		if (assetContainer instanceof Aspect)
		{
			Aspect aspect = (Aspect) assetContainer;
			themeRequestName = ASPECT_THEME_REQUEST;
			resourcesRequestName = ASPECT_RESOURCE_REQUEST;
			requestArgs = new String[] { aspect.getName() };
		}
		else if (assetContainer instanceof Bladeset)
		{
			Bladeset bladeset = (Bladeset) assetContainer;
			themeRequestName = BLADESET_THEME_REQUEST;
			resourcesRequestName = BLADESET_RESOURCE_REQUEST;
			requestArgs = new String[] { bladeset.getName() };
		}
		else if (assetContainer instanceof Blade)
		{
			Blade blade = (Blade) assetContainer;
			Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
			themeRequestName = BLADE_THEME_REQUEST;
			resourcesRequestName = BLADE_RESOURCE_REQUEST;
			requestArgs = new String[] { bladeset.getName(), blade.getName() };
		}
		else if (assetContainer instanceof BladeWorkbench)
		{
			BladeWorkbench workbench = (BladeWorkbench) assetContainer;
			Blade blade = brjs.locateAncestorNodeOfClass(workbench, Blade.class);
			Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
			themeRequestName = WORKBENCH_THEME_REQUEST;
			resourcesRequestName = BLADEWORKBENCH_RESOURCE_REQUEST;
			requestArgs = new String[] { bladeset.getName(), blade.getName() };
		}
		else if (assetContainer instanceof BladesetWorkbench)
		{
			BladesetWorkbench workbench = (BladesetWorkbench) assetContainer;
			Bladeset bladeset = brjs.locateAncestorNodeOfClass(workbench, Bladeset.class);
			resourcesRequestName = BLADESETWORKBENCH_RESOURCE_REQUEST;
			requestArgs = new String[] { bladeset.getName() };
		}
		else if (assetContainer instanceof JsLib)
		{
			JsLib lib = (JsLib) assetContainer;
			themeRequestName = null;
			resourcesRequestName = LIB_REQUEST;
			requestArgs = new String[] { lib.getName() };
		}
		else {
			return contentPaths;
		}
		
		contentPaths.addAll(calculateContentPathsForThemesAndResources(assetContainer, themeRequestName, resourcesRequestName, requestArgs));
		
		return contentPaths;
	}
	
	
	private Set<String> calculateContentPathsForThemesAndResources(AssetContainer container, String themeRequestName, String resourcesRequestName, String... requestArgs) throws MalformedTokenException, ConfigException
	{
		Set<String> themeContentPaths = new LinkedHashSet<>();
		Set<String> resourceContentPaths = new LinkedHashSet<>();
		for (Asset dirAsset : getDirectoryAssets(container)) {
			for (MemoizedFile assetFile : dirAsset.file().files()) {
				if (fileIgnoredByBrjsConfig(assetFile)) {
					continue;
				} else {
					appendContentPath(themeContentPaths, resourceContentPaths, dirAsset, assetFile, container, themeRequestName, resourcesRequestName, requestArgs);
				}
			}
		}
		Set<String> contentPaths = new LinkedHashSet<>();
		contentPaths.addAll(resourceContentPaths);
		contentPaths.addAll(themeContentPaths);
		return contentPaths;
	}
	
	private void appendContentPath(Set<String> themeContentPaths, Set<String> resourceContentPaths, Asset dirAsset, MemoizedFile assetFile, 
			AssetContainer container, String themeRequestName, String resourcesRequestName, String[] requestArgs) throws MalformedTokenException
	{
		String dirAssetRequestPath = dirAsset.getPrimaryRequirePath();
		if (dirAssetRequestPath.contains("theme!")) {
			String themeName = StringUtils.substringBefore( StringUtils.substringAfter(dirAssetRequestPath, "!"), ":" );
			String assetPath = getThemeDir(container, themeName).getRelativePath(assetFile);
			String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { themeName, assetPath } );
			themeContentPaths.add( contentPathParser.createRequest(themeRequestName, createRequestArgs) );				
		} else {
			String assetPath = container.dir().getRelativePath(assetFile);
			String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { assetPath } );
			resourceContentPaths.add( contentPathParser.createRequest(resourcesRequestName, createRequestArgs) );				
		}
	}

	private List<Asset> getDirectoryAssets(AssetContainer container) {
		List<Asset> directoryAssets = new ArrayList<>();
		for (Asset asset : container.assets()) {
			if (asset.file().isDirectory()) {
				directoryAssets.add( asset );
			}
		}
		return directoryAssets;
	}
	
	private MemoizedFile getThemeDir(AssetContainer assetContainer, String themeName) {
		return assetContainer.file("themes/"+themeName);
	}
}
