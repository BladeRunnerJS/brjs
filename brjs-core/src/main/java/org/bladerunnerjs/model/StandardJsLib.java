package org.bladerunnerjs.model;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.naming.InvalidNameException;

import org.bladerunnerjs.model.engine.Node;
import org.bladerunnerjs.model.engine.NodeMap;
import org.bladerunnerjs.model.engine.RootNode;
import org.bladerunnerjs.model.exception.ConfigException;
import org.bladerunnerjs.model.exception.modelupdate.ModelUpdateException;
import org.bladerunnerjs.utility.NameValidator;
import org.bladerunnerjs.utility.TestRunner;

public class StandardJsLib extends AbstractAssetContainer implements JsLib
{
	private String name;
	private JsLibConf libConf;
	private Node parent;
	private final NodeMap<TypedTestPack> testTypes;
	
	public StandardJsLib(RootNode rootNode, Node parent, File dir, String name)
	{
		super(rootNode, parent, dir);
		this.name = name;
		this.parent = parent;
		testTypes = TypedTestPack.createNodeSet(rootNode);
	}
	
	public StandardJsLib(RootNode rootNode, Node parent, File dir)
	{
		// TODO: can we avoid having to have a null name for a NamedNode that is available as a single item through the model
		this(rootNode, parent, dir, null);
	}
	
	public static NodeMap<StandardJsLib> createAppNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, StandardJsLib.class, "libs", null);
	}
	
	public static NodeMap<StandardJsLib> createSdkNonBladeRunnerLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, StandardJsLib.class, "sdk/libs/javascript/thirdparty", null);
	}
	
	public static NodeMap<StandardJsLib> createAppNonBladeRunnerLibNodeSet(RootNode rootNode)
	{
		return new NodeMap<>(rootNode, StandardJsLib.class, "thirdparty-libraries", null);
	}
	
	
	@Override
	public void addTemplateTransformations(Map<String, String> transformations) throws ModelUpdateException
	{
		try {
			transformations.put("libns", libConf().getLibNamespace());
		}
		catch(ConfigException e) {
			throw new ModelUpdateException(e);
		}
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
	public App getApp()
	{
		if (parent == root())
		{
			return root().systemApp("SDK");			
		}
		return super.getApp();
	}
	
	@Override
	public JsLibConf libConf() throws ConfigException
	{
		if(libConf == null) {
			libConf = new JsLibConf(this);
		}
		
		return libConf ;
	}
	
	@Override
	public void populate(String libNamespace) throws InvalidNameException, ModelUpdateException
	{
		NameValidator.assertValidRootPackageName(this, libNamespace);
		
		try {
			libConf().setLibNamespace(libNamespace);
			populate();
			libConf().write();
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
	public String namespace() {
		return getName();
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
	
	@Override
	public List<TypedTestPack> testTypes()
	{
		return children(testTypes);
	}
	
	@Override
	public TypedTestPack testType(String testTypeName)
	{
		return child(testTypes, testTypeName);
	}
	
}
