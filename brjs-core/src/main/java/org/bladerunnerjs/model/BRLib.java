package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRLibManifest;

public class BRLib extends StandardJsLib
{
	
	private final BRLibManifest libManifest;
	
	public BRLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir, name);
		try
		{
			this.libManifest = new BRLibManifest(this);
		}
		catch (ConfigException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String requirePrefix()
	{
		if (!libManifest.manifestExists())
		{
			return super.requirePrefix();			
		}
		
		try
		{
			return libManifest.getRequirePrefix();
		}
		catch (ConfigException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String namespace()
	{
		if (!libManifest.manifestExists())
		{
			return super.namespace();			
		}
		
		try
		{
			return libManifest.getRequirePrefix().replace("/", ".");
		}
		catch (ConfigException e)
		{
			throw new RuntimeException(e);
		}
	}
	
}
