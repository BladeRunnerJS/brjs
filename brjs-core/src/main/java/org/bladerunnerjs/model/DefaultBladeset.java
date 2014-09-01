package org.bladerunnerjs.model;

import java.io.File;
import java.util.Collections;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;


public class DefaultBladeset extends Bladeset
{
	private static final String BLADES_DIRNAME = "blades";
	
	private final NodeList<Blade> blades = new NodeList<>(this, Blade.class, BLADES_DIRNAME, null);
	private final List<TypedTestPack> testTypes = Collections.emptyList(); // needed to prevent 'discoverAllChildren' from using the superclass NodeList
	
	public DefaultBladeset(RootNode rootNode, Node parent, File dir)
	{
		super(rootNode, parent, dir, "default");
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		file(BLADES_DIRNAME).mkdir();
	}
	
	@Override
	public void create() throws InvalidNameException, ModelUpdateException
	{
		super.createDefaultNode();
	}
	
	@Override
	public String requirePrefix() {
		App app = parent();
		return app.getRequirePrefix();
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return testTypes;
	}
	
	public List<Blade> blades()
	{
		return blades.list();
	}
	
	public Blade blade(String bladeName)
	{
		return blades.item(bladeName);
	}

	public boolean exists()
	{
		return file(BLADES_DIRNAME).isDirectory();
	}

	@Override
	public String getTypeName() {
		return getClass().getSuperclass().getSimpleName();
	}
}
