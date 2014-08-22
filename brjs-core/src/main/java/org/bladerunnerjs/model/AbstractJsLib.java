package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.NamespaceUtility;
import org.bladerunnerjs.utility.TestRunner;

public abstract class AbstractJsLib extends AbstractAssetContainer implements JsLib
{
	private String name;
	private Node parent;
	private File[] scopeFiles;
	
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this, TypedTestPack.class);
	private final MemoizedValue<Boolean> isNamespaceEnforcedValue = new MemoizedValue<Boolean>("AbstractJsLib.isNamespaceEnforcedValue", root(), file("no-namespace-enforcement"));
	
	public AbstractJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		this.parent = parent;
	}
	
	public AbstractJsLib(RootNode rootNode, Node parent, File dir)
	{
		// TODO: can we avoid having to have a null name for a NamedNode that is available as a single item through the model
		this(rootNode, parent, dir, null);
	}
	
	@Override
	public File[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			// TODO: perhaps all library objects should be app specific (even when they are only in the sdk) so that libraries can be cached better
			scopeFiles = new File[] {root().dir()};
		}
		
		return scopeFiles;
	}
	
	@Override
	public List<AssetContainer> scopeAssetContainers() {
		return new ArrayList<>(app().jsLibs());
	}
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		transformations.put("lib", StringUtils.capitalize(getName()));
		transformations.put("libns", NamespaceUtility.convertToNamespace(requirePrefix()));
	}
	
	@Override
	public String getName()
	{
		if(name == null){
			 name = dir().getName();
		}
		return name;
	}
	
	@Override
	public boolean isValidName()
	{
		return NameValidator.isValidDirectoryName(name);
	}
	
	@Override
	public void assertValidName() throws InvalidNameException
	{
		NameValidator.assertValidDirectoryName(this);
	}
	
	@Override
	public Node parentNode()
	{
		return parent;
	}
	
	@Override
	public void populate() throws InvalidNameException, ModelUpdateException
	{
		String libNamespace = getName().toLowerCase();
		populate( libNamespace );
	}
	
	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
		if (!dir().exists()) { create(); }

		NameValidator.assertValidRootPackageName(this, libNamespace);
		
		try {
			RootAssetLocation rootAssetLocation = rootAssetLocation();
			if(rootAssetLocation != null) {
				rootAssetLocation.setRequirePrefix(libNamespace.replace('.', '/'));
				rootAssetLocation.populate();
			}
			
		}
		catch (ConfigException e) {
			if(e.getCause() instanceof InvalidNameException) {
				throw (InvalidNameException) e.getCause();
			}
			else {
				throw new ModelUpdateException(e);
			}
		}
	}
	
	@Override
	public String requirePrefix() {
		RootAssetLocation rootAssetLocation = rootAssetLocation();
		return (rootAssetLocation != null) ? rootAssetLocation.requirePrefix() : getName();
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return isNamespaceEnforcedValue.value(() -> {
			// secret mechanism for CaplinTrader, to aid with backwards compatibility
			return (file("no-namespace-enforcement").exists()) ? false : true;
		});
	}
	
	@Override
	public String getTemplateName()
	{
		return "jslib";
	}
	
	@Override
	public void runTests(TestType... testTypes)
	{
		TestRunner.runTests(testTypes);
	}
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return testTypes.list();
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return testTypes.item(testTypeName);
	}
}
