package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.File;

import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.plugins.bundlers.cssresource.CssResourceContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;
import org.bladerunnerjs.utility.RelativePathUtility;

public class TargetPathCreator
{
	private final BRJS brjs;
	private final ContentPathParser cssResourceContentPathParser;
	
	public TargetPathCreator(BRJS brjs) {
		this.brjs = brjs;
		cssResourceContentPathParser = brjs.plugins().contentPlugin("cssresource").getContentPathParser();
	}
	
	public String getRelativeBundleRequestForImage(File imageFile) throws ContentProcessingException
	{
		return "../../" + getBundleRequestForImage(imageFile);
	}
	
	public String getBundleRequestForImage(File imageFile) throws ContentProcessingException
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
	
	private String getTargetPath(File imageFile) throws ContentProcessingException
	{

		Node firstAncestorNode = brjs.locateFirstAncestorNode(imageFile);
		AssetLocation assetLocation  = null;
		AssetContainer assetContainer = null;
		if (firstAncestorNode instanceof AssetLocation){
			 assetLocation = (AssetLocation)firstAncestorNode;
			 assetContainer = assetLocation.assetContainer();
		} else {
			assetContainer = (AssetContainer) firstAncestorNode;
			assetLocation = assetContainer.assetLocation(".");
		}
		String targetPath = null;
		
		try {
			if(assetContainer instanceof Aspect) {
				ThemedAssetLocation theme = (ThemedAssetLocation) assetLocation;
				String resourcePath = RelativePathUtility.get(theme.dir(), imageFile);
				
				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.ASPECT_THEME_REQUEST, ((Aspect) assetContainer).getName(), theme.dir().getName(), resourcePath);
			}
			else if(assetContainer instanceof Bladeset) {
				ThemedAssetLocation theme = (ThemedAssetLocation) assetLocation;
				String resourcePath = RelativePathUtility.get(theme.dir(), imageFile);
				Bladeset bladeset = (Bladeset) assetContainer;
				
				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESET_THEME_REQUEST, bladeset.getName(), theme.dir().getName(), resourcePath);
			}
			else if(assetContainer instanceof Blade) {
				ThemedAssetLocation theme = (ThemedAssetLocation) assetLocation;
				String resourcePath = RelativePathUtility.get(theme.dir(), imageFile);
				Blade blade = (Blade) assetContainer;
				Bladeset bladeset = blade.parent();
				
				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADE_THEME_REQUEST, bladeset.getName(), blade.getName(), theme.dir().getName(), resourcePath);
			}
			else if(assetContainer instanceof Workbench) {
				Workbench workbench = (Workbench) assetContainer;
				String resourcePath = RelativePathUtility.get(assetLocation.file("resources"), imageFile);
				Blade blade = workbench.parent();
				Bladeset bladeset = blade.parent();
				
				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.WORKBENCH_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
			}
			else if(assetContainer instanceof JsLib) {
				JsLib jsLib = (JsLib) assetContainer;
				String resourcePath = RelativePathUtility.get(jsLib.dir(), imageFile);
				
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
}
