package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.UrlContentAccessor;
import org.bladerunnerjs.model.FileInfo;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.BinaryResponseContent;
import org.bladerunnerjs.plugin.ResponseContent;
import org.bladerunnerjs.plugin.Locale;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.RelativePathUtility;

public class CssResourceContentPlugin extends AbstractContentPlugin {	
	public static final String ASPECT_THEME_REQUEST = "aspect-theme-request";
	public static final String ASPECT_RESOURCE_REQUEST = "aspect-resource-request";
	public static final String BLADESET_THEME_REQUEST = "bladeset-theme-request";
	public static final String BLADESET_RESOURCE_REQUEST = "bladeset-resource-request";
	public static final String BLADE_THEME_REQUEST = "blade-theme-request";
	public static final String BLADE_RESOURCE_REQUEST = "blade-resource-request";
	public static final String WORKBENCH_THEME_REQUEST = "workbench-theme-request";
	public static final String WORKBENCH_RESOURCE_REQUEST = "workbench-resource-request";
	public static final String LIB_REQUEST = "lib-request";
	
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
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/workbench/theme_<theme>/<resourcePath>").as(WORKBENCH_THEME_REQUEST)
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/workbench_resource/<resourcePath>").as(WORKBENCH_RESOURCE_REQUEST)
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
	public String getCompositeGroupName() {
		return null;
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
	public ResponseContent handleRequest(ParsedContentPath contentPath, BundleSet bundleSet, UrlContentAccessor contentAccessor, String version) throws ContentProcessingException {
		
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		String theme = contentPath.properties.get("theme");
		String resourcePath = contentPath.properties.get("resourcePath");
		File resourceFile = null;
		
		if (contentPath.formName.equals(ASPECT_THEME_REQUEST))
		{
			String aspectName = contentPath.properties.get("aspect");
			Aspect aspect =  bundlableNode.app().aspect(aspectName);
			List<ResourcesAssetLocation> resourceAssetLocations = getResourceAssetLocations(aspect);
			for (ResourcesAssetLocation location : resourceAssetLocations) {
				if (location.getThemeName().equals(theme)){
					resourceFile = location.file(resourcePath);
				}
			}
		}
		else if (contentPath.formName.equals(ASPECT_RESOURCE_REQUEST))
		{
			String aspectName = contentPath.properties.get("aspect");
			Aspect aspect =  bundlableNode.app().aspect(aspectName);
			resourceFile = aspect.file(resourcePath);
		}
		else if (contentPath.formName.equals(BLADESET_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(contentPath.properties.get("bladeset"));
			ThemedAssetLocation location = getThemedResourceLocation(bladeset, theme);
			resourceFile = location.file(resourcePath);
		}
		else if (contentPath.formName.equals(BLADESET_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(contentPath.properties.get("bladeset"));
			resourceFile = bladeset.file(resourcePath);
		}
		else if (contentPath.formName.equals(BLADE_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(contentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(contentPath.properties.get("blade"));
			ThemedAssetLocation location = getThemedResourceLocation(blade, theme);
			resourceFile = location.file(resourcePath);
		}
		else if (contentPath.formName.equals(BLADE_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(contentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(contentPath.properties.get("blade"));
			resourceFile = blade.file(resourcePath);
		}
		else if (contentPath.formName.equals(WORKBENCH_THEME_REQUEST))
		{
			//TODO: this needs implementing
			// Workbenches dont have themes ?
		}
		else if (contentPath.formName.equals(WORKBENCH_RESOURCE_REQUEST))
		{
			Bladeset bladeset = bundlableNode.app().bladeset(contentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(contentPath.properties.get("blade"));
			Workbench workbench = blade.workbench();
			resourceFile = workbench.file(resourcePath);
		}
		else if (contentPath.formName.equals(LIB_REQUEST))
		{
			JsLib jsLib = bundlableNode.app().jsLib(contentPath.properties.get("lib"));
			resourceFile = jsLib.file(resourcePath);
		}
		else
		{
			throw new ContentProcessingException("Cannot handle request with form name " + contentPath.formName);
		}
		
		try
		{
			if (fileIgnoredByBrjsConfig(resourceFile)) {
				String relativePath = RelativePathUtility.get(brjs.getFileInfoAccessor(), brjs.dir(), resourceFile);
				throw new FileNotFoundException("The file at '"+relativePath+"' is ignored by the BRJS configuration so cannot be served");
			}
			return new BinaryResponseContent( new FileInputStream(resourceFile) );	
		}
		catch (FileNotFoundException | ConfigException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	private boolean fileIgnoredByBrjsConfig(File resourceFile) throws ConfigException
	{
		String relativePath = RelativePathUtility.get(brjs.getFileInfoAccessor(), brjs.dir(), resourceFile);
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
		else if (assetContainer instanceof Workbench)
		{
			Workbench workbench = (Workbench) assetContainer;
			Blade blade = brjs.locateAncestorNodeOfClass(workbench, Blade.class);
			Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
			themeRequestName = WORKBENCH_THEME_REQUEST;
			resourcesRequestName = WORKBENCH_RESOURCE_REQUEST;
			requestArgs = new String[] { bladeset.getName(), blade.getName() };
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
			File assetLocationDir = assetLocation.dir();
			FileInfo assetLocationDirInfo = brjs.getFileInfo(assetLocationDir);
			if (assetLocationDirInfo.isDirectory()){
				for (File file : assetLocationDirInfo.nestedFiles()) {
					if (!fileIgnoredByBrjsConfig(file)) {
						createRequestForNestedDir(container, themeRequestName, resourcesRequestName, contentPaths, assetLocation, file, requestArgs);
					}
				}
			}
		}
		
		return contentPaths;
	}

	private void createRequestForNestedDir(AssetContainer container, String themeRequestName, String resourcesRequestName, Set<String> contentPaths, AssetLocation assetLocation, File file, String... requestArgs) throws MalformedTokenException
	{
		File assetLocationParentDir = assetLocation.dir().getParentFile();
		//TODO: this is wrong, it relies on knowledge of the app structure which should be in the model. How do we tell if an asset location is inside 'themes'?
		if (assetLocation instanceof ThemedAssetLocation && assetLocationParentDir.getName().equals("themes")) {
			if (themeRequestName != null) {
				ThemedAssetLocation themeAssetLocation = (ThemedAssetLocation) assetLocation;
				String assetPath = RelativePathUtility.get(brjs.getFileInfoAccessor(), assetLocation.dir(), file);
				String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { themeAssetLocation.getThemeName(), assetPath } );
				String request = contentPathParser.createRequest(themeRequestName, createRequestArgs);
				contentPaths.add(request );
			}
		} else {
			if (resourcesRequestName != null) {
				String assetPath = RelativePathUtility.get(brjs.getFileInfoAccessor(), container.dir(), file);
				String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { assetPath } );
				contentPaths.add( contentPathParser.createRequest(resourcesRequestName, createRequestArgs) );
			}
		}
	}
}
