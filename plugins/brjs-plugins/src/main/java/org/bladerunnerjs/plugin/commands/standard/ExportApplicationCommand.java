package org.bladerunnerjs.plugin.commands.standard;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.FileUtils;
import org.bladerunnerjs.utility.ZipUtility;
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
		String banner = parsedArgs.getString("banner");
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
			MemoizedFile temporaryExportDir = brjs.getMemoizedFile( FileUtils.createTemporaryDirectory( this.getClass(), appName ) );
			
			IOFileFilter excludeUserLibraryTestsFilter = createExcludeUserLibsTestsFilter(appName);
			NotFileFilter brjsJarFilter = new NotFileFilter(new AndFileFilter(new PrefixFileFilter("brjs-"), new SuffixFileFilter(".jar")));
			IOFileFilter combinedFilter = new AndFileFilter(new ExcludeDirFileFilter("bundles"), brjsJarFilter);
			
			combinedFilter = new AndFileFilter(combinedFilter, excludeUserLibraryTestsFilter);
			
			createResourcesFromSdkTemplate(app.dir(), temporaryExportDir, combinedFilter);
			if (banner != null) {
				String jsBanner = "/*\n" + banner + "\n*/\n\n";
				includeBannerInDirectoryClasses(brjs, new File(temporaryExportDir, "libs"), jsBanner, bannerExtensions);
			}
			ZipUtility.zipFolder(temporaryExportDir, destinationZipLocation, false);
			brjs.getFileModificationRegistry().incrementFileVersion(destinationZipLocation);
		}
		catch (Exception e)
		{
			throw new CommandOperationException("Could not create application zip for application '" + appName + "'", e);  
		}

		logger.println("Successfully exported application '" + appName + "'");
		logger.println(" " + destinationZipLocation.getAbsolutePath());
		
		return 0;
	}

	
	private void createResourcesFromSdkTemplate(MemoizedFile templateDir, MemoizedFile targetDir, FileFilter fileFilter) throws IOException, ConfigException
	{
		ArrayList<MemoizedFile> addList = new ArrayList<>();
		recurseIntoSubfoldersAndAddAllFilesMatchingFilter( Arrays.asList(brjs.bladerunnerConf().getIgnoredPaths()) , addList, templateDir, fileFilter );
		
		if (!targetDir.exists())
		{
			targetDir.mkdirs();
		}
		
		for (MemoizedFile f : addList)
		{			
			String relativePathFromTemplateDir = f.getAbsolutePath().replace(templateDir.getAbsolutePath(), "");
			MemoizedFile newResourceToAdd = targetDir.file(relativePathFromTemplateDir);

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
	
	private void createFile(MemoizedFile source, MemoizedFile newFileLocation) throws IOException
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

	private ArrayList<MemoizedFile> recurseIntoSubfoldersAndAddAllFilesMatchingFilter(List<String> ignoredFiles, ArrayList<MemoizedFile> addList, MemoizedFile file, FileFilter filter)
	{
		if (ignoredFiles.contains(file.getName())) {
			return addList;
		}
		
		if (file.isDirectory())
		{
			for (MemoizedFile r : file.listFiles(filter))
			{
				recurseIntoSubfoldersAndAddAllFilesMatchingFilter(ignoredFiles, addList, r, filter);
			}
		}
		
		if (filter.accept(file))
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
	
	private void includeBannerInDirectoryClasses(BRJS brjs, File dir, String banner, String[] extensions) throws IOException, ConfigException 
	{
		if (dir.exists())
		{
			for (File file : FileUtils.listFiles(dir, extensions, true))
			{
				includeBanner(brjs, file, banner);
			}
		}
	}

	private void includeBanner(BRJS brjs, File file, String disclaimer) throws ConfigException, IOException 
	{
		String fileContent = org.apache.commons.io.FileUtils.readFileToString(file, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
	
		FileUtils.write(brjs, file, disclaimer + fileContent, brjs.bladerunnerConf().getDefaultFileCharacterEncoding());
	}
}
