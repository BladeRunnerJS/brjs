package org.bladerunnerjs.model;

import java.io.File;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.plugin.plugins.brjsconformant.BRLibManifest;

public class BRSdkLib extends StandardJsLib
{
	
	private final BRLibManifest libManifest;
	
	public BRSdkLib(RootNode rootNode, Node parent, File dir, String name)
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
	
	public static NodeMap<BRSdkLib> createSdkLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, BRSdkLib.class, "sdk/libs/javascript/br-libs", null);
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
	
}
