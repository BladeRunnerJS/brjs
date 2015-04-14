package org.bladerunnerjs.plugin.commands.standard;

import java.io.File;

import org.bladerunnerjs.api.BRJS;
import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.model.exception.InvalidBundlableNodeException;
import org.bladerunnerjs.api.model.exception.ModelOperationException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.model.exception.command.DirectoryDoesNotExistCommandException;
import org.bladerunnerjs.api.plugin.JSAPArgsParsingCommandPlugin;
import org.bladerunnerjs.api.BundlableNode;
import org.bladerunnerjs.utility.deps.DependencyGraphReportBuilder;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnflaggedOption;


public class BundleDepsCommand extends JSAPArgsParsingCommandPlugin
{
	private BRJS brjs;
	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		argsParser.registerParameter(new UnflaggedOption("bundle-dir").setRequired(true).setHelp("the bundle directory to show dependencies for"));
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
		return "bundle-deps";
	}
	
	@Override
	public String getCommandDescription()
	{
		return "Show dependencies for a given bundlable directory.";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		String bundleDir = parsedArgs.getString("bundle-dir");
		boolean showAllDependencies = parsedArgs.getBoolean("all");
		File bundlableDir = brjs.file("sdk/" + bundleDir);
		String relativePath = brjs.file("sdk").toPath().relativize(bundlableDir.toPath()).toString().replace("\\", "/");
		
		if(!bundlableDir.exists()) throw new DirectoryDoesNotExistCommandException(relativePath, this);
		
		
		try {
			BundlableNode bundlableNode = brjs.locateFirstBundlableAncestorNode(bundlableDir);
			logger.println(DependencyGraphReportBuilder.createReport(bundlableNode, showAllDependencies));
		}
		catch (ModelOperationException e) {
			throw new CommandOperationException(e);
		}
		catch (InvalidBundlableNodeException e) {
			throw new CommandArgumentsException(e, this);
		}
		return 0;
	}
}
