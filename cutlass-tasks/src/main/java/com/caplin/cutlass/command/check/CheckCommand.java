package com.caplin.cutlass.command.check;

import java.io.File;
import java.io.FileFilter;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;

import com.caplin.cutlass.command.LegacyCommandPlugin;
import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import com.caplin.cutlass.BRJSAccessor;
import org.bladerunnerjs.model.JsNonBladeRunnerLib;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;

public class CheckCommand implements LegacyCommandPlugin
{
	private FileFilter CAPLIN_JAR_FILE_NAME_FILTER = new AndFileFilter(new PrefixFileFilter("brjs-"), new SuffixFileFilter(".jar"));
	private ConsoleWriter out;
	
	public CheckCommand()
	{
		out = BRJSAccessor.root.getConsoleWriter();
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
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException
	{
		CheckUtility checkUtility = new CheckUtility();
		StringBuilder messageToShowUser = new StringBuilder();
		
		messageToShowUser.append("-- Environment Details --\n");
		messageToShowUser.append( checkUtility.printEnvironmentDetails() );
		messageToShowUser.append("\n");
		
		messageToShowUser.append("-- Application Details --\n");
		
		List<File> sdkLibsApplicationJars = Arrays.asList(BRJSAccessor.root.appJars().dir().listFiles(CAPLIN_JAR_FILE_NAME_FILTER));
		List<JsNonBladeRunnerLib> sdkThirdpartyLibraries = BRJSAccessor.root.sdkNonBladeRunnerLibs();
		
		for(App application : BRJSAccessor.root.apps())
		{
			checkUtility.checkJarDifferencesAndAddMessageListingThem(messageToShowUser, sdkLibsApplicationJars, application.dir());
			checkUtility.checkForApplicationThirdpartyLibraries(messageToShowUser, sdkThirdpartyLibraries, application);
		}
		
		checkUtility.checkThatWeHaveNothingInPatchesDirectory(messageToShowUser);
		
		out.println(messageToShowUser.toString());
	}
}
