package org.bladerunnerjs.plugin.plugins.bundlers.css;

import java.io.File;

import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.AssetLocation;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.ThemedAssetLocation;
import org.bladerunnerjs.model.BladeWorkbench;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.MalformedTokenException;
import org.bladerunnerjs.plugin.plugins.bundlers.cssresource.CssResourceContentPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

public class TargetPathCreator
{
	private final BRJS brjs;
	private final ContentPathParser cssResourceContentPathParser;
	
	public TargetPathCreator(BRJS brjs) {
		this.brjs = brjs;
		cssResourceContentPathParser = brjs.plugins().contentPlugin("cssresource").getContentPathParser();
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

		Node firstAncestorNode = brjs.locateFirstAncestorNode( brjs.getMemoizedFile(imageFile) );
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
		
		File assetLocationParentDir = assetLocation.dir().getParentFile();
		try {
			if(assetContainer instanceof Aspect) {
				Aspect aspect = (Aspect) assetContainer;
				if (assetLocation instanceof ThemedAssetLocation && assetLocationParentDir.getName().equals("themes")) {
					ThemedAssetLocation theme = (ThemedAssetLocation) assetLocation;
					String resourcePath = theme.dir().getRelativePath(imageFile);
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.ASPECT_THEME_REQUEST, ((Aspect) assetContainer).getName(), theme.dir().getName(), resourcePath);
				} else {
					String resourcePath = assetContainer.dir().getRelativePath(imageFile);
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.ASPECT_RESOURCE_REQUEST, aspect.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof Bladeset) {
				Bladeset bladeset = (Bladeset) assetContainer;
				if (assetLocation instanceof ThemedAssetLocation && assetLocationParentDir.getName().equals("themes")) {
    				ThemedAssetLocation theme = (ThemedAssetLocation) assetLocation;
    				String resourcePath = theme.dir().getRelativePath(imageFile);
    				
    				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESET_THEME_REQUEST, bladeset.getName(), theme.dir().getName(), resourcePath);
				} else {
					String resourcePath = bladeset.dir().getRelativePath(imageFile);
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADESET_RESOURCE_REQUEST, bladeset.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof Blade) {
				Blade blade = (Blade) assetContainer;
				Bladeset bladeset = blade.parent();
				if (assetLocation instanceof ThemedAssetLocation && assetLocationParentDir.getName().equals("themes")) {
    				ThemedAssetLocation theme = (ThemedAssetLocation) assetLocation;
    				String resourcePath = theme.dir().getRelativePath(imageFile);
    				
    				targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADE_THEME_REQUEST, bladeset.getName(), blade.getName(), theme.dir().getName(), resourcePath);
				} else {
					String resourcePath = blade.dir().getRelativePath(imageFile);
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.BLADE_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
				}
			}
			else if(assetContainer instanceof BladeWorkbench) {
				BladeWorkbench workbench = (BladeWorkbench) assetContainer;
				Blade blade = workbench.parent();
				Bladeset bladeset = blade.parent();
				
				if (assetLocation instanceof ThemedAssetLocation && assetLocationParentDir.getName().equals("themes")) {
					String resourcePath = assetLocation.file("resources").getRelativePath(imageFile);
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.WORKBENCH_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
				} else {
					String resourcePath = workbench.dir().getRelativePath(imageFile);
					targetPath = cssResourceContentPathParser.createRequest(CssResourceContentPlugin.WORKBENCH_RESOURCE_REQUEST, bladeset.getName(), blade.getName(), resourcePath);
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
}
