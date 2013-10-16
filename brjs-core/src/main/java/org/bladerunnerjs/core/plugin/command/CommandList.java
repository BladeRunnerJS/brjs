package org.bladerunnerjs.core.plugin.command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.bladerunnerjs.core.plugin.command.core.HelpCommand;
import org.bladerunnerjs.core.plugin.command.core.VersionCommand;
import org.bladerunnerjs.model.BRJS;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;


public class CommandList
{
	private List<CommandPlugin> coreCommands = new ArrayList<>();
	private List<CommandPlugin> pluginCommands;
	
	public CommandList(BRJS brjs, List<CommandPlugin> pluginCommands)
	{
		this.pluginCommands = pluginCommands;
		
		CommandPlugin helpCommand = new HelpCommand();
		CommandPlugin versionCommand = new VersionCommand();
		
		helpCommand.setBRJS(brjs);
		versionCommand.setBRJS(brjs);
		
		coreCommands.add(helpCommand);
		coreCommands.add(versionCommand);
	}
	
	public CommandPlugin lookupTask(String commandName)
	{
		for (CommandPlugin task : getAllTasks())
		{
			if (commandName.equalsIgnoreCase(task.getCommandName()))
			{
				return task;
			}
		}
		return null;
	}

	public void addCommand(CommandPlugin task)
	{
		pluginCommands.add(task);
		sortCommandLists();
	}

	public List<CommandPlugin> getCoreCommands()
	{
		return coreCommands;
	}

	public List<CommandPlugin> getCoreCommands(Predicate<CommandPlugin> commandFilter)
	{
		return new ArrayList<CommandPlugin>( Collections2.filter(getCoreCommands(), commandFilter) );
	}

	public List<CommandPlugin> getPluginCommands()
	{
		return pluginCommands;
	}

	public List<CommandPlugin> getPluginCommands(Predicate<CommandPlugin> commandFilter)
	{
		return new ArrayList<CommandPlugin>( Collections2.filter(getPluginCommands(), commandFilter) );
	}
	
	public int getLongestCommandName()
	{
		int longestCommandName = 0;
		for (CommandPlugin task : getAllTasks())
		{
			longestCommandName = Math.max( longestCommandName, task.getCommandName().length());
		}
		return longestCommandName;
	}
	
	public int getLongestCommandDescription()
	{
		int longestCommandDescription = 0;
		for (CommandPlugin task : getAllTasks())
		{
			longestCommandDescription = Math.max( longestCommandDescription, task.getCommandDescription().length() );
		}
		return longestCommandDescription;
	}

	private List<CommandPlugin> getAllTasks()
	{
		List<CommandPlugin> allTasks = new ArrayList<CommandPlugin>();
		allTasks.addAll(coreCommands);
		allTasks.addAll(pluginCommands);
		return allTasks;
	}
	
	private void sortCommandLists()
	{
		Comparator<CommandPlugin> comparator = new Comparator<CommandPlugin>()
		{
			public int compare(CommandPlugin t1, CommandPlugin t2)
			{
				return t1.getCommandName().compareTo(t2.getCommandName());
			}
		};
		Collections.sort(coreCommands, comparator);
		Collections.sort(pluginCommands, comparator);
	}
	
}
