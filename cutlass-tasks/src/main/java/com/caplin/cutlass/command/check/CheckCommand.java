package com.caplin.cutlass.command.check;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.command.LegacyCommandPlugin;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.StaticModelAccessor;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.plugin.base.AbstractPlugin;

public class CheckCommand extends AbstractPlugin implements LegacyCommandPlugin
{
	private FileFilter CAPLIN_JAR_FILE_NAME_FILTER = new AndFileFilter(new PrefixFileFilter("brjs-"), new SuffixFileFilter(".jar"));
	private ConsoleWriter out;
	
	public CheckCommand()
	{
		out = StaticModelAccessor.root.getConsoleWriter();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
	}
	
	@Override
	public String getCommandName()
	{
		return "check";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Check all applications for: 1) jar file consistency, 2) thirdparty-library overrides and 3) use of js-patches";
	}

	@Override
	public String getCommandUsage()
	{
		return "";
	}
	
	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException
	{
		CheckUtility checkUtility = new CheckUtility();
		StringBuilder messageToShowUser = new StringBuilder();
		
		messageToShowUser.append("-- Environment Details --\n");
		messageToShowUser.append( checkUtility.printEnvironmentDetails() );
		messageToShowUser.append("\n");
		
		messageToShowUser.append("-- Application Details --\n");
		
		List<File> sdkLibsApplicationJars = Arrays.asList(StaticModelAccessor.root.appJars().dir().listFiles(CAPLIN_JAR_FILE_NAME_FILTER));
		List<SdkJsLib> sdkThirdpartyLibraries = StaticModelAccessor.root.sdkLibs();
		
		for(App application : StaticModelAccessor.root.userApps())
		{
			checkUtility.checkJarDifferencesAndAddMessageListingThem(messageToShowUser, sdkLibsApplicationJars, application.dir());
			checkUtility.checkForApplicationThirdpartyLibraries(messageToShowUser, sdkThirdpartyLibraries, application);
		}
		
		checkUtility.checkThatWeHaveNothingInPatchesDirectory(messageToShowUser);
		
		out.println(messageToShowUser.toString());
		return 0;
	}
}
