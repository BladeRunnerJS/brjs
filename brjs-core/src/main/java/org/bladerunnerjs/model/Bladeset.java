package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.bladerunnerjs.model.engine.NamedNode;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;

public class Bladeset extends AbstractComponent implements NamedNode
{
	private final NodeList<Blade> blades = new NodeList<>(this, Blade.class, "blades", null);
	private String name;
	private File[] scopeFiles;
	
	public Bladeset(RootNode rootNode, Node parent, File dir) {
		this(rootNode, parent, dir, StringUtils.substringBeforeLast(dir.getName(), "-bladeset"));
	}
	
	public Bladeset(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
	}
	
	@Override
	public File[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			scopeFiles = new File[] {dir(), app().libsDir(), app().libsDir(), root().sdkJsLibsDir().dir(), BladerunnerConf.getConfigFilePath(root()), app().file("app.conf")};
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
		transformations.put("bladeset", getName());
		transformations.put("bladesetTitle", WordUtils.capitalize(getName()) );
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getTypeName() {
		return this.getClass().getSimpleName();
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
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		super.populate();
		testType("unit").testTech("js-test-driver").populate();
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
}
