package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.File;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ThemeAssetLocation;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.RelativePathUtility;

public class TargetPathCreator
{
	private final BRJS brjs;
	private final ContentPathParser cssResourceContentPathParser;
	
	public TargetPathCreator(BRJS brjs) {
		this.brjs = brjs;
		cssResourceContentPathParser = brjs.plugins().contentProvider("cssresource").getContentPathParser();
	}
	
	public String getRelativeBundleRequestForImage(File imageFile) throws BundlerProcessingException
	{
		return "../" + getBundleRequestForImage(imageFile);
	}
	
	public String getBundleRequestForImage(File imageFile) throws BundlerProcessingException
	{
		String targetPath = null;
		
		try 
		{
			targetPath = getTargetPath(imageFile);
		}
		catch(BundlerProcessingException bundlerProcessingException)
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
		
		return targetPath;
	}
	
	private String getTargetPath(File imageFile) throws BundlerProcessingException
	{
		AssetLocation assetLocation = (AssetLocation) brjs.locateFirstAncestorNode(imageFile);
		AssetContainer assetContainer = assetLocation.getAssetContainer();
		String targetPath = null;
		
		try {
			if(assetContainer instanceof Aspect) {
				ThemeAssetLocation theme = (ThemeAssetLocation) assetLocation;
				String resourcePath = RelativePathUtility.get(theme.dir(), imageFile);
				
				targetPath = cssResourceContentPathParser.createRequest("aspect-request", theme.dir().getName(), resourcePath);
			}
			else if(assetContainer instanceof Bladeset) {
				ThemeAssetLocation theme = (ThemeAssetLocation) assetLocation;
				String resourcePath = RelativePathUtility.get(theme.dir(), imageFile);
				Bladeset bladeset = (Bladeset) assetContainer;
				
				targetPath = cssResourceContentPathParser.createRequest("bladeset-request", bladeset.getName(), theme.dir().getName(), resourcePath);
			}
			else if(assetContainer instanceof Blade) {
				ThemeAssetLocation theme = (ThemeAssetLocation) assetLocation;
				String resourcePath = RelativePathUtility.get(theme.dir(), imageFile);
				Blade blade = (Blade) assetContainer;
				Bladeset bladeset = blade.parent();
				
				targetPath = cssResourceContentPathParser.createRequest("blade-request", bladeset.getName(), blade.getName(), theme.dir().getName(), resourcePath);
			}
			else if(assetContainer instanceof Workbench) {
				Workbench workbench = (Workbench) assetContainer;
				String resourcePath = RelativePathUtility.get(assetLocation.file("resources"), imageFile);
				Blade blade = workbench.parent();
				Bladeset bladeset = blade.parent();
				
				targetPath = cssResourceContentPathParser.createRequest("blade-workbench-request", bladeset.getName(), blade.getName(), resourcePath);
			}
			else if(assetContainer instanceof JsLib) {
				JsLib jsLib = (JsLib) assetContainer;
				String resourcePath = RelativePathUtility.get(jsLib.dir(), imageFile);
				
				targetPath = cssResourceContentPathParser.createRequest("lib-request", jsLib.getName(), resourcePath);
			}
			else {
				throw new BundlerFileProcessingException(imageFile, "File does not exist in a known scope");
			}
		}
		catch(MalformedTokenException e) {
			throw new BundlerProcessingException(e);
		}
		
		return targetPath;
	}
}
