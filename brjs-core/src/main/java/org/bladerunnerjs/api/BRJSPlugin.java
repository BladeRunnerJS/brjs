package org.bladerunnerjs.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.logging.Logger;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.plugin.Event;
import org.bladerunnerjs.api.plugin.EventObserver;
import org.bladerunnerjs.model.AbstractBRJSNode;
import org.bladerunnerjs.model.SdkJsLib;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.NodeProperties;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.ObserverList;


public class BRJSPlugin extends AbstractBRJSNode implements NamedNode
{

	private String name;
	private Logger logger;
	private final NodeList<SdkJsLib> sdkLibs = new NodeList<>(this, SdkJsLib.class, "libs/javascript", null);

	public BRJSPlugin(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		logger = rootNode.logger(BRJSPlugin.class);
	}

	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{	
	}

	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}

	public List<SdkJsLib> sdkLibs()
	{
		return sdkLibs.list();
	}
	
	public SdkJsLib sdkLib(String libName)
	{
		return sdkLibs.item(libName);
	}
	
	
//	javascript
//	java
//	system apps
//	templates overrides
//
//	jsdoc-toolkit-resources
//
//	release notes?
//	dashboard changes?

	
}
