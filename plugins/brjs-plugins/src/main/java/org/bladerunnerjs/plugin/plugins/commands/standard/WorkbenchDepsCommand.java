package org.bladerunnerjs.plugin.plugins.commands.standard;

import org.bladerunnerjs.logging.Logger;
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
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;


public class WorkbenchDepsCommand extends ArgsParsingCommandPlugin
{
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application containing the workbench to show dependencies for"));
		argsParser.registerParameter(new UnflaggedOption("bladeset-name").setRequired(true).setHelp("the bladeset containing the workbench to show dependencies for"));
		argsParser.registerParameter(new UnflaggedOption("blade-name").setRequired(true).setHelp("the blade containing the workbench to show dependencies for"));
		argsParser.registerParameter(new Switch("all").setShortFlag('A').setLongFlag("all").setDefault("false").setHelp("show all ocurrences of a dependency"));
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{
		this.brjs = brjs;
		this.logger = brjs.logger(this.getClass());
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
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		String bladesetName = parsedArgs.getString("bladeset-name");
		String bladeName = parsedArgs.getString("blade-name");
		boolean showAllDependencies = parsedArgs.getBoolean("all");
		
		App app = brjs.app(appName);
		Bladeset bladeset = app.bladeset(bladesetName);
		Blade blade = bladeset.blade(bladeName);
		Workbench workbench = blade.workbench();
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(!bladeset.dirExists()) throw new NodeDoesNotExistException(bladeset, this);
		if(!blade.dirExists()) throw new NodeDoesNotExistException(blade, this);
		if(!workbench.dirExists()) throw new NodeDoesNotExistException(workbench, "workbench", this);
		
		try {
			logger.println(DependencyGraphReportBuilder.createReport(workbench, showAllDependencies));
		}
		catch (ModelOperationException e) {
			throw new CommandOperationException(e);
		}
		return 0;
	}
}
