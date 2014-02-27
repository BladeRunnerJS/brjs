package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;

public final class Bladeset extends AbstractComponent implements NamedNode
{
	private final NodeMap<Blade> blades;
	private String name;

	public Bladeset(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		blades = Blade.createNodeSet(rootNode);
		
		registerInitializedNode();
	}
	
	public static NodeMap<Bladeset> createNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, Bladeset.class, null, "-bladeset$");
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		transformations.put("bladeset", getName());
	}
	
	@Override
	public String getName()
	{
		return name;
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name) && NameValidator.isValidPackageName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
		NameValidator.assertValidPackageName(this, name);
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		super.populate();
		testType("unit").testTech("js-test-driver").populate();
		theme("standard").populate();
	}
	
	@Override
	public String namespace() {
		App app = parent();
		return app.getNamespace() + "." + getName();
	}
	
	public App parent()
	{
		return (App) parentNode();
	}

	public List<Blade> blades()
	{
		return children(blades);
	}
	
	public Blade blade(String bladeName)
	{
		return child(blades, bladeName);
	}
}
