package org.bladerunnerjs.plugin.plugins.bundlers.cssresource;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.BundleSet;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.ResourcesAssetLocation;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.base.AbstractContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.ContentPathParserBuilder;

public class CssResourceContentPlugin extends AbstractContentPlugin {
	private static final String ASPECT = "aspect";
	private static final String BLADESET = "bladeset";
	private static final String BLADE = "blade";
	private static final String THEME = "theme";
	private static final String RESOURCE_PATH = "resourcePath";
	private static final String LIB = "lib";
	
	public static final String ASPECT_REQUEST = "aspect-request";
	public static final String BLADESET_REQUEST = "bladeset-request";
	public static final String BLADE_REQUEST = "blade-request";
	public static final String BLADE_WORKBENCH_REQUEST = "blade-workbench-request";
	public static final String LIB_REQUEST = "lib-request";
	
	private final ContentPathParser contentPathParser;
	
	
	//TODO: add aspect to URL
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("cssresource/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(ASPECT_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(BLADESET_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/blade_<"+BLADE+">/theme_<"+THEME+">/<"+RESOURCE_PATH+">").as(BLADE_REQUEST)
				.and("cssresource/bladeset_<"+BLADESET+">/blade_<"+BLADE+">/workbench/<"+RESOURCE_PATH+">").as(BLADE_WORKBENCH_REQUEST)
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
		// do nothing
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
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public List<String> getValidProdContentPaths(BundleSet bundleSet, String... locales) throws ContentProcessingException {
		return getValidContentPaths(bundleSet, locales);
	}
	
	@Override
	public void writeContent(ParsedContentPath contentPath, BundleSet bundleSet, OutputStream os) throws ContentProcessingException {
		BundlableNode bundlableNode = bundleSet.getBundlableNode();
		File resourceFile = null;
		
		if(contentPath.formName.equals(ASPECT_REQUEST)) {
			String theme = contentPath.properties.get(THEME);
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = ((ResourcesAssetLocation) bundlableNode.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if(contentPath.formName.equals(BLADESET_REQUEST)) {
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get(BLADESET));
			String theme = contentPath.properties.get(THEME);
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = ((ResourcesAssetLocation) bladeset.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if(contentPath.formName.equals(BLADE_REQUEST)) {
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get(BLADESET));
			Blade blade = bladeset.blade(contentPath.properties.get(BLADE));
			String theme = contentPath.properties.get(THEME);
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = ((ResourcesAssetLocation) blade.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if(contentPath.formName.equals(BLADE_WORKBENCH_REQUEST)) {
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get(BLADESET));
			Blade blade = bladeset.blade(contentPath.properties.get(BLADE));
			Workbench workbench = blade.workbench();
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = workbench.assetLocation("resources").file(resourcePath);
		}
		else if(contentPath.formName.equals(LIB_REQUEST)) {
			JsLib jsLib = bundlableNode.getApp().jsLib(contentPath.properties.get(LIB));
			String resourcePath = contentPath.properties.get(RESOURCE_PATH);
			
			resourceFile = jsLib.file(resourcePath);
		}
		
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
	
	private List<String> getValidContentPaths(BundleSet bundleSet, String... locales) {
		List<String> contentPaths = new ArrayList<>();
		
		for(AssetLocation assetLocation : bundleSet.getResourceNodes()) {
			AssetContainer assetContainer = assetLocation.getAssetContainer();
			
			if(assetContainer instanceof Aspect) {
			}
		}
		
		return contentPaths;
	}
}
