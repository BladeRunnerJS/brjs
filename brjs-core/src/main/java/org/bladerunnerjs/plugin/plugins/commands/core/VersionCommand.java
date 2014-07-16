package org.bladerunnerjs.plugin.plugins.commands.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.IOUtils;
import org.bladerunnerjs.logging.Logger;
import org.bladerunnerjs.model.BRJS;
import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;

import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;


public class VersionCommand extends ArgsParsingCommandPlugin
{
	// Note: ASCII art created using http://patorjk.com/software/taag/#p=display&h=1&f=Ivrit&t=%20%20%20%20BladeRunnerJS%20%20%20%20
	private static final String ASCII_ART_RESOURCE_PATH = "org/bladerunnerjs/core/plugin/command/core/bladerunner-ascii-art.txt";
	
	private BRJS brjs;

	private Logger logger;
	
	@Override
	protected void configureArgsParser(JSAP argsParser) throws JSAPException {
		// do nothing
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
		return "version";
	}

	@Override
	public String getCommandDescription()
	{
		return "Displays the BladeRunnerJS version";
	}
	
	@Override
	protected int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException {
		try (InputStream asciiArtInputStream = getClass().getClassLoader().getResourceAsStream( ASCII_ART_RESOURCE_PATH )) {
			StringWriter asciiArtWriter = new StringWriter();
			IOUtils.copy(asciiArtInputStream, asciiArtWriter, "UTF-8");
			
			logger.println( brjs.versionInfo().toString() );
			logger.println("");
			logger.println(asciiArtWriter.toString());
			logger.println("");
		}
		catch(IOException e) {
			throw new CommandOperationException("error reading ascii art resource file", e);
		}
		
		return 0;
	}
}
