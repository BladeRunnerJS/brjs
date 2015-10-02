package org.bladerunnerjs.api.plugin;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.model.exception.command.ArgumentParsingException;
import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;
import org.bladerunnerjs.api.plugin.base.AbstractCommandPlugin;

import com.google.common.base.Joiner;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;

/**
 * An abstract implementation of {@link CommandPlugin} which uses <a href="http://www.martiansoftware.com/jsap/">JSAP</a>
 * to parse command arguments and provide helpful error messages.
 * 
 * The parser can be configured when @{link {@link #configureArgsParser(JSAP)} is called. 
 * JSAP has a 'fluid' API where methods are chained together to configure a specific parameter. 
 * 
 * To register a parameter:
 * <pre>
 * {@code
 * argsParser.registerParameter(new UnflaggedOption("my-option").setRequired(true).setHelp("a helpful message about this arg"));
 * }
 * </pre>
 * 
 * To set a 'port' flag which is used via the command arguments as either '-port XYZ' or '-p XYZ' use:
 * <pre>
 * {@code 
 *  argsParser.registerParameter(new FlaggedOption("port").setShortFlag('p').setLongFlag("port").setRequired(false).setHelp("the port number"));
 * }
 * </pre>
 * 
 * Command arguments will be automatically parsed and provided when {@link #doCommand(JSAPResult)} is called. Argument values are accessed
 * by name, for example:
 * 
 * <pre>
 * {@code
 * parsedArgs.getString("some-arg");
 * parsedArgs.getBoolean("some-arg");
 * }
 * </pre>
 * 
 * @see <a href="http://www.martiansoftware.com/jsap/doc/">JSAP Manual</a>
 * @see <a href="http://www.martiansoftware.com/jsap/doc/javadoc/">JSAP API docs</a>
 *
 */
public abstract class JSAPArgsParsingCommandPlugin extends AbstractCommandPlugin implements CommandPlugin {
	private final JSAP argsParser = new JSAP();
	
	protected abstract void configureArgsParser(JSAP argsParser) throws JSAPException;
	protected abstract int doCommand(JSAPResult parsedArgs) throws CommandArgumentsException, CommandOperationException;
	
	public JSAPArgsParsingCommandPlugin() {
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
		StringBuilder help = new StringBuilder();
		for (String line : argsParser.getHelp().split("\n")) {
			if (line.length() >= 2 && Character.isWhitespace(line.charAt(0)) && Character.isWhitespace(line.charAt(1))) {
				line = line.substring(2);
			}
			help.append(line+"\n");			
		}
		return help.toString();
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