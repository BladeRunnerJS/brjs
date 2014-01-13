package org.bladerunnerjs.plugin;

import org.bladerunnerjs.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.model.exception.command.CommandOperationException;
import org.bladerunnerjs.plugin.utility.command.ArgsParsingCommandPlugin;


/**
 * Command plug-ins allow new commands to be made available to users of BladeRunnerJS.
 * 
 * <p>The main advantage to creating a command plug-in rather than providing an external command or script is that command plug-ins get access to the
 * BladeRunnerJS model. Commands can be invoked by users using the 'brjs' command, or can be invoked programmatically using the
 * {@link org.bladerunnerjs.model.BRJS#runCommand} method. An {@link ArgsParsingCommandPlugin} class is available for developers that would like help
 * parsing the command parameters for their command.</p>
 * 
 * <p>The following methods are <i>identifier-methods</i>, and may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()}
 * has been invoked:</p>
 * 
 * <ul>
 *   <li>{@link #getCommandName}</li>
 * </ul>
 */
public interface CommandPlugin extends Plugin
{
	/**
	 * Returns the name of the command.
	 * 
	 * <p><b>Note:</b> Developers should not rely on any class initialization performed within {@link Plugin#setBRJS Plugin.setBRJS()} as this
	 * method is an <i>identifier-method</i> which may be invoked before {@link Plugin#setBRJS Plugin.setBRJS()} has itself been
	 * invoked.</p>
	 */
	public String getCommandName();
	
	/**
	 * Returns a description message that helps users to determine whether a command may be of interest to them or not.
	 */
	public String getCommandDescription();
	
	/**
	 * Returns a usage message that shows the user an example of how the command parameters are used.
	 * 
	 * <p><b>Note:</b> This method doesn't need to be implemented if you've chosen to extend {@link ArgsParsingCommandPlugin}.</p>
	 */
	public String getCommandUsage();
	
	/**
	 * Returns a detailed help message that describes the various parameters the command provides, and how they are used.
	 * 
	 * <p><b>Note:</b> This method doesn't need to be implemented if you've chosen to extend {@link ArgsParsingCommandPlugin}.</p>
	 */
	public String getCommandHelp();
	
	/**
	 * Runs the command using the provided user arguments.
	 * 
	 * @param args The list of arguments provided by the user.
	 * 
	 * @throws CommandArgumentsException if any invalid arguments were provided.
	 * @throws CommandOperationException if a problem was encountered while running the command.
	 */
	public void doCommand(String[] args) throws CommandArgumentsException, CommandOperationException;
}
