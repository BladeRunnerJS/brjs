package com.caplin.cutlass.command.analyse;

import java.io.File;

import org.bladerunnerjs.core.console.ConsoleWriter;
import org.bladerunnerjs.core.plugin.command.AbstractCommandPlugin;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.request.BundlerProcessingException;
import com.caplin.cutlass.bundler.js.analyser.CodeAnalyser;
import com.caplin.cutlass.bundler.js.analyser.CodeAnalyserFactory;
import com.caplin.cutlass.bundler.js.analyser.JsonCodeUnitVisitor;
import com.caplin.cutlass.command.LegacyCommandPlugin;

public class DependencyAnalyserCommand extends AbstractCommandPlugin implements LegacyCommandPlugin 
{
	private final BRJS brjs;
	private final ConsoleWriter out;
	
	public DependencyAnalyserCommand(BRJS brjs) 
	{
		this.brjs = brjs;
		out = this.brjs.getConsoleWriter();
	}
	
	@Override
	public void setBRJS(BRJS brjs)
	{	
	}
	
	@Override
	public String getCommandName()
	{
		return "app-dependencies";
	}
	
	@Override
	public String getCommandDescription() 
	{
		return "Provides a dependency analysis of javascript classes for an application aspect.";
	}

	@Override
	public String getCommandUsage() 
	{
		return "<appname> [aspect-name] [format]";
	}

	@Override
	public String getCommandHelp() {
		return getCommandUsage();
	}
	
	@Override
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException 
	{	
		AnalyserConfig config = new AnalyserConfig(args, this);
		File seedFileDir = config.getAspectDirectory();
		CodeAnalyser codeAnalyser = null;
		String result = "";
		

		try 
		{
			codeAnalyser = CodeAnalyserFactory.getCodeAnalyser(seedFileDir);
		} 
		catch (BundlerProcessingException e) 
		{
			throw new CommandOperationException(e);
		}
		
		if(config.getOutputFormatInJson() == true)
		{
			JsonCodeUnitVisitor visitor = new JsonCodeUnitVisitor();
			codeAnalyser.emit(visitor);
			result = visitor.getResult();
		}
		else 
		{
			result = codeAnalyser.emitString();
		}
		
		out.println("Dependency analysis for '" + config.getAspectDirectory().getAbsolutePath() + "':\n");
		out.println(result);
	}
}
