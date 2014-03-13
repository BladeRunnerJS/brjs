package org.bladerunnerjs.plugin.plugins.commands.standard;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.Blade;
import org.bladerunnerjs.model.Bladeset;
import org.bladerunnerjs.model.Workbench;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.NodeDoesNotExistException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;
import org.bladerunnerjs.utility.deps.DependencyGraphReportBuilder;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;


public class WorkbenchDepsCommand extends ArgsParsingCommandPlugin
{
	private ConsoleWriter out;
	private BRJS brjs;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application containing the workbench to show dependencies for"));
		argsParser.registerParameter(new UnflaggedOption("bladeset-name").setRequired(true).setHelp("the bladeset containing the workbench to show dependencies for"));
		argsParser.registerParameter(new UnflaggedOption("blade-name").setRequired(true).setHelp("the blade containing the workbench to show dependencies for"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		out = brjs.getConsoleWriter();
	}
	
	@Override
	public String getCommandName()
	{
		return "workbench-deps";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Show dependencies for a given workbench.";
	}
	
	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		String bladesetName = parsedArgs.getString("bladeset-name");
		String bladeName = parsedArgs.getString("blade-name");
		
		App app = brjs.app(appName);
		Bladeset bladeset = app.bladeset(bladesetName);
		Blade blade = bladeset.blade(bladeName);
		Workbench workbench = blade.workbench();
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(!bladeset.dirExists()) throw new NodeDoesNotExistException(bladeset, this);
		if(!blade.dirExists()) throw new NodeDoesNotExistException(blade, this);
		if(!workbench.dirExists()) throw new NodeDoesNotExistException(workbench, "workbench", this);
		
		try {
			out.println(DependencyGraphReportBuilder.createReport(workbench));
		}
		catch (ModelOperationException e) {
			throw new CommandOperationException(e);
		}
	}
}
