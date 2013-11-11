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
import org.bladerunnerjs.model.utility.NameValidator;

public class Bladeset extends AbstractComponent implements NamedNode
{
	private final NodeMap<Blade> blades = Blade.createNodeSet();
	private String name;

	public Bladeset(RootNode rootNode, Node parent, File dir, String name)
	{
		super(dir);
		this.name = name;
		init(rootNode, parent, dir);
	}
	
	public static NodeMap<Bladeset> createNodeSet()
	{
		return new NodeMap<>(Bladeset.class, null, "-bladeset$");
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
	public String getRequirePrefix() {
		App app = parent();
		return "/" + app.getNamespace() + "/" + getName();
	}
	
	public App parent()
	{
		return (App) parent;
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
