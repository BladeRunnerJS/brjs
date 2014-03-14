package org.bladerunnerjs.plugin.plugins.commands.standard;

import org.bladerunnerjs.console.ConsoleWriter;
import org.bladerunnerjs.model.App;
import org.bladerunnerjs.model.Aspect;
import org.bladerunnerjs.model.BRJS;
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


public class DepInsightCommand extends ArgsParsingCommandPlugin
{
	private ConsoleWriter out;
	private BRJS brjs;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("app-name").setRequired(true).setHelp("the application to show dependencies for"));
		argsParser.registerParameter(new UnflaggedOption("require-path").setRequired(true).setHelp("the source module to show dependencies for"));
		argsParser.registerParameter(new UnflaggedOption("aspect-name").setDefault("default").setHelp("the aspect to show dependencies for"));
		argsParser.registerParameter(new Switch("alias").setShortFlag('a').setLongFlag("alias").setDefault("false").setHelp("display dependencies for an alias rather than a require path"));
		argsParser.registerParameter(new Switch("all").setShortFlag('A').setLongFlag("all").setDefault("false").setHelp("show all ocurrences of a dependency"));
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
		return "dep-insight";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Show inverse dependencies for a given source module.";
	}
	
	@Override
	protected void doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String appName = parsedArgs.getString("app-name");
		String aspectName = parsedArgs.getString("aspect-name");
		String requirePathOrAlias = parsedArgs.getString("require-path");
		boolean isAlias = parsedArgs.getBoolean("alias");
		boolean showAllDependencies = parsedArgs.getBoolean("all");
		
		App app = brjs.app(appName);
		Aspect aspect = app.aspect(aspectName);
		
		if(!app.dirExists()) throw new NodeDoesNotExistException(app, this);
		if(!aspect.dirExists()) throw new NodeDoesNotExistException(aspect, this);
		
		try {
			if(isAlias) {
				out.println(DependencyGraphReportBuilder.createReportForAlias(aspect, requirePathOrAlias, showAllDependencies));
			}
			else {
				out.println(DependencyGraphReportBuilder.createReport(aspect, requirePathOrAlias, showAllDependencies));
			}
		}
		catch (ModelOperationException e) {
			throw new CommandOperationException(e);
		}
	}
}
