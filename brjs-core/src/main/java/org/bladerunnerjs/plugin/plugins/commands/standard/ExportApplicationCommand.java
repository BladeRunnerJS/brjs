package org.bladerunnerjs.plugin.plugins.commands.standard;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.JsLib;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtility;
import org.bladerunnerjs.utility.filefilter.ExcludeDirFileFilter;

import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class ExportApplicationCommand extends ArgsParsingCommandPlugin
{	
	public class Messages {
		public static final String APP_CREATED_CONSOLE_MSG = "Successfully created new app '%s'";
		public static final String APP_DEPLOYED_CONSOLE_MSG = "Successfully deployed '%s' app";
	}
	
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the name of the application to be exported"));
		argsParser.registerParameter(new FlaggedOption("banner").setLongFlag("banner").setRequired(false).setHelp("a banner that will be added to every exported class"));
		argsParser.registerParameter(new FlaggedOption("bannerExtensions").setLongFlag("bannerExtensions").setRequired(false).setDefault("js").setHelp("a comma seperated list of extensions to apply the banner to"));
		argsParser.registerParameter(new UnflaggedOption("target").setRequired(false).setDefault("").setHelp("the target dir for the exported app"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		logger = brjs.logger(this.getClass());
	}
	
	@Override
	public String getCommandName()
	{
		return "export-app";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Create an importable zip for a given application.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException 
	{
		String appName = parsedArgs.getString("app-name");
		String banner = "/*\n" + parsedArgs.getString("banner") + "\n*/\n\n";
		String[] bannerExtensions = parsedArgs.getString("bannerExtensions").split(",");
		String targetPath = parsedArgs.getString("target");

		App app = brjs.app(appName);
		if(!app.dirExists()) throw new CommandArgumentsException("Could not find application '" + appName + "'", this);
		
		File targetDir;
		if (targetPath.equals("")) {
			targetDir = brjs.storageDir("exported-apps");
		} else {
    		targetDir = new File(targetPath);
    		if (!targetDir.isDirectory()) 
    		{
    			targetDir = brjs.file("sdk/" + targetPath);
    		}
		}
		File destinationZipLocation = new File(targetDir, appName + ".zip");

		try 
		{
			File temporaryExportDir = FileUtility.createTemporaryDirectory(appName);
			
			IOFileFilter excludeUserLibraryTestsFilter = createExcludeUserLibsTestsFilter(appName);
			NotFileFilter brjsJarFilter = new NotFileFilter(new AndFileFilter(new PrefixFileFilter("brjs-"), new SuffixFileFilter(".jar")));
			IOFileFilter combinedFilter = new AndFileFilter(new ExcludeDirFileFilter("js-test-driver", "bundles"), brjsJarFilter);
			
			combinedFilter = new AndFileFilter(combinedFilter, excludeUserLibraryTestsFilter);
			
			createResourcesFromSdkTemplate(app.dir(), temporaryExportDir, combinedFilter);
			includeBannerInDirectoryClasses(new File(temporaryExportDir, "libs"), banner, bannerExtensions);
			FileUtility.zipFolder(temporaryExportDir, destinationZipLocation, false);
		}
		catch (Exception e)
		{
			throw new CommandOperationException("Could not create application zip for application '" + appName + "'", e);  
		}

		logger.println("Successfully exported application '" + appName + "'");
		logger.println(" " + destinationZipLocation.getAbsolutePath());
		
		return 0;
	}

	
	private void createResourcesFromSdkTemplate(File templateDir, File targetDir, FileFilter fileFilter) throws IOException
	{
		ArrayList<File> addList = new ArrayList<File>();
		recurseIntoSubfoldersAndAddAllFilesMatchingFilter(addList, templateDir, fileFilter);
		
		if (!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		
		for (File f : addList)
		{			
			String relativePathFromTemplateDir = f.getAbsolutePath().replace(templateDir.getAbsolutePath(), "");
			File newResourceToAdd = new File(targetDir, relativePathFromTemplateDir);

			if (f.isDirectory() == true)
			{
				newResourceToAdd.mkdirs();
			}
			else 
			{
				createFile(f, newResourceToAdd);
			}
		}
	}
	
	private void createFile(File source, File newFileLocation) throws IOException
	{
		if (source.exists() == true)
		{
			if (newFileLocation.exists() == false)
			{
				if (newFileLocation.getParentFile().exists() == false)
				{
					newFileLocation.getParentFile().mkdirs();
				}
				
				FileUtils.copyFile(source, newFileLocation);
			}
		}
	}

	private ArrayList<File> recurseIntoSubfoldersAndAddAllFilesMatchingFilter(ArrayList<File> addList, File file, FileFilter filter)
	{
		if(file.isDirectory())
		{
			for(File r : file.listFiles(filter))
			{
				if (!r.getName().startsWith("."))
				{
					recurseIntoSubfoldersAndAddAllFilesMatchingFilter(addList, r, filter);
				}
			}
		}
		
		if(filter.accept(file) && !file.getName().startsWith("."))
		{
			addList.add(file);
		}
		
		return addList;
	}
	
	private IOFileFilter createExcludeUserLibsTestsFilter(String appName) 
	{
		IOFileFilter excludeDirFilter = new ExcludeDirFileFilter("");
		
		for (JsLib jsLib : brjs.app(appName).jsLibs())
		{
			if (jsLib.parentNode() instanceof App)
			{
				excludeDirFilter = new AndFileFilter (excludeDirFilter, new ExcludeDirFileFilter(jsLib.getName(), "test"));
			}
		}
		
		return excludeDirFilter;
	}
	
	private void includeBannerInDirectoryClasses(File dir, String banner, String[] extensions) throws IOException, ConfigException 
	{
		if (dir.exists())
		{
			for (File file : FileUtils.listFiles(dir, extensions, true))
			{
				includeBanner(file, banner);
			}
		}
	}

	private void includeBanner(File file, String disclaimer) throws ConfigException, IOException 
	{
		String fileContent = FileUtils.readFileToString(file, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
	
		FileUtils.writeStringToFile(file, disclaimer + fileContent, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
	}
}
