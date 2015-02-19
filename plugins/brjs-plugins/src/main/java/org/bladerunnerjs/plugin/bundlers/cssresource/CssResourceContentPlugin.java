package org.bladerunnerjs.plugin.bundlers.cssresource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.Asset;
import org.bladerunnerjs.api.AssetLocation;
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
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.plugin.bundlers.css.CssAssetPlugin;
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
			for(AssetContainer assetContainer : bundleSet.getBundlableNode().scopeAssetContainers())
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
		
		for (Asset cssAsset : bundleSet.getResourceFiles(brjs.plugins().assetPlugin(CssAssetPlugin.class))) {
			filterUsedContentPaths(cssAsset, validContentPaths, usedContentPaths, true);
			if (validContentPaths.isEmpty()) {
				break;
			}
		}
		
		for (Asset seedAsset : bundleSet.getBundlableNode().seedAssets()) {
			filterUsedContentPaths(seedAsset, validContentPaths, usedContentPaths, false);
			if (validContentPaths.isEmpty()) {
				break;
			}
		}
		
		return usedContentPaths;
	}

	private void filterUsedContentPaths(Asset asset, List<String> validContentPaths, List<String> usedContentPaths, boolean rewriteUrls) throws ContentProcessingException {
		List<String> foundContentPaths = new ArrayList<>();
		String assetContents = (rewriteUrls) ? getCssAssetFileContents(asset) : readAssetToString(asset);
		for (String contentPath : validContentPaths) {
			if (assetContents.contains(contentPath)) {
				foundContentPaths.add(contentPath);
			}
		}
		usedContentPaths.addAll(foundContentPaths);
		validContentPaths.removeAll(foundContentPaths);
	}
	
	private String readAssetToString(Asset seedAsset) throws ContentProcessingException
	{
		try
		{
			return IOUtils.toString(seedAsset.getReader());
		}
		catch (IOException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	private String getCssAssetFileContents(Asset cssAsset) throws ContentProcessingException {
		try
		{
			return new CssRewriter(cssAsset).getRewrittenFileContents();
		}
		catch (ContentProcessingException | IOException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	private ThemedAssetLocation getThemedResourceLocation(AssetContainer container, String themeName){
		
		ThemedAssetLocation result = null;
		for(AssetLocation location: container.assetLocations()){
			if (location instanceof ThemedAssetLocation){
				String locationThemeName = ((ThemedAssetLocation)location).getThemeName();
				if(locationThemeName.equals(themeName)){
					result = ((ThemedAssetLocation)location);
				}
			}
		}
		return result;
	}
	
	@Override
	public ResponseContent handleRequest(String contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws MalformedRequestException, ContentProcessingException {
		ParsedContentPath parsedContentPath = contentPathParser.parse(contentPath);
		
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		String theme = parsedContentPath.properties.get("theme");
		String resourcePath = parsedContentPath.properties.get("resourcePath");
		MemoizedFile resourceFile = null;
		
		if (parsedContentPath.formName.equals(ASPECT_THEME_REQUEST))
		{
			String aspectName = parsedContentPath.properties.get("aspect");
			Aspect aspect =  bundlableNode.app().aspect(aspectName);
			List<ResourcesAssetLocation> resourceAssetLocations = getResourceAssetLocations(aspect);
			for (ResourcesAssetLocation location : resourceAssetLocations) {
				if (location.getThemeName().equals(theme)){
					resourceFile = location.file(resourcePath);
				}
			}
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
			ThemedAssetLocation location = getThemedResourceLocation(bladeset, theme);
			resourceFile = location.file(resourcePath);
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
			ThemedAssetLocation location = getThemedResourceLocation(blade, theme);
			resourceFile = location.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(BLADE_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(parsedContentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(parsedContentPath.properties.get("blade"));
			resourceFile = blade.file(resourcePath);
		}
		else if (parsedContentPath.formName.equals(WORKBENCH_THEME_REQUEST))
		{
			//TODO: this needs implementing
			// Workbenches dont have themes ?
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
	
	private List<ResourcesAssetLocation> getResourceAssetLocations(AssetContainer container){
		
		List<ResourcesAssetLocation> result = new ArrayList<>();
		for (AssetLocation location: container.assetLocations()){
			if (location instanceof ResourcesAssetLocation) {
				result.add( (ResourcesAssetLocation) location );
			}
		}
		return result;
	}
	
	private Set<String> calculateContentPathsForThemesAndResources(AssetContainer container, String themeRequestName, String resourcesRequestName, String... requestArgs) throws MalformedTokenException, ConfigException
	{
		Set<String> contentPaths = new LinkedHashSet<>();
		for (ResourcesAssetLocation assetLocation : getResourceAssetLocations(container)){
			MemoizedFile assetLocationDir = brjs.getMemoizedFile( assetLocation.dir() );
			if (assetLocationDir.isDirectory()){
				for (MemoizedFile file : assetLocationDir.nestedFiles()) {
					if (!fileIgnoredByBrjsConfig(file)) {
						createRequestForNestedDir(container, themeRequestName, resourcesRequestName, contentPaths, assetLocation, file, requestArgs);
					}
				}
			}
		}
		
		return contentPaths;
	}

	private void createRequestForNestedDir(AssetContainer container, String themeRequestName, String resourcesRequestName, Set<String> contentPaths, AssetLocation assetLocation, MemoizedFile file, String... requestArgs) throws MalformedTokenException
	{
		File assetLocationParentDir = assetLocation.dir().getParentFile();
		//TODO: this is wrong, it relies on knowledge of the app structure which should be in the model. How do we tell if an asset location is inside 'themes'?
		if (assetLocation instanceof ThemedAssetLocation && assetLocationParentDir.getName().equals("themes")) {
			if (themeRequestName != null) {
				ThemedAssetLocation themeAssetLocation = (ThemedAssetLocation) assetLocation;
				String assetPath = assetLocation.dir().getRelativePath(file);
				String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { themeAssetLocation.getThemeName(), assetPath } );
				String request = contentPathParser.createRequest(themeRequestName, createRequestArgs);
				contentPaths.add(request );
			}
		} else {
			if (resourcesRequestName != null) {
				String assetPath = container.dir().getRelativePath(file);
				String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { assetPath } );
				contentPaths.add( contentPathParser.createRequest(resourcesRequestName, createRequestArgs) );
			}
		}
	}
}
