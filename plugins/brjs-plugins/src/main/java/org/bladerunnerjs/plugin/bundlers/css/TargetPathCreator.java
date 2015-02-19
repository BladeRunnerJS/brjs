package org.bladerunnerjs.plugin.bundlers.css;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.Aspect;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.api.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.api.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.api.plugin.RoutableContentPlugin;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.plugin.bundlers.cssresource.CssResourceContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

public class TargetPathCreator
{
	private final BRJS brjs;
	private final ContentPathParser cssResourceContentPathParser;
	
	public TargetPathCreator(BRJS brjs) {
		this.brjs = brjs;
		cssResourceContentPathParser = brjs.plugins().contentPlugin("cssresource").castTo(RoutableContentPlugin.class).getContentPathParser();
	}
	
	public String getRelativeBundleRequestForImage(MemoizedFile imageFile) throws ContentProcessingException
	{
		return "../../" + getBundleRequestForImage(imageFile);
	}
	
	public String getBundleRequestForImage(MemoizedFile imageFile) throws ContentProcessingException
	{
		try 
		{
			return getTargetPath(imageFile);
		}
		catch(ContentProcessingException bundlerProcessingException)
		{
			throw bundlerProcessingException;
		}
		catch(Exception ex)
		{
			// TODO: understand how we can ever end up in here
			CssImageReferenceException cssImageReferenceException = new CssImageReferenceException(ex);
			cssImageReferenceException.setReferencedResourcePath(imageFile.getAbsolutePath());
			throw cssImageReferenceException;
		}
	}
	
	private String getTargetPath(MemoizedFile imageFile) throws ContentProcessingException
	{
		String targetPath = null;
		
		AssetContainer assetContainer = brjs.locateAncestorNodeOfClass( imageFile, AssetContainer.class );
		if (assetContainer == null) {
			throw new ContentFileProcessingException(imageFile, "File does not exist in a known scope");
		}
		
		try {
			if(assetContainer instanceof Aspect) {
				Aspect aspect = (Aspect) assetContainer;
				String relativePath = assetContainer.dir().getRelativePath(imageFile);
				String themeName = getThemeName(relativePath);
				String resourcePath = getResourcePath(relativePath);
				if (imageFile.isChildOf(assetContainer.file("themes"))) {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.ASPECT_THEME_REQUEST, aspect.getName(), themeName, resourcePath);
				} else {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.ASPECT_RESOURCE_REQUEST, aspect.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof Bladeset) {
				Bladeset bladeset = (Bladeset) assetContainer;
				String relativePath = assetContainer.dir().getRelativePath(imageFile);
				String themeName = getThemeName(relativePath);
				String resourcePath = getResourcePath(relativePath);
				if (imageFile.isChildOf(assetContainer.file("themes"))) {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESET_THEME_REQUEST, bladeset.getName(), themeName, resourcePath);
				} else {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESET_RESOURCE_REQUEST, bladeset.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof Blade) {
				Blade blade = (Blade) assetContainer;
				Bladeset bladeset = blade.parent();
				String relativePath = assetContainer.dir().getRelativePath(imageFile);
				String themeName = getThemeName(relativePath);
				String resourcePath = getResourcePath(relativePath);
				if (imageFile.isChildOf(assetContainer.file("themes"))) {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADE_THEME_REQUEST, bladeset.getName(), blade.getName(), themeName, resourcePath);
				} else {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADE_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof BladeWorkbench) {
				BladeWorkbench workbench = (BladeWorkbench) assetContainer;
				Blade blade = workbench.parent();
				Bladeset bladeset = blade.parent();
				String relativePath = assetContainer.dir().getRelativePath(imageFile);
				String resourcePath = getResourcePath(relativePath);
				if (imageFile.isChildOf(assetContainer.file("themes"))) {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADEWORKBENCH_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
				} else {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADEWORKBENCH_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof BladesetWorkbench) {
				BladesetWorkbench workbench = (BladesetWorkbench) assetContainer;
				Bladeset bladeset = workbench.parent();
				String relativePath = assetContainer.dir().getRelativePath(imageFile);
				String resourcePath = getResourcePath(relativePath);
				if (imageFile.isChildOf(assetContainer.file("themes"))) {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESETWORKBENCH_RESOURCE_REQUEST, bladeset.getName(), resourcePath);
				} else {
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESETWORKBENCH_RESOURCE_REQUEST, bladeset.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof JsLib) {
				JsLib jsLib = (JsLib) assetContainer;
				String resourcePath = jsLib.dir().getRelativePath(imageFile);
				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.LIB_REQUEST, jsLib.getName(), resourcePath);
			}
			else {
				throw new ContentFileProcessingException(imageFile, "File does not exist in a known scope");
			}
		}
		catch(MalformedTokenException e) {
			throw new ContentProcessingException(e);
		}
		
		return targetPath;
	}			
			
	private String getThemeName(String relativePath) {
		String[] relativePathSplit = relativePath.split("/");
		if (relativePathSplit.length < 2) {
			return "common";
		}
		return relativePathSplit[1];
	}
	
	private String getResourcePath(String relativePath) {
		String themesPath = "themes/"+getThemeName(relativePath)+"/";
		if ( relativePath.startsWith(themesPath) ) {
			return StringUtils.substringAfter(relativePath, themesPath);
		}
		return relativePath;
	}
	
}
