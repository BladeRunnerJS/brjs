package org.bladerunnerjs.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.AbstractComponent;
import org.bladerunnerjs.model.AssetContainer;
import org.bladerunnerjs.model.BladesetWorkbench;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeItem;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.TemplateUtility;
import org.bladerunnerjs.utility.NameValidator;

public class Bladeset extends AbstractComponent implements NamedNode
{
	public static final String BLADES_DIRNAME = "blades";

	private final NodeItem<BladesetWorkbench> workbench = new NodeItem<>(this, BladesetWorkbench.class, "workbench");
	private final NodeList<Blade> blades = new NodeList<>(this, Blade.class, "blades", null);
	private String name;
	private MemoizedFile[] scopeFiles;
	
	public Bladeset(RootNode rootNode, Node parent, MemoizedFile dir) {
		this(rootNode, parent, dir, StringUtils.substringBeforeLast(dir.getName(), "-bladeset"));
	}
	
	public Bladeset(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new MemoizedFile[] {dir(), app().libsDir(), app().libsDir(), root().sdkJsLibsDir().dir()};
		}
		
		return scopeFiles;
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		List<AssetContainer> scopeAssetContainers = new ArrayList<>();
		scopeAssetContainers.add(this);
		scopeAssetContainers.addAll(app().jsLibs());
		
		return scopeAssetContainers;
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		transformations.put("bladesetRequirePrefix", requirePrefix());
		transformations.put("bladesetNamespace", requirePrefix().replace("/", "."));
		transformations.put("bladeset", getName());
		transformations.put("bladesetTitle", WordUtils.capitalize(getName()) );
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
		if (!name.equals("default")) { // 'default' is a valid bladeset name since its the implicit bladeset name for the optional bladeset
			NameValidator.assertValidPackageName(this, name);
		}
	}
	
	@Override
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		super.populate(templateGroup);
		
		TemplateUtility.populateOrCreate(testType("unit").defaultTestTech(), templateGroup);
		TemplateUtility.populateOrCreate(workbench(), templateGroup);
	}
	
	@Override
	public String requirePrefix() {
		App app = parent();
		return app.getRequirePrefix() + "/" + getName();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return true;
	}
	
	public App parent()
	{
		return (App) parentNode();
	}

	public List<Blade> blades()
	{
		return blades.list();
	}
	
	public Blade blade(String bladeName)
	{
		return blades.item(bladeName);
	}
	
	public BladesetWorkbench workbench()
	{
		return workbench.item();
	}
}
