package org.bladerunnerjs.testing.specutility.engine;

import java.util.List;

import org.bladerunnerjs.model.BundlableNode;
import org.bladerunnerjs.model.RequestMode;
import org.bladerunnerjs.model.exception.ModelOperationException;
import org.bladerunnerjs.model.exception.request.ContentProcessingException;
import org.bladerunnerjs.plugin.ContentPlugin;


public class ContentPluginCommander
{

	CommanderChainer commanderChainer;
	SpecTest specTest;
	private ContentPlugin contentPlugin;

	public ContentPluginCommander(SpecTest specTest, ContentPlugin contentPlugin)
	{
		this.specTest = specTest;
		commanderChainer = new CommanderChainer(specTest);
		this.contentPlugin = contentPlugin;
	}

	public CommanderChainer getPossibleDevRequests(BundlableNode bundlableNode, List<String> requestsList) throws ContentProcessingException, ModelOperationException
	{
		requestsList.addAll( contentPlugin.getValidContentPaths(bundlableNode.getBundleSet(), RequestMode.Dev) );
		return commanderChainer;
	}

	public CommanderChainer getPossibleProdRequests(BundlableNode bundlableNode, List<String> requestsList) throws ContentProcessingException, ModelOperationException
	{
		requestsList.addAll( contentPlugin.getValidContentPaths(bundlableNode.getBundleSet(), RequestMode.Prod) );
		return commanderChainer;
	}
	
}
