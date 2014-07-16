package org.bladerunnerjs.plugin.utility.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bladerunnerjs.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.CommandPlugin;
import org.bladerunnerjs.plugin.base.AbstractCommandPlugin;

import com.google.common.base.Joiner;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

public abstract class ArgsParsingCommandPlugin extends AbstractCommandPlugin implements CommandPlugin {
	private final JSAP argsParser = new JSAP();
	
	protected abstract void configureArgsParser(JSAP argsParser) throws JSAPException;
	protected abstract int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException;
	
	public ArgsParsingCommandPlugin() {
		try {
			configureArgsParser(argsParser);
		} catch (JSAPException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public final String getCommandUsage() {
		return argsParser.getUsage();
	}
	
	@Override
	public String getCommandHelp() {
		return argsParser.getHelp();
	}
	
	@Override
	public final int doCommand(String... args) throws CommandArgumentsException, CommandOperationException {
		JSAPResult parsedArgs = parseArgs(args);
		return doCommand(parsedArgs);
	}
	
	private JSAPResult parseArgs(String[] args) throws ArgumentParsingException {
		JSAPResult parsedArgs = argsParser.parse(args);
		
		if(!parsedArgs.success()) {
			List<String> errorMessages = new ArrayList<>();
			Iterator<?> paramsIterator = parsedArgs.getBadParameterIDIterator();
			
			while(paramsIterator.hasNext()) {
				String paramName = (String) paramsIterator.next();
				errorMessages.add(parsedArgs.getException(paramName).getMessage());
			}
			
			throw new ArgumentParsingException(Joiner.on("\n  ").join(errorMessages), this);
		}
		
		return parsedArgs;
	}
}