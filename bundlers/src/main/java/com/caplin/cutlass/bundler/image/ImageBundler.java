package com.caplin.cutlass.bundler.image;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;

import com.caplin.cutlass.LegacyFileBundlerPlugin;
import com.caplin.cutlass.BRJSAccessor;

import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.ParsedContentPath;
import org.bladerunnerjs.model.exception.request.RequestHandlingException;
import org.bladerunnerjs.model.exception.request.MalformedRequestException;

import com.caplin.cutlass.AppMetaData;
import com.caplin.cutlass.bundler.RequestScopeProvider;
import com.caplin.cutlass.bundler.css.TargetPathCreator;

import org.bladerunnerjs.model.exception.request.ContentFileProcessingException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.model.exception.request.ResourceNotFoundException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;
import org.bladerunnerjs.utility.ContentPathParser;

import com.caplin.cutlass.bundler.parser.RequestParserFactory;
import com.caplin.cutlass.structure.ScopeLevel;
import com.caplin.cutlass.structure.model.Node;
import com.caplin.cutlass.structure.model.SdkModel;
import com.caplin.cutlass.structure.model.path.AppPath;
import com.caplin.cutlass.structure.model.path.AspectPath;
import com.caplin.cutlass.structure.model.path.BladePath;

public class ImageBundler extends AbstractPlugin implements LegacyFileBundlerPlugin
{
	private final ContentPathParser contentPathParser = RequestParserFactory.createImageBundlerContentPathParser();;
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getBundlerExtension()
	{
		return "image.bundle";
	}
	
	@Override
	public List<String> getValidRequestForms()
	{
		return contentPathParser.getRequestForms();
	}
	
	@Override
	public List<File> getBundleFiles(File baseDir, File testDir, String requestName) throws RequestHandlingException
	{
		Node requestNode = SdkModel.getNode(baseDir);
		ParsedContentPath request = contentPathParser.parse(requestName);
		ScopeLevel imageScope = getImageScope(request.properties);
		
		if(!RequestScopeProvider.isValidRequest(requestNode, imageScope))
		{
			throw new MalformedRequestException(requestName, "images within the " + imageScope +
				" scope can not be accessed for requests made at the " + requestNode.getNodeType() + " level.");
		}
		
		File image = getImage(imageScope, baseDir, request.properties);

		List<File> files = new ArrayList<File>();
		if(image.exists())
		{
			files.add(image);
		}
		return files;
	}

	@Override
	public void writeBundle(List<File> sourceFiles, OutputStream outputStream) throws RequestHandlingException
	{
		if(sourceFiles.size() == 0)
		{
			throw new ResourceNotFoundException();
		}
		else if(sourceFiles.size() > 1)
		{
			throw new ContentProcessingException("More than one image file was requested to be added to a single bundle.");
		}
		else
		{
			File imageFile = sourceFiles.get(0);
			
			try
			{
				InputStream input = new FileInputStream(imageFile);
				
				try
				{
					IOUtils.copy(input, outputStream);
				}
				finally
				{
					input.close();
					outputStream.flush(); // TODO: see if we can remove this flush() since there doesn't seem to be any particular reason for it
				}
			}
			catch (Exception e)
			{
				throw new ContentFileProcessingException(imageFile, e, "Error while bundling files.");
			}
		}
		
	}

	@Override
	public List<String> getValidRequestStrings(AppMetaData appMetaData) throws ContentProcessingException
	{
		List<String> requests = new ArrayList<String>();
		for(File image : appMetaData.getImages())
		{
			requests.add(TargetPathCreator.getBundleRequestForImage(image));
		}
		return requests;
	}
	
	private ScopeLevel getImageScope(Map<String, String> requestProperties)
	{
		String bladeSet = requestProperties.get("bladeset");
		String blade = requestProperties.get("blade");
		String theme = requestProperties.get("theme");
		String sdk = requestProperties.get("sdk");
		ScopeLevel imageScope = null;
		
		if((sdk != null && sdk.equals("sdk")) && (bladeSet == null) && (blade == null) && (theme == null))
		{
			imageScope = ScopeLevel.SDK_SCOPE;
		}
		else if((bladeSet == null) && (blade == null) && (theme != null))
		{
			imageScope = ScopeLevel.ASPECT_SCOPE;
		}
		else if((bladeSet != null) && (blade == null) && (theme != null))
		{
			imageScope = ScopeLevel.BLADESET_SCOPE;
		}
		else if((bladeSet != null) && (blade != null) && (theme != null))
		{
			imageScope = ScopeLevel.BLADE_SCOPE;
		}
		else if((bladeSet != null) && (blade != null) && (theme == null))
		{
			imageScope = ScopeLevel.WORKBENCH_SCOPE;
		}
		
		return imageScope;
	}
	
	private File getImage(ScopeLevel imageScope, File baseDir, Map<String, String> requestProperties)
	{
		String themeName = requestProperties.get("theme");
		String imagePath = requestProperties.get("imagePath");
		String bladesetName = requestProperties.get("bladeset");
		String bladeName = requestProperties.get("blade");
		File themeDir = null;
		
		// Branching sdk scoped requests via the new model
		if(imageScope == ScopeLevel.SDK_SCOPE)
		{
			File resourcesDir = BRJSAccessor.root.sdkLib().assetLocation("resources").dir();
			
			if(resourcesDir != null)
			{
				return new File(resourcesDir, imagePath);
			}
		}

		switch(imageScope)
		{
			case ASPECT_SCOPE:
				AspectPath aspectPath = AspectPath.locateAncestorPath(baseDir);
				
				if(aspectPath.getDir() == null)
				{
					aspectPath = AppPath.locateAncestorPath(baseDir).aspectPath("default");
				}
				
				themeDir = aspectPath.themesPath().themePath(themeName).getDir();
				break;
			
			case BLADESET_SCOPE:
				themeDir = AppPath.locateAncestorPath(baseDir).bladesetPath(bladesetName).themesPath().themePath(themeName).getDir();
				break;
			
			case BLADE_SCOPE:
				themeDir = AppPath.locateAncestorPath(baseDir).bladesetPath(bladesetName).bladesPath().bladePath(bladeName).themesPath().themePath(themeName).getDir();
				break;
			
			case WORKBENCH_SCOPE:
				themeDir = BladePath.locateAncestorPath(baseDir).workbenchPath().resourcesPath().stylePath().getDir();
				break;
			
			default:
				break;
		}
		
		return new File(themeDir, imagePath);
	}
}
