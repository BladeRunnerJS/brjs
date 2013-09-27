package org.bladerunnerjs.model.utility;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.BRJSNode;
import org.bladerunnerjs.model.exception.template.DirectoryAlreadyExistsException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.utility.FileUtility;


public class TemplateUtility
{
	public static void installTemplate(BRJSNode node, String templateName, Map<String, String> transformations) throws TemplateInstallationException {
		try {
			if(node.dirExists() && !(node instanceof BRJS)) {
				File[] dirContents = node.dir().listFiles();
				
				if(dirContents.length != 0) {
					throw new DirectoryAlreadyExistsException(node);
				}
			}
			
			File templateDir = node.root().template(templateName).dir();
			FileUtility.createResourcesFromSdkTemplate(templateDir, node.dir());
			
			if(!transformations.isEmpty()) {
				transformDir(node.dir(), transformations);
			}
		}
		catch(IOException e) {
			throw new TemplateInstallationException(e);
		}
	}
	
	private static void transformDir(File dir, Map<String, String> transformations) throws TemplateInstallationException
	{
		for(String transformKey : transformations.keySet())
		{
			String findText = "@" + transformKey;
			String replaceText = transformations.get(transformKey);
			
			dir = transformFileName(dir, findText, replaceText);
		}
		
		for(File file : dir.listFiles())
		{
			if(!file.isDirectory())
			{
				if(file.getName().matches("^.*\\.(txt|js|xml|properties|bundle|conf|css|htm|html|jsp|java)$"))
				{
					transformFile(file, transformations);
				}
			}
			else
			{
				transformDir(file, transformations);
			}
		}
	}
	
	private static void transformFile(File file, Map<String, String> transformations) throws TemplateInstallationException
	{
		try {
			String fileContents = FileUtils.readFileToString(file);
			
			for(String transformationKey : transformations.keySet()) {
				String findText = "@" + transformationKey;
				String replaceText = transformations.get(transformationKey);
				
				file = transformFileName(file, findText, replaceText);
				fileContents = fileContents.replaceAll(findText, replaceText);
			}
			
			FileUtils.writeStringToFile(file, fileContents);
		}
		catch(IOException e) {
			throw new TemplateInstallationException(e);
		}
	}
	
	private static File transformFileName(File file, String findText, String replaceText) throws TemplateInstallationException
	{
		if(file.getName().contains(findText))
		{
			File renamedFile = new File(file.getParent(), file.getName().replaceAll(findText, replaceText));
			
			if(file.renameTo(renamedFile))
			{
				file = renamedFile;
			}
			else
			{
				throw new TemplateInstallationException("unable to rename '" + file.getPath() + "' directory to '" + renamedFile.getPath() + "'.");
			}
		}
		
		return file;
	}
}
