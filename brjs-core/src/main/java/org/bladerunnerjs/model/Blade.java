package org.bladerunnerjs.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.memoization.MemoizedFile;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.utility.ImplicitTemplateHandlerUtility;
import org.bladerunnerjs.utility.NameValidator;


public final class Blade extends AbstractComponent implements NamedNode
{
	private final NodeItem<Workbench> workbench = new NodeItem<>(this, Workbench.class, "workbench");
	private final String name;
	private final List<AssetContainer> bladeAssetContainers;
	
	public Blade(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		bladeAssetContainers = new ArrayList<>();
		bladeAssetContainers.add(this);
		bladeAssetContainers.add((Bladeset) parent);
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		return parent().memoizedScopeFiles();
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		List<AssetContainer> scopeAssetContainers = new ArrayList<>(bladeAssetContainers);
		scopeAssetContainers.addAll(app().jsLibs());
		
		return scopeAssetContainers;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		transformations.put("bladeRequirePrefix", requirePrefix());
		transformations.put("bladeNamespace", requirePrefix().replace("/", "."));
		transformations.put("blade", getName());
		transformations.put("bladeTitle", WordUtils.capitalize(getName()) );
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
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		super.populate(templateGroup);
		
		ImplicitTemplateHandlerUtility templateUtility = new ImplicitTemplateHandlerUtility();
		templateUtility.populateOrCreate(testType("unit").defaultTestTech(), templateGroup);
		templateUtility.populateOrCreate(testType("acceptance").defaultTestTech(), templateGroup);
		templateUtility.populateOrCreate(workbench(), templateGroup);
	}
	
	@Override
	public String requirePrefix() {
		return parent().requirePrefix() + "/" + getName();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return true;
	}
	
	public Bladeset parent()
	{
		return (Bladeset) parentNode();
	}
	
	public Workbench workbench()
	{
		return workbench.item();
	}
}
