package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.Theme;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.engine.ThemeableNode;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;
import org.bladerunnerjs.utility.RelativePathUtility;

public class CssResourceContentPlugin extends AbstractContentPlugin {
	private static final String ASPECT = "aspect";
	private static final String BLADESET = "bladeset";
	private static final String BLADE = "blade";
	private static final String THEME = "theme";
	private static final String RESOURCE_PATH = "resourcePath";
	private static final String LIB = "lib";
	
	public static final String ASPECT_THEME_REQUEST = "aspect-theme-request";
	public static final String ASPECT_RESOURCES_REQUEST = "aspect-resources-request";
	public static final String BLADESET_THEME_REQUEST = "bladeset-theme-request";
	public static final String BLADESET_RESOURCES_REQUEST = "bladeset-resources-request";
	public static final String BLADE_THEME_REQUEST = "blade-theme-request";
	public static final String BLADE_RESOURCES_REQUEST = "blade-resources-request";
	public static final String WORKBENCH_THEME_REQUEST = "workbench-theme-request";
	public static final String WORKBENCH_RESOURCES_REQUEST = "workbench-resources-request";
	public static final String LIB_REQUEST = "lib-request";
	
	private final ContentPathParser contentPathParser;
	private BRJS brjs;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("cssresource/aspect_<"+ASPECT+">/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(ASPECT_THEME_REQUEST)
				.and("cssresource/aspect_<"+ASPECT+">/resources/<"+RESOURCE_PATH+">").as(ASPECT_RESOURCES_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(BLADESET_THEME_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/resources/<"+RESOURCE_PATH+">").as(BLADESET_RESOURCES_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/blade_<"+BLADE+">/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(BLADE_THEME_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/blade_<"+BLADE+">/resources/<"+RESOURCE_PATH+">").as(BLADE_RESOURCES_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/blade_<"+BLADE+">/workbench/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(WORKBENCH_THEME_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/blade_<"+BLADE+">/workbench/resources/<"+RESOURCE_PATH+">").as(WORKBENCH_RESOURCES_REQUEST)
				.and("cssresource/lib_<"+LIB+">/<"+RESOURCE_PATH+">").as(LIB_REQUEST)
			.where(ASPECT).hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and(BLADESET).hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and(BLADE).hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and(LIB).hasForm(ContentPathParserBuilder.NAME_TOKEN)
				.and(RESOURCE_PATH).hasForm(ContentPathParserBuilder.PATH_TOKEN)
				.and(THEME).hasForm(ContentPathParserBuilder.NAME_TOKEN);
		
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
	public String getGroupName() {
		return null;
	}
	
	@Override
	public ContentPathParser getContentPathParser() {
		return contentPathParser;
	}
	
	@Override
	public List<String> getValidDevContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		try
		{
			return getValidContentPaths(bundleSet.getBundlableNode().getApp(), locales);
		}
		catch (MalformedTokenException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		try
		{
			return getValidContentPaths(bundleSet.getBundlableNode().getApp(), locales);
		}
		catch (MalformedTokenException ex)
		{
			throw new ContentProcessingException(ex);
		}
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException {
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		File resourceFile = null;
		
		if (contentPath.formName.equals(ASPECT_THEME_REQUEST))
		{
			String theme = contentPath.properties.get(THEME);
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = ((ResourcesAssetLocation) bundlableNode.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if (contentPath.formName.equals(ASPECT_RESOURCES_REQUEST))
		{
			//TODO: this needs implementing
		}
		else if (contentPath.formName.equals(BLADESET_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get(BLADESET));
			String theme = contentPath.properties.get(THEME);
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = ((ResourcesAssetLocation) bladeset.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if (contentPath.formName.equals(BLADESET_RESOURCES_REQUEST))
		{
			//TODO: this needs implementing
		}
		else if (contentPath.formName.equals(BLADE_THEME_REQUEST))
		{
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get(BLADESET));
			Blade blade = bladeset.blade(contentPath.properties.get(BLADE));
			String theme = contentPath.properties.get(THEME);
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = ((ResourcesAssetLocation) blade.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if (contentPath.formName.equals(BLADE_RESOURCES_REQUEST))
		{
			//TODO: this needs implementing
		}
		else if (contentPath.formName.equals(WORKBENCH_THEME_REQUEST))
		{
			//TODO: this needs implementing
		}
		else if (contentPath.formName.equals(WORKBENCH_RESOURCES_REQUEST))
		{
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get(BLADESET));
			Blade blade = bladeset.blade(contentPath.properties.get(BLADE));
			Workbench workbench = blade.workbench();
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = workbench.assetLocation("resources").file(resourcePath);
		}
		else if (contentPath.formName.equals(LIB_REQUEST))
		{
			JsLib jsLib = bundlableNode.getApp().jsLib(contentPath.properties.get(LIB));
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = jsLib.file(resourcePath);
		}
		else
		{
			throw new ContentProcessingException("Cannot handle request with form name " + contentPath.formName);
		}
		
		//TODO: remove this line once all request types are handled
		if (resourceFile == null) { return; }
		
		try(InputStream input = new FileInputStream(resourceFile);)
		{
			IOUtils.copy(input, os);
		}
		catch(IOException e) {
			throw new ContentProcessingException(e);
		}
		finally
		{
			// TODO: see if we can remove this flush() since there doesn't seem to be any particular reason for it
			try {
				os.flush();
			}
			catch (IOException e) {
				throw new ContentProcessingException(e);
			}
		}
	}
	
	private List<String> getValidContentPaths(App app, String... locales) throws MalformedTokenException, ContentProcessingException {
		List<String> contentPaths = new ArrayList<>();
		
		for(AssetContainer assetContainer : app.getAllAssetContainers())
		{
			contentPaths.addAll( getValidContentPaths(assetContainer) );
		}
		
		return contentPaths;
	}

	
	private List< String> getValidContentPaths(AssetContainer assetContainer) throws MalformedTokenException, ContentProcessingException
	{		
		List<String> contentPaths = new ArrayList<>();
		
		String themeRequestName = "";
		String resourcesRequestName = "";
		String[] requestArgs = new String[0];
		
		
		if (assetContainer instanceof Aspect)
		{
			Aspect aspect = (Aspect) assetContainer;
			themeRequestName = ASPECT_THEME_REQUEST;
			resourcesRequestName = ASPECT_RESOURCES_REQUEST;
			requestArgs = new String[] { aspect.getName() };
		}
		else if (assetContainer instanceof Bladeset)
		{
			Bladeset bladeset = (Bladeset) assetContainer;
			themeRequestName = BLADESET_THEME_REQUEST;
			resourcesRequestName = BLADESET_RESOURCES_REQUEST;
			requestArgs = new String[] { bladeset.getName() };
		}
		else if (assetContainer instanceof Blade)
		{
			Blade blade = (Blade) assetContainer;
			Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
			themeRequestName = BLADE_THEME_REQUEST;
			resourcesRequestName = BLADE_RESOURCES_REQUEST;
			requestArgs = new String[] { bladeset.getName(), blade.getName() };
		}
		else if (assetContainer instanceof Workbench)
		{
			Workbench workbench = (Workbench) assetContainer;
			Blade blade = brjs.locateAncestorNodeOfClass(workbench, Blade.class);
			Bladeset bladeset = brjs.locateAncestorNodeOfClass(blade, Bladeset.class);
			themeRequestName = WORKBENCH_THEME_REQUEST;
			resourcesRequestName = WORKBENCH_RESOURCES_REQUEST;
			requestArgs = new String[] { bladeset.getName(), blade.getName() };
		}
		else {
			throw new ContentProcessingException("Unable to get content paths for asset container of type " + assetContainer.getClass());
		}
		
		File resourcesDir = assetContainer.assetLocation("resources").dir();
		contentPaths.addAll(  calculateContentPathsForThemesAndResources((ThemeableNode)assetContainer, themeRequestName, resourcesDir, resourcesRequestName, requestArgs) );		
		
		return contentPaths;
	}
	
	private List<String> calculateContentPathsForThemesAndResources(ThemeableNode themeableNode, String themeRequestName, File resourcesDir, String resourcesRequestName, String... requestArgs) throws MalformedTokenException
	{
		List<String> contentPaths = new ArrayList<>();
		
		for (Theme theme : themeableNode.themes())
		{
    		File themeDir = theme.dir();
    		if (themeDir.isDirectory())
    		{
        		for (File file : brjs.getFileIterator(themeDir).nestedFiles())
        		{
        			if (file.isFile() && !file.getName().endsWith(".css"))
        			{
        				String assetPath = RelativePathUtility.get(themeDir, file);
        				String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { theme.getName(), assetPath } );
        				
        				contentPaths.add( contentPathParser.createRequest(themeRequestName, createRequestArgs) );
        			}
        		}
    		}
		}
		
		if (resourcesDir.isDirectory())
		{
			for (File file : brjs.getFileIterator(resourcesDir).nestedFiles())
			{
				if (file.isFile() && !file.getName().endsWith(".css"))
				{
					String assetPath = RelativePathUtility.get(resourcesDir, file);
    				String[] createRequestArgs = ArrayUtils.addAll( requestArgs, new String[] { assetPath } );
    				
					contentPaths.add( contentPathParser.createRequest(resourcesRequestName, createRequestArgs) );
				}
			}
		}
		
		return contentPaths;
	}
}
