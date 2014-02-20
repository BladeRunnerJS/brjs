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
	private final ContentPathParser contentPathParser;
	
	{
		ContentPathParserBuilder contentPathParserBuilder = new ContentPathParserBuilder();
		contentPathParserBuilder
			.accepts("cssresource/theme_<theme>/<resourcePath>").as("aspect-request")
				.and("cssresource/bladeset_<bladeset>/theme_<theme>/<resourcePath>").as("bladeset-request")
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/theme_<theme>/<resourcePath>").as("blade-request")
				.and("cssresource/bladeset_<bladeset>/blade_<blade>/workbench/<resourcePath>").as("blade-workbench-request")
				.and("cssresource/lib_<lib>/<resourcePath>").as("lib-request")
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
		
		if(contentPath.formName.equals("aspect-request")) {
			String theme = contentPath.properties.get("theme");
			String resourcePath = contentPath.properties.get("resourcePath");
			
			resourceFile = ((ResourcesAssetLocation) bundlableNode.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if(contentPath.formName.equals("bladeset-request")) {
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get("bladeset"));
			String theme = contentPath.properties.get("theme");
			String resourcePath = contentPath.properties.get("resourcePath");
			
			resourceFile = ((ResourcesAssetLocation) bladeset.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if(contentPath.formName.equals("blade-request")) {
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(contentPath.properties.get("blade"));
			String theme = contentPath.properties.get("theme");
			String resourcePath = contentPath.properties.get("resourcePath");
			
			resourceFile = ((ResourcesAssetLocation) blade.assetLocation("resources")).theme(theme).file(resourcePath);
		}
		else if(contentPath.formName.equals("blade-workbench-request")) {
			Bladeset bladeset = bundlableNode.getApp().bladeset(contentPath.properties.get("bladeset"));
			Blade blade = bladeset.blade(contentPath.properties.get("blade"));
			Workbench workbench = blade.workbench();
			String resourcePath = contentPath.properties.get("resourcePath");
			
			resourceFile = workbench.assetLocation("resources").file(resourcePath);
		}
		else if(contentPath.formName.equals("lib-request")) {
			JsLib jsLib = bundlableNode.getApp().jsLib(contentPath.properties.get("lib"));
			String resourcePath = contentPath.properties.get("resourcePath");
			
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
