package com.caplin.cutlass.bundler.css;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bladerunnerjs.model.exception.request.BundlerFileProcessingException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.file.RelativePath;
import com.caplin.cutlass.BRJSAccessor;
import org.bladerunnerjs.model.JsLib;
import com.caplin.cutlass.structure.BundlePathsFromRoot;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.NodeType;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.node.AspectNode;
import com.caplin.cutlass.structure.model.node.BladeNode;
import com.caplin.cutlass.structure.model.node.BladesetNode;
import com.caplin.cutlass.structure.model.node.WorkbenchNode;

public class TargetPathCreator
{
	private static final String THEMES = "themes";
	private static final String IMAGE_BUNDLE_EXT = "_image.bundle";
	private static final String THIRDPARTY_BUNDLE_EXT = "_thirdparty.bundle";
	private static final Pattern thirdpartyLibraryPattern = Pattern.compile(".*(thirdparty-libraries|thirdparty)[/\\\\](.*)");
	
	public static String getRelativeBundleRequestForImage(File imageFile) throws BundlerProcessingException
	{
		return "../" + getBundleRequestForImage(imageFile);
	}

	public static String getBundleRequestForImage(File imageFile) throws BundlerProcessingException
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
			cssImageReferenceException.setReferencedImagePath(imageFile.getAbsolutePath());
//			cssImageReferenceException.setImageScopeLevel(imageFileScope);
			throw cssImageReferenceException;
		}
		return targetPath;
	}
	
	private static String getTargetPath(File imageFile) throws BundlerProcessingException
	{
		
		String targetPath = null;
		
		Node node = SdkModel.getNode(imageFile);
		NodeType nodeType = node.getNodeType();

		// Branching sdk requests via the new model 
		if(nodeType.equals(NodeType.SDK))
		{
			JsLib jslib = BRJSAccessor.root.locateAncestorNodeOfClass(imageFile, JsLib.class);
			if(jslib.dir() != null) {
				targetPath = BundlePathsFromRoot.IMAGES + "sdk" + getSDKResourcesRelativePath(imageFile) + IMAGE_BUNDLE_EXT;
			}
			return targetPath;
		}
		
		AspectNode aspectNode = null;
		BladesetNode bladesetNode = null;
		BladeNode bladeNode = null;
		WorkbenchNode workbenchNode = null;
		
		switch(nodeType)
		{
			case ASPECT:
				aspectNode = (AspectNode) node;
				targetPath = BundlePathsFromRoot.IMAGES + getThemeRelativePath(imageFile, aspectNode.getDir()) + IMAGE_BUNDLE_EXT;
				break;
			
			case BLADESET:
				bladesetNode = (BladesetNode) node;
				targetPath = BundlePathsFromRoot.IMAGES + getBladeset(bladesetNode) + "/" + getThemeRelativePath(imageFile, bladesetNode.getDir()) + IMAGE_BUNDLE_EXT;
				break;
			
			case BLADE:
				bladeNode = (BladeNode) node;
				bladesetNode = (BladesetNode) bladeNode.getParentNode();
				targetPath = BundlePathsFromRoot.IMAGES + getBladeset(bladesetNode) + "/" + getBlade(bladeNode) + "/" + getThemeRelativePath(imageFile, bladeNode.getDir()) + IMAGE_BUNDLE_EXT;
				break;
			
			case WORKBENCH:
				workbenchNode = (WorkbenchNode) node;
				bladeNode = (BladeNode) workbenchNode.getParentNode();
				bladesetNode = (BladesetNode) bladeNode.getParentNode();
				targetPath = BundlePathsFromRoot.IMAGES + getBladeset(bladesetNode) + "/" + getBlade(bladeNode) + "/workbench/" + getStyleRelativePath(imageFile, workbenchNode.getDir()) + IMAGE_BUNDLE_EXT;
				break;
			
			case THIRDPARTY_LIB:
				targetPath = BundlePathsFromRoot.THIRDPARTY + getThirdpartyLibraryRelativePath(imageFile) + THIRDPARTY_BUNDLE_EXT;
				break;
			
			default:
				throw new BundlerFileProcessingException(imageFile, "File does not exist in a known scope");
		}
		
		return targetPath;
	}

	private static String getSDKResourcesRelativePath(File imageFile)
	{
		JsLib jslib = BRJSAccessor.root.locateAncestorNodeOfClass(imageFile, JsLib.class);

		Path resources = jslib.resources().dir().getAbsoluteFile().toPath();
		Path relativised = resources.relativize(imageFile.getAbsoluteFile().toPath());
		
		String relativePath = "/" + relativised.toString();
		relativePath = relativePath.replaceAll("\\\\", "/");

		return relativePath;
	}
	
	private static String getThirdpartyLibraryRelativePath(File imageFile)
	{
		String thirdpartyLibraryRelativePath = "";
		String path = imageFile.getAbsolutePath();
		Matcher matcher = thirdpartyLibraryPattern.matcher(path);
		
		if(matcher.matches()) 
		{
			thirdpartyLibraryRelativePath = matcher.group(2);
		}
		
		thirdpartyLibraryRelativePath = thirdpartyLibraryRelativePath.replaceAll("\\\\", "/");
		
		return thirdpartyLibraryRelativePath;
	}

	private static String getBlade(BladeNode bladeNode)
	{
		return "blade_" + bladeNode.getName();
	}

	private static String getBladeset(BladesetNode bladesetNode)
	{
		return "bladeset_" + bladesetNode.getName();
	}

	private static String getThemeRelativePath(File imageFile, File scopeDir)
	{
		String themeName = extractTheme(imageFile, scopeDir);
		File themeDir = new File(scopeDir.getPath() + "/themes/" + themeName);
		
		return "theme_" + themeName + "/" + getRelativePath(imageFile, themeDir);
	}
	
	private static String getStyleRelativePath(File imageFile, File scopeDir)
	{
		File styleDir = new File(scopeDir.getPath() + "/resources/style");
		
		return getRelativePath(imageFile, styleDir);
	}
	
	private static String getRelativePath(File imageFile, File baseDir)
	{
		String relativePath = RelativePath.getRelativePath(baseDir, imageFile).replaceAll("\\\\", "/");
		
		return relativePath;
	}
	
	private static String extractTheme(File file, File scopeDir)
	{	
		File parent = file.getParentFile();
		if (parent.getName().equals(THEMES) && parent.getParentFile().getAbsoluteFile().equals(scopeDir.getAbsoluteFile()))
		{
			return file.getName();
		}
		return extractTheme(file.getParentFile(), scopeDir);
	}
}
