package org.bladerunnerjs.api.plugin;

import org.bladerunnerjs.api.model.exception.command.CommandArgumentsException;
import org.bladerunnerjs.api.model.exception.command.CommandOperationException;


/**
 * Command plug-ins allow new commands to be made available to users of BladeRunnerJS.
 * 
 * <p>The main advantage to creating a command plug-in rather than providing an external command or script is that command plug-ins get access to the
 * BladeRunnerJS model. Commands can be invoked by users using the 'brjs' command, or can be invoked programmatically using the
 * {@link org.bladerunnerjs.api.BRJS#runCommand} method. 
 * 
 * A {@link JSAPArgsParsingCommandPlugin} class is available for developers that would like help
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
	 * 
	 * @return The name of the command
	 */
	public String getCommandName();
	
	/**
	 * Returns a description message that helps users to determine whether a command may be of interest to them or not.
	 * 
	 * @return The command description
	 */
	public String getCommandDescription();
	
	/**
	 * Returns a usage message that shows the user an example of how the command parameters are used.
	 * 
	 * <p><b>Note:</b> This method doesn't need to be implemented if you've chosen to extend {@link JSAPArgsParsingCommandPlugin}.</p>
	 * 
	 * @return A string detailing the usage of the command
	 */
	public String getCommandUsage();
	
	/**
	 * Returns a detailed help message that describes the various parameters the command provides, and how they are used.
	 * 
	 * <p><b>Note:</b> This method doesn't need to be implemented if you've chosen to extend {@link JSAPArgsParsingCommandPlugin}.</p>
	 * 
	 * @return A help message to be displayed if the command is used incorrectly
	 */
	public String getCommandHelp();
	
	/**
	 * Runs the command using the provided user arguments.
	 * 
	 * @param args The list of arguments provided by the user.
	 * 
	 * @return The exit code. 0 on success, non 0 otherwise. Typically commands will return 1 on failure unless
	 * they have multiple exit paths. The exit code is not a replacement for exceptions, exceptions should still
	 * be thrown if arguments are invalid or an unexpected exception occurs during the execution of the command.
	 * 
	 * @throws CommandArgumentsException if any invalid arguments were provided.
	 * @throws CommandOperationException if a problem was encountered while running the command.
	 * 
	 */
	public int doCommand(String... args) throws CommandArgumentsException, CommandOperationException;
}
