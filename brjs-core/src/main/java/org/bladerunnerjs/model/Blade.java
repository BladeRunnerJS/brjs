package org.bladerunnerjs.model;

import java.io.File;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;


public class Blade extends AbstractComponent implements NamedNode
{
	private final NodeItem<Workbench> workbench = new NodeItem<>(Workbench.class, "workbench");
	private String name;

	public Blade(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	public static NodeMap<Blade> createNodeSet()
	{
		return new NodeMap<>(Blade.class, "blades", null);
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		transformations.put("blade", getName());
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
		workbench().populate();
	}
	
	@Override
	public String namespace() {
		Bladeset bladeset = parent();
		App app = bladeset.parent();
		// TODO: why not use bladeset.getNamespace()
		return app.getNamespace() + "." + bladeset.getName() + "." + getName();
	}
	
	public Bladeset parent()
	{
		return (Bladeset) parentNode();
	}
	
	public Workbench workbench()
	{
		return item(workbench);
	}
}
