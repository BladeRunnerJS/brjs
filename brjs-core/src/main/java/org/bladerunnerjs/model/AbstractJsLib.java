package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.memoization.MemoizedValue;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.RequirePathException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TestRunner;

public abstract class AbstractJsLib extends AbstractAssetContainer implements JsLib
{
	private String name;
	private Node parent;
	private File[] scopeFiles;
	
	private final MemoizedValue<Boolean> isNamespaceEnforcedValue = new MemoizedValue<Boolean>("AbstractJsLib.isNamespaceEnforcedValue", root(), file("no-namespace-enforcement"));
	
	public AbstractJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		this.parent = parent;
		
		// TODO: we should never call registerInitializedNode() from a non-final class
		registerInitializedNode();
	}
	
	public AbstractJsLib(RootNode rootNode, Node parent, File dir)
	{
		// TODO: can we avoid having to have a null name for a NamedNode that is available as a single item through the model
		this(rootNode, parent, dir, null);
	}
	
	@Override
	public File[] scopeFiles() {
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
		transformations.put("libns", namespace());
	}
	
	@Override
	public String getName()
	{
		// TODO: remove this 'caplin' to 'br' hack once all the code is using the new model
		return (name != null) ? name : dir().getName().replaceAll("^caplin$", "br");
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
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
		NameValidator.assertValidRootPackageName(this, libNamespace);
		
		try {
			create();
			
			RootAssetLocation rootAssetLocation = rootAssetLocation();
			if(rootAssetLocation != null) {
				rootAssetLocation().setNamespace(libNamespace);
			}
			
			BRJSNodeHelper.populate(this, true);
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
		try {
			RootAssetLocation rootAssetLocation = rootAssetLocation();
			return (rootAssetLocation != null) ? rootAssetLocation().requirePrefix() : getName();
		}
		catch(RequirePathException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public String namespace() {
		try {
			RootAssetLocation rootAssetLocation = rootAssetLocation();
			return (rootAssetLocation != null) ? rootAssetLocation().namespace() : requirePrefix().replace("/", ".");
		}
		catch(RequirePathException e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public boolean isNamespaceEnforced() {
		return isNamespaceEnforcedValue.value(() -> {
			// secret mechanism for CaplinTrader, to aid with backwards compatibility
			return (file("no-namespace-enforcement").exists()) ? false : true;
		});
	}
	
	@Override
	public String toString()
	{
		return super.toString()+" - "+getName();
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
}
