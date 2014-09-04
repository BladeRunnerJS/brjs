package org.bladerunnerjs.runner;

import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.UnflaggedOption;

public class MultipleArgsTestCommand extends ArgsParsingCommandPlugin {
	protected Logger logger;
	
	@Override
	public void setBRJS(BRJS brjs) {
		logger = brjs.logger(getClass());
	}
	
	@Override
	public String getCommandName() {
		return "multiple-args-command-test";
	}

	@Override
	public String getCommandDescription() {
		return "";
	}
	
	@Override
	public String getCommandHelp() {
		return "";
	}
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {		
		argsParser.registerParameter(new UnflaggedOption("arg1").setRequired(true).setHelp("help for arg1"));
		argsParser.registerParameter(new UnflaggedOption("arg2").setRequired(true).setHelp("help for arg2"));
		argsParser.registerParameter(new UnflaggedOption("arg3").setRequired(true).setHelp("help for arg3"));
		argsParser.registerParameter(new UnflaggedOption("arg4").setRequired(true).setHelp("help for arg4"));
		argsParser.registerParameter(new UnflaggedOption("arg5").setRequired(true).setHelp("help for arg5"));
	}

	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		// TODO Auto-generated method stub
		return 0;
	}
}
