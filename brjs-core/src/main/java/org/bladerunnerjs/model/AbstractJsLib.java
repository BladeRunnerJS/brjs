package org.bladerunnerjs.model;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.apache.commons.lang3.StringUtils;
import org.bladerunnerjs.api.JsLib;
import org.bladerunnerjs.api.TestType;
import org.bladerunnerjs.api.ThirdpartyLibManifest;
import org.bladerunnerjs.api.TypedTestPack;
import org.bladerunnerjs.api.memoization.MemoizedFile;
import org.bladerunnerjs.api.memoization.MemoizedValue;
import org.bladerunnerjs.api.model.exception.ConfigException;
import org.bladerunnerjs.api.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.api.model.exception.template.TemplateInstallationException;
import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeList;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TemplateUtility;
import org.bladerunnerjs.utility.TestRunner;

public abstract class AbstractJsLib extends AbstractAssetContainer implements JsLib
{
	private String name;
	private Node parent;
	private MemoizedFile[] scopeFiles;
	
	private final NodeList<TypedTestPack> testTypes = TypedTestPack.createNodeSet(this, TypedTestPack.class);
	private final MemoizedValue<Boolean> isNamespaceEnforcedValue = new MemoizedValue<Boolean>("AbstractJsLib.isNamespaceEnforcedValue", root(), file("no-namespace-enforcement"));
	
	//TODO: this is bad, it should be a plugin concern. Move this class into the plugins project and remove all BRLibConf code in this class
	private BRLibConf brLibConf;
	private ThirdpartyLibManifest thirdpartyManifest;
	
	public AbstractJsLib(RootNode rootNode, Node parent, MemoizedFile dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		this.parent = parent;
		try
		{
			brLibConf = new BRLibConf(this);
			thirdpartyManifest = new ThirdpartyLibManifest(this);
		}
		catch (ConfigException ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public AbstractJsLib(RootNode rootNode, Node parent, MemoizedFile dir)
	{
		// TODO: can we avoid having to have a null name for a NamedNode that is available as a single item through the model
		this(rootNode, parent, dir, null);
	}
	
	@Override
	public MemoizedFile[] memoizedScopeFiles() {
		if(scopeFiles == null) {
			// TODO: perhaps all library objects should be app specific (even when they are only in the sdk) so that libraries can be cached better
			scopeFiles = new MemoizedFile[] {root().dir()};
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
		transformations.put("libns", requirePrefix().replace("/", "."));
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
	public void populate(String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		String libNamespace = getName().toLowerCase();
		populate(libNamespace, templateGroup);
	}
	
	@Override
	public void populate(String libNamespace, String templateGroup) throws InvalidNameException, ModelUpdateException, TemplateInstallationException
	{
		if (!dir().exists()) { create(); }

		NameValidator.assertValidRootPackageName(this, libNamespace);
		
		try {
			if (thirdpartyManifest.exists()) {
				thirdpartyManifest.write();				
			} else {
				brLibConf.setRequirePrefix( libNamespace.replace('.', '/') );
				brLibConf.write();
			}
			BRJSNodeHelper.populate(this, templateGroup, true);
			incrementChildFileVersions();
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
		if (brLibConf.manifestExists()) {
			try
			{
				return brLibConf.getRequirePrefix();
			}
			catch (ConfigException ex)
			{
				throw new RuntimeException(ex);
			}
		}
		return dir().getName();
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
		return (thirdpartyManifest.exists()) ? "thirdparty-lib" : "br-lib";
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
