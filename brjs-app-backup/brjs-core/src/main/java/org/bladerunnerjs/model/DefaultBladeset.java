package org.bladerunnerjs.model;

import java.util.Collections;
import java.util.List;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.api.App;
import org.bladerunnerjs.api.Blade;
import org.bladerunnerjs.api.Bladeset;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;


public class DefaultBladeset extends Bladeset
{
	private final NodeList<Blade> blades = new NodeList<>(this, Blade.class, BLADES_DIRNAME, null);
	private final List<TypedTestPack> testTypes = Collections.emptyList(); // needed to prevent 'discoverAllChildren' from using the superclass NodeList
	
	public DefaultBladeset(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		super(rootNode, parent, dir, "default");
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException
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
